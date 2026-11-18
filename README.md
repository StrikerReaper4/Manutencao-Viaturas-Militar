# Manutencao-Viaturas-Militar

MVM - Sistema de Controle de Manutenção de Viaturas Militares.
Trabalho de Análise e Projeto Orientado a Objetos - UFMS.

## Tecnologias

- Java 17 (desktop, Swing)
- SQLite (via `sqlite-jdbc`)
- Maven

## Iteração 1 - Abrir Manutenção

Implementação do caso de uso **Abrir manutenção**, seguindo o Diagrama de Classes de Projeto
e o Diagrama de Comunicação da operação `GravarManutencao`.

## Iteração 2 - Encerrar Manutenção

Implementação do caso de uso **Encerrar manutenção**, incluindo o registro de manutenção executada
sem abertura prévia, seguindo o Diagrama de Classes consolidado e o Diagrama de Comunicação da
operação `GravarEncerramento`.

## Iteração 3 - Autenticação e cadastros de apoio

- **Autenticar usuário (RF05):** o sistema abre na tela de login; o menu mostra só o que a classe do
  usuário pode fazer. Senhas guardadas em SHA-256 (RNF06).
- **Gerenciar viatura (RF01)** e **Gerenciar usuário (RF02):** só Operador.
- **Gerenciar item (RF03):** Operador e Mecânico.
- **Registrar pane (RF09):** só Operador; mostra a fila de atendimento por prioridade.
- Remoção só de registros sem vínculo (RNF12/RNF13).

Usuários de teste (senha `123456`): `admin` (Operador), `silva`, `souza`, `pereira` (Mecânicos).

## Iteração 4 - Edição de manutenção e consultas operacionais

- **Editar manutenção (RF07):** altera tipo, pane, odômetro de entrada, filtros previstos e mecânicos de uma
  manutenção em andamento. O Mecânico só edita as que abriu ou em que é responsável e só pode *adicionar*
  mecânicos; o Operador edita qualquer uma e também remove. Manutenções encerradas não aparecem (RNF14).
- **Atualizar situação de registro (RF04):** só Operador; inativa/reativa viaturas, itens e usuários com
  confirmação (RNF10), guardando data e usuário responsável em `TB_HistoricoSituacao`.
- **Consultar manutenções em andamento (RF10)** e **Consultar manutenções preventivas (RF11):** Operador e
  Mecânico; a preventiva é sinalizada ao atingir o km limite (5.000 se não informado) ou 6 meses.
- Bancos antigos são atualizados sozinhos (coluna `idUsuarioAbertura` e dados de `dados_iteracao4.sql`).

## Iteração 5 - Consultas complementares e relatórios

- **Registrar pane** agora aceita as peças necessárias (`TB_Pane_Item`).
- **Consultar viaturas aguardando peças (RF12):** junta as peças das panes ainda não atendidas com os
  materiais e o serviço que faltaram na última manutenção encerrada de cada viatura.
- **Emitir relatório de itens utilizados (RF13):** por período (padrão: hoje) e nome do item, sem diferenciar
  maiúsculas nem acentos.
- **Emitir relatório mensal (RF14):** manutenções iniciadas ou encerradas no mês; o que não foi preenchido
  sai em branco, inclusive a coluna de valor, para preencher na folha impressa.
- Consultas e relatórios têm botão **Imprimir** (dá para salvar em PDF pela janela de impressão).
- Consulta de peças e relatórios são privativos do Operador.

## Estrutura

```
src/main/java/br/ufms/mvm
├── visao/         interface (telas de Abrir e Encerrar manutenção)
├── controladora/  uma controladora por caso de uso
├── modelo/        classes do modelo (Viatura, Pane, Manutencao...)
└── persistencia/  DAOs e conexão com o SQLite
src/main/resources/banco
├── schema.sql     tabelas (mapeamento objeto-relacional)
├── dados.sql      dados de teste
└── dados_iteracao2.sql  dados de teste do encerramento
```

## Como rodar

Abrir o projeto no IntelliJ / NetBeans / VS Code como projeto Maven e executar `br.ufms.mvm.Main`.

Ou pelo terminal:

```
mvn compile exec:java
```

O arquivo `mvm.db` é criado na primeira execução, já com alguns dados de teste.

## Branches

- `main`: versão estável
- `iteracao-1-abrir-manutencao`: desenvolvimento da 1ª iteração
