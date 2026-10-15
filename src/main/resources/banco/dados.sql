-- Dados de teste, so roda quando o banco é criado pela primeira vez
-- senha de todos: 123456 (SHA-256)
--
-- Cenarios pra testar o caso de uso Abrir Manutencao:
--   EB3456789012 -> tem panes, corretiva (Variante 2) ou preventiva vencida
--   EB3456789013 -> uma pane, odometro alto (bom pra testar M03 digitando menos de 87320)
--   EB3456789014 -> sem pane (M06) e preventiva ainda nao vencida (M09)
--   EB3456789015 -> inativa, nao aparece na lista (RN04)
--   EB3456789016 -> ja tem manutencao em andamento (M05)
--   EB3456789017 -> sem pane (M06) e preventiva vencida, vai direto pros filtros
--   mecanico Lima -> inativo, nao aparece na lista (RN04)

INSERT INTO TB_Modelo (nome) VALUES ('Marruá AM21');
INSERT INTO TB_Modelo (nome) VALUES ('Worker 15.210');
INSERT INTO TB_Modelo (nome) VALUES ('Guarani VBTP');

INSERT INTO TB_TipoViatura (descricao) VALUES ('Viatura leve');
INSERT INTO TB_TipoViatura (descricao) VALUES ('Viatura de transporte');
INSERT INTO TB_TipoViatura (descricao) VALUES ('Viatura blindada');

INSERT INTO TB_Viatura VALUES ('EB3456789012', 'ABC1D23', 5000, 42150, 'Disponível', 1, 1, 1);
INSERT INTO TB_Viatura VALUES ('EB3456789013', 'DEF4G56', 10000, 87320, 'Disponível com restrição', 1, 2, 2);
INSERT INTO TB_Viatura VALUES ('EB3456789014', NULL, 5000, 12800, 'Disponível', 1, 3, 3);
INSERT INTO TB_Viatura VALUES ('EB3456789015', 'GHI7J89', 5000, 150300, 'Indisponível', 0, 1, 1);

INSERT INTO TB_Viatura VALUES ('EB3456789016', 'JKL0M12', 5000, 63400, 'Indisponível', 1, 2, 2);
INSERT INTO TB_Viatura VALUES ('EB3456789017', 'NOP3Q45', 5000, 26000, 'Disponível', 1, 1, 1);

-- datas limite: 20/10/2026, 05/11/2026 e 15/10/2026
INSERT INTO TB_Pane (descricao, prioridade, dataLimite, missao, situacao, EB) VALUES ('Vazamento de óleo no motor', 1, 1792508400000, 'Patrulha de fronteira', 'Registrada', 'EB3456789012');
INSERT INTO TB_Pane (descricao, prioridade, dataLimite, missao, situacao, EB) VALUES ('Farol dianteiro queimado', 3, 1793890800000, NULL, 'Registrada', 'EB3456789012');
INSERT INTO TB_Pane (descricao, prioridade, dataLimite, missao, situacao, EB) VALUES ('Freio traseiro com ruído', 2, 1792076400000, 'Apoio logístico', 'Registrada', 'EB3456789013');
-- pane ja em atendimento, nao deve aparecer na lista de nao atendidas
INSERT INTO TB_Pane (descricao, prioridade, dataLimite, missao, situacao, EB) VALUES ('Embreagem patinando', 1, 1792076400000, 'Escolta de comboio', 'Em atendimento', 'EB3456789016');
-- pane de viatura inativa
INSERT INTO TB_Pane (descricao, prioridade, missao, situacao, EB) VALUES ('Bateria descarregando', 2, NULL, 'Registrada', 'EB3456789015');

INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Filtro de óleo', 'Tecfil PSL 140');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Filtro de combustível', 'Tecfil PSC 496');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Filtro de ar', 'Tecfil ARL 4151');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Decantador', 'Racor R90P');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Filtro de arla', 'Bosch F01C600194');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Filtro de ar da cabine', 'Tecfil ARL 4040');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Direção hidráulica', 'Harza F1200');

INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario) VALUES ('silva', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Silva', 'Sgt', 'MECANICO');
INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario) VALUES ('souza', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Souza', 'Cb', 'MECANICO');
INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario) VALUES ('pereira', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Pereira', 'Sd', 'MECANICO');
INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario, ativo) VALUES ('lima', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Lima', 'Sd', 'MECANICO', 0);
INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario) VALUES ('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Oliveira', 'Ten', 'OPERADOR');

-- preventiva antiga ja encerrada, serve pra testar a M09 (EB3456789014 faz a proxima com 15000 km)
INSERT INTO TB_Manutencao (tipo, dataInicio, dataEncerramento, odometroEntrada, odometroSaida, situacao, situacaoFinal, EB)
VALUES ('Preventiva', 1767225600000, 1767312000000, 10000, 10020, 'Encerrada', 'Disponível', 'EB3456789014');

-- preventiva antiga da EB3456789017 (proxima era com 25000 km, ja passou)
INSERT INTO TB_Manutencao (tipo, dataInicio, dataEncerramento, odometroEntrada, odometroSaida, situacao, situacaoFinal, EB)
VALUES ('Preventiva', 1756728000000, 1756814400000, 20000, 20015, 'Encerrada', 'Disponível', 'EB3456789017');

-- manutencao em andamento da EB3456789016 (M05), com o Silva de responsavel
INSERT INTO TB_Manutencao (tipo, dataInicio, odometroEntrada, situacao, EB, idPane)
VALUES ('Corretiva', 1791547200000, 63400, 'Em andamento', 'EB3456789016', 4);
INSERT INTO TB_Manutencao_Mecanico (idManutencao, idUsuario) VALUES (3, 1);
