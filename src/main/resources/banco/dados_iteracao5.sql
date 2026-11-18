-- Dados de teste da iteracao 5, entram quando a TB_Pane_Item eh criada
-- Cenarios:
--   EB3456789012 -> a pane "Vazamento de oleo" ja foi registrada pedindo 1 junta do carter:
--                   aparece no RF12 (aguardando pecas)
--   EB3456789014 -> corretiva encerrada em 10/11/2026 com servico nao concluido: usou 4 abracadeiras
--                   e 2 L de oleo, faltou 1 jogo de pastilha e o servico "Trocar amortecedor".
--                   Aparece no RF12, no RF13 (periodo de novembro) e no RF14 (mes 11/2026)
--   EB3456789022 -> a preventiva de 01/10/2026 usou 18 L de oleo: aparece no RF13 e no RF14 de 10/2026

INSERT INTO TB_Pane_Item (idPane, idItem, quantidade)
VALUES ((SELECT idPane FROM TB_Pane WHERE descricao = 'Vazamento de óleo no motor'),
        (SELECT idItem FROM TB_Item WHERE nome = 'Junta do cárter'), 1);

INSERT INTO TB_Pane (descricao, prioridade, dataLimite, missao, situacao, EB)
VALUES ('Suspensão dianteira com folga', 2, 1795316400000, 'Patrulha de fronteira', 'Em atendimento', 'EB3456789014');

INSERT INTO TB_Manutencao (tipo, dataInicio, dataEncerramento, odometroEntrada, odometroSaida, situacao, situacaoFinal, EB, idPane)
VALUES ('Corretiva', 1793703600000, 1794337200000, 12800, 12810, 'Encerrada', 'Disponível com restrição', 'EB3456789014',
        (SELECT MAX(idPane) FROM TB_Pane));
INSERT INTO TB_Manutencao_Mecanico (idManutencao, idUsuario)
VALUES ((SELECT MAX(idManutencao) FROM TB_Manutencao), (SELECT idUsuario FROM TB_Usuario WHERE login = 'pereira'));
INSERT INTO TB_ItemManutencao_Material (idItemMaterial, idManutencao, quantidade, faltante, idItem)
VALUES (1, (SELECT MAX(idManutencao) FROM TB_Manutencao), 4, 0, (SELECT idItem FROM TB_Item WHERE nome = 'Abraçadeira metálica'));
INSERT INTO TB_ItemManutencao_Material (idItemMaterial, idManutencao, quantidade, faltante, idItem)
VALUES (2, (SELECT MAX(idManutencao) FROM TB_Manutencao), 2, 0, (SELECT idItem FROM TB_Item WHERE nome = 'Óleo lubrificante 15W40'));
INSERT INTO TB_ItemManutencao_Material (idItemMaterial, idManutencao, quantidade, faltante, idItem)
VALUES (3, (SELECT MAX(idManutencao) FROM TB_Manutencao), 1, 1, (SELECT idItem FROM TB_Item WHERE nome = 'Pastilha de freio'));
INSERT INTO TB_ItemManutencao_Servico (idItemServico, idManutencao, descricao, faltante)
VALUES (1, (SELECT MAX(idManutencao) FROM TB_Manutencao), 'Trocar amortecedor', 1);

INSERT INTO TB_ItemManutencao_Material (idItemMaterial, idManutencao, quantidade, faltante, idItem)
VALUES (1, (SELECT idManutencao FROM TB_Manutencao WHERE EB = 'EB3456789022' AND situacao = 'Encerrada' LIMIT 1), 18, 0,
        (SELECT idItem FROM TB_Item WHERE nome = 'Óleo lubrificante 15W40'));
