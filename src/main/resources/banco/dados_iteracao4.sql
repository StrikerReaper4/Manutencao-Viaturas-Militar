-- Dados de teste da iteracao 4, entram quando a TB_HistoricoSituacao eh criada
-- Cenarios:
--   EB3456789022 -> preventiva feita em 01/10/2026 e rodou so 1200 km: aparece "em dia" no RF11
--   (as manutencoes em andamento das iteracoes 1 e 2 servem pro Editar e pro RF10)

INSERT INTO TB_Viatura VALUES ('EB3456789022', 'UVW9X01', 5000, 31200, 'Disponível', 1, 1, 1);

INSERT INTO TB_Manutencao (tipo, dataInicio, dataEncerramento, odometroEntrada, odometroSaida, situacao, situacaoFinal, EB)
VALUES ('Preventiva', 1790823600000, 1790910000000, 30000, 30000, 'Encerrada', 'Disponível', 'EB3456789022');
