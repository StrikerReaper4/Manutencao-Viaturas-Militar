-- Dados de teste da iteracao 2, entram quando as tabelas novas sao criadas
-- Cenarios pra testar o caso de uso Encerrar Manutencao:
--   EB3456789016 -> corretiva em andamento, fica reservada pra M05 do Abrir
--   EB3456789018 -> preventiva em andamento com filtros previstos (Variante 2)
--   EB3456789019 -> outra preventiva em andamento (FA06/M09: trocar pra corretiva)
--   EB3456789020 -> corretiva em andamento com pane (Variante 3, pane vira Atendida)
--   EB3456789021 -> corretiva em andamento com pane (Variante 4, servico nao concluido)
--   EB3456789012 -> sem manutencao em andamento, usar na Variante 1 (sem abertura previa)
--   Junta antiga -> inativa, nao aparece na lista de itens (RN06)

INSERT INTO TB_Item (nome, unidadeMedida, quantidadeEstoque) VALUES ('Óleo lubrificante 15W40', 'L', 200);
INSERT INTO TB_Item (nome, unidadeMedida, quantidadeEstoque) VALUES ('Junta do cárter', 'un', 12);
INSERT INTO TB_Item (nome, unidadeMedida, quantidadeEstoque) VALUES ('Abraçadeira metálica', 'un', 50);
INSERT INTO TB_Item (nome, unidadeMedida, quantidadeEstoque) VALUES ('Pastilha de freio', 'jogo', 8);
INSERT INTO TB_Item (nome, unidadeMedida, quantidadeEstoque) VALUES ('Filtro de arla Bosch F01C600194', 'un', 0);
INSERT INTO TB_Item (nome, unidadeMedida, quantidadeEstoque, ativo) VALUES ('Junta antiga', 'un', 3, 0);

INSERT INTO TB_Viatura VALUES ('EB3456789018', 'RST6U78', 5000, 48200, 'Indisponível', 1, 2, 2);

INSERT INTO TB_Manutencao (tipo, dataInicio, odometroEntrada, situacao, EB)
VALUES ('Preventiva', 1791547200000, 48200, 'Em andamento', 'EB3456789018');

INSERT INTO TB_Manutencao_Mecanico (idManutencao, idUsuario)
VALUES ((SELECT MAX(idManutencao) FROM TB_Manutencao), (SELECT idUsuario FROM TB_Usuario WHERE login = 'souza'));

INSERT INTO TB_ItemManutencao_Filtro (idItemFiltro, idManutencao, especificacao, idFiltro)
VALUES (1, (SELECT MAX(idManutencao) FROM TB_Manutencao), 'Tecfil PSL 140', (SELECT idFiltro FROM TB_Filtro WHERE tipo = 'Filtro de óleo'));
INSERT INTO TB_ItemManutencao_Filtro (idItemFiltro, idManutencao, especificacao, idFiltro)
VALUES (2, (SELECT MAX(idManutencao) FROM TB_Manutencao), 'Tecfil PSC 496', (SELECT idFiltro FROM TB_Filtro WHERE tipo = 'Filtro de combustível'));
INSERT INTO TB_ItemManutencao_Filtro (idItemFiltro, idManutencao, especificacao, idFiltro)
VALUES (3, (SELECT MAX(idManutencao) FROM TB_Manutencao), 'Tecfil ARL 4151', (SELECT idFiltro FROM TB_Filtro WHERE tipo = 'Filtro de ar'));

-- viaturas extras pra cada cenario ter a sua manutencao em andamento
INSERT INTO TB_Viatura VALUES ('EB3456789019', 'VWX9Y01', 5000, 31500, 'Indisponível', 1, 1, 1);
INSERT INTO TB_Viatura VALUES ('EB3456789020', 'ZAB2C34', 10000, 72000, 'Indisponível', 1, 2, 2);
INSERT INTO TB_Viatura VALUES ('EB3456789021', 'DEF5G67', 5000, 54300, 'Indisponível', 1, 3, 3);

-- EB3456789019: preventiva com filtros previstos
INSERT INTO TB_Manutencao (tipo, dataInicio, odometroEntrada, situacao, EB)
VALUES ('Preventiva', 1791633600000, 31500, 'Em andamento', 'EB3456789019');
INSERT INTO TB_Manutencao_Mecanico (idManutencao, idUsuario)
VALUES ((SELECT MAX(idManutencao) FROM TB_Manutencao), (SELECT idUsuario FROM TB_Usuario WHERE login = 'silva'));
INSERT INTO TB_ItemManutencao_Filtro (idItemFiltro, idManutencao, especificacao, idFiltro)
VALUES (1, (SELECT MAX(idManutencao) FROM TB_Manutencao), 'Tecfil PSL 140', (SELECT idFiltro FROM TB_Filtro WHERE tipo = 'Filtro de óleo'));
INSERT INTO TB_ItemManutencao_Filtro (idItemFiltro, idManutencao, especificacao, idFiltro)
VALUES (2, (SELECT MAX(idManutencao) FROM TB_Manutencao), 'Racor R90P', (SELECT idFiltro FROM TB_Filtro WHERE tipo = 'Decantador'));

-- EB3456789020: corretiva, pane em atendimento (data limite 25/10/2026)
INSERT INTO TB_Pane (descricao, prioridade, dataLimite, missao, situacao, EB)
VALUES ('Alternador sem carga', 1, 1792940400000, 'Transporte de tropa', 'Em atendimento', 'EB3456789020');
INSERT INTO TB_Manutencao (tipo, dataInicio, odometroEntrada, situacao, EB, idPane)
VALUES ('Corretiva', 1791633600000, 72000, 'Em andamento', 'EB3456789020', (SELECT MAX(idPane) FROM TB_Pane));
INSERT INTO TB_Manutencao_Mecanico (idManutencao, idUsuario)
VALUES ((SELECT MAX(idManutencao) FROM TB_Manutencao), (SELECT idUsuario FROM TB_Usuario WHERE login = 'pereira'));

-- EB3456789021: corretiva, pane em atendimento (data limite 30/10/2026)
INSERT INTO TB_Pane (descricao, prioridade, dataLimite, missao, situacao, EB)
VALUES ('Vazamento no radiador', 2, 1793372400000, 'Patrulha de fronteira', 'Em atendimento', 'EB3456789021');
INSERT INTO TB_Manutencao (tipo, dataInicio, odometroEntrada, situacao, EB, idPane)
VALUES ('Corretiva', 1791633600000, 54300, 'Em andamento', 'EB3456789021', (SELECT MAX(idPane) FROM TB_Pane));
INSERT INTO TB_Manutencao_Mecanico (idManutencao, idUsuario)
VALUES ((SELECT MAX(idManutencao) FROM TB_Manutencao), (SELECT idUsuario FROM TB_Usuario WHERE login = 'silva'));
INSERT INTO TB_Manutencao_Mecanico (idManutencao, idUsuario)
VALUES ((SELECT MAX(idManutencao) FROM TB_Manutencao), (SELECT idUsuario FROM TB_Usuario WHERE login = 'souza'));
