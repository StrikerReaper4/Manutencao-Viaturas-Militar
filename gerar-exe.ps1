# Gera o executavel do MVM para Windows (pasta dist\MVM com o MVM.exe e o Java embutido).
#
# Uso (no PowerShell, na pasta do projeto):
#     powershell -ExecutionPolicy Bypass -File .\gerar-exe.ps1
#
# Precisa so do JDK 17 ou mais novo (o jpackage vem junto). Nao precisa de Maven.
# Resultado:
#     dist\MVM\MVM.exe       -> o programa (a pasta MVM inteira vai junto, nao so o .exe)
#     dist\MVM-<versao>.zip  -> a mesma pasta zipada, pronta pra mandar pro usuario
#
# Pra lancar uma versao nova: mude a <version> no pom.xml e rode o script de novo.
# O banco de cada usuario fica em %APPDATA%\MVM\mvm.db, fora da pasta do programa,
# entao trocar a pasta MVM pela nova nao apaga os dados.

$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot   # caminhos relativos evitam problema com acento no nome das pastas

# --- 1. Acha o JDK (precisa do javac, jar e jpackage) ---
function Find-Jdk {
    $candidatos = @()
    if ($env:JAVA_HOME) { $candidatos += $env:JAVA_HOME }
    $cmd = Get-Command jpackage -ErrorAction SilentlyContinue
    if ($cmd) { $candidatos += (Split-Path (Split-Path $cmd.Source)) }
    foreach ($base in @("$env:ProgramFiles\Eclipse Adoptium", "$env:ProgramFiles\Java", "$env:ProgramFiles\Microsoft", "$env:USERPROFILE\.jdks")) {
        if (Test-Path $base) {
            $candidatos += Get-ChildItem $base -Directory | Where-Object { $_.Name -match 'jdk' } |
                Sort-Object Name -Descending | ForEach-Object { $_.FullName }
        }
    }
    foreach ($c in $candidatos) {
        if (Test-Path (Join-Path $c 'bin\jpackage.exe')) { return $c }
    }
    throw 'JDK 17+ nao encontrado. Instale um JDK (ex.: Eclipse Temurin 17) ou defina JAVA_HOME.'
}
$jdk = Find-Jdk
$bin = Join-Path $jdk 'bin'
Write-Host "JDK: $jdk"

# --- 2. Le a versao do programa e a do sqlite-jdbc no pom.xml ---
[xml]$pom = Get-Content 'pom.xml' -Encoding UTF8
$versao = $pom.project.version
$versaoSqlite = ($pom.project.dependencies.dependency | Where-Object { $_.artifactId -eq 'sqlite-jdbc' }).version
Write-Host "Versao do MVM: $versao  |  sqlite-jdbc: $versaoSqlite"

# --- 3. Pega o jar do SQLite (do cache do Maven ou baixa do Maven Central) ---
$nomeSqlite = "sqlite-jdbc-$versaoSqlite.jar"
$sqlite = Join-Path $env:USERPROFILE ".m2\repository\org\xerial\sqlite-jdbc\$versaoSqlite\$nomeSqlite"
if (-not (Test-Path $sqlite)) {
    New-Item -ItemType Directory -Force 'build\lib' | Out-Null
    $sqlite = "build\lib\$nomeSqlite"
    if (-not (Test-Path $sqlite)) {
        $url = "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/$versaoSqlite/$nomeSqlite"
        Write-Host "Baixando $url"
        Invoke-WebRequest -Uri $url -OutFile $sqlite -UseBasicParsing
    }
}

# --- 4. Limpa e compila ---
foreach ($p in @('build\classes', 'build\input', 'dist')) {
    if (Test-Path $p) { Remove-Item -Recurse -Force $p }
}
New-Item -ItemType Directory -Force 'build\classes', 'build\input', 'dist' | Out-Null

$fontes = Get-ChildItem 'src\main\java' -Recurse -Filter *.java | Resolve-Path -Relative
Set-Content 'build\fontes.txt' ($fontes | ForEach-Object { '"' + ($_ -replace '\\', '/') + '"' }) -Encoding ASCII
& "$bin\javac.exe" --release 17 -encoding UTF-8 -cp $sqlite -d 'build\classes' '@build\fontes.txt'
if ($LASTEXITCODE -ne 0) { throw 'Erro na compilacao.' }
Copy-Item 'src\main\resources\*' 'build\classes' -Recurse -Force   # scripts SQL do banco

# --- 5. Monta o mvm.jar (o sqlite-jdbc vai ao lado, ligado pelo Class-Path) ---
Set-Content 'build\manifest.txt' "Main-Class: br.ufms.mvm.Main`r`nClass-Path: $nomeSqlite`r`n" -Encoding ASCII
& "$bin\jar.exe" --create --file 'build\input\mvm.jar' --manifest 'build\manifest.txt' -C 'build\classes' .
if ($LASTEXITCODE -ne 0) { throw 'Erro ao montar o mvm.jar.' }
Copy-Item $sqlite 'build\input'

# --- 6. Gera a pasta com o MVM.exe e um Java reduzido embutido ---
& "$bin\jpackage.exe" --type app-image --name MVM --app-version $versao `
    --input 'build\input' --main-jar mvm.jar --main-class br.ufms.mvm.Main `
    --add-modules 'java.base,java.desktop,java.sql,java.logging' `
    --java-options '-Dfile.encoding=UTF-8' `
    --vendor 'Grupo MVM - UFMS' --dest 'dist'
if ($LASTEXITCODE -ne 0) { throw 'Erro no jpackage.' }

# --- 7. Confere se o executavel funciona: roda "MVM.exe --verificar" com um AppData temporario,
#        assim ele cria um banco novo do zero sem mexer no banco de quem esta gerando ---
$tmp = Join-Path $env:TEMP ("mvm-verificacao-" + [guid]::NewGuid())
New-Item -ItemType Directory $tmp | Out-Null
$appDataOriginal = $env:APPDATA
try {
    $env:APPDATA = $tmp
    $p = Start-Process -FilePath (Resolve-Path 'dist\MVM\MVM.exe').Path -ArgumentList '--verificar' -Wait -PassThru
} finally {
    $env:APPDATA = $appDataOriginal
}
$bancoOk = Test-Path (Join-Path $tmp 'MVM\mvm.db')
Remove-Item -Recurse -Force $tmp
if ($p.ExitCode -ne 0 -or -not $bancoOk) {
    throw "O MVM.exe gerado falhou na verificacao (codigo $($p.ExitCode), banco criado: $bancoOk)."
}
Write-Host 'Verificacao: MVM.exe abriu e criou o banco corretamente.'

# --- 8. Zipa pra distribuir ---
$zip = "dist\MVM-$versao.zip"
Compress-Archive -Path 'dist\MVM' -DestinationPath $zip -Force

Write-Host ''
Write-Host 'Pronto!'
Write-Host "  Programa: $(Resolve-Path 'dist\MVM\MVM.exe')"
Write-Host "  Zip:      $(Resolve-Path $zip)"
Write-Host '  Banco de cada usuario: %APPDATA%\MVM\mvm.db (criado na primeira execucao)'
