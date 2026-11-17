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
