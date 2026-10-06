-- Esquema conforme o Mapeamento Objeto-Relacional (MVM)
-- coluna "ativo" incluida em viatura e usuario por causa da RN04 (so registros ativos)

CREATE TABLE IF NOT EXISTS TB_Modelo (
    idModelo INTEGER PRIMARY KEY AUTOINCREMENT,
    nome     TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS TB_TipoViatura (
    idTipoViatura INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao     TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS TB_Viatura (
    EB                     TEXT PRIMARY KEY,
    placa                  TEXT,
    kmManutencaoPreventiva INTEGER NOT NULL DEFAULT 5000,
    odometroAtual          INTEGER NOT NULL DEFAULT 0,
    situacao               TEXT NOT NULL,
    ativo                  INTEGER NOT NULL DEFAULT 1,
    idModelo               INTEGER REFERENCES TB_Modelo(idModelo),
    idTipoViatura          INTEGER REFERENCES TB_TipoViatura(idTipoViatura)
);

CREATE TABLE IF NOT EXISTS TB_Pane (
    idPane     INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao  TEXT NOT NULL,
    prioridade INTEGER NOT NULL,
    dataLimite INTEGER,
    missao     TEXT,
    situacao   TEXT NOT NULL,
    EB         TEXT NOT NULL REFERENCES TB_Viatura(EB)
);

CREATE TABLE IF NOT EXISTS TB_Manutencao (
    idManutencao     INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo             TEXT NOT NULL,
    dataInicio       INTEGER NOT NULL,
    dataEncerramento INTEGER,
    odometroEntrada  INTEGER NOT NULL,
    odometroSaida    INTEGER,
    situacao         TEXT NOT NULL,
    situacaoFinal    TEXT,
    EB               TEXT NOT NULL REFERENCES TB_Viatura(EB),
    idPane           INTEGER REFERENCES TB_Pane(idPane)
);

CREATE TABLE IF NOT EXISTS TB_Filtro (
    idFiltro      INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo          TEXT NOT NULL,
    especificacao TEXT
);

CREATE TABLE IF NOT EXISTS TB_ItemManutencao_Filtro (
    idItemFiltro  INTEGER NOT NULL,
    idManutencao  INTEGER NOT NULL REFERENCES TB_Manutencao(idManutencao),
    especificacao TEXT,
    volumeOleo    REAL,
    idFiltro      INTEGER NOT NULL REFERENCES TB_Filtro(idFiltro),
    PRIMARY KEY (idItemFiltro, idManutencao)
);

CREATE TABLE IF NOT EXISTS TB_Usuario (
    idUsuario   INTEGER PRIMARY KEY AUTOINCREMENT,
    login       TEXT NOT NULL UNIQUE,
    senha       TEXT NOT NULL,
    nome        TEXT NOT NULL,
    graduacao   TEXT,
    tipoUsuario TEXT NOT NULL,
    ativo       INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS TB_Manutencao_Mecanico (
    idManutencao INTEGER NOT NULL REFERENCES TB_Manutencao(idManutencao),
    idUsuario    INTEGER NOT NULL REFERENCES TB_Usuario(idUsuario),
    PRIMARY KEY (idManutencao, idUsuario)
);
