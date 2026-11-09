-- Dados de teste da iteracao 2, entram quando as tabelas novas sao criadas
--   EB3456789016 -> manutencao corretiva em andamento, serve pra encerrar (Variante 3)
--   EB3456789018 -> manutencao preventiva em andamento, com filtros previstos (Variante 2)
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
