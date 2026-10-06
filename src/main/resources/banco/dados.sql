-- Dados de teste, so roda quando o banco é criado pela primeira vez
-- senha de todos: 123456 (SHA-256)

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

INSERT INTO TB_Pane (descricao, prioridade, missao, situacao, EB) VALUES ('Vazamento de óleo no motor', 1, 'Patrulha de fronteira', 'Registrada', 'EB3456789012');
INSERT INTO TB_Pane (descricao, prioridade, missao, situacao, EB) VALUES ('Farol dianteiro queimado', 3, NULL, 'Registrada', 'EB3456789012');
INSERT INTO TB_Pane (descricao, prioridade, missao, situacao, EB) VALUES ('Freio traseiro com ruído', 2, 'Apoio logístico', 'Registrada', 'EB3456789013');

INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Filtro de óleo', 'Tecfil PSL 140');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Filtro de combustível', 'Tecfil PSC 496');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Filtro de ar', 'Tecfil ARL 4151');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Decantador', 'Racor R90P');
INSERT INTO TB_Filtro (tipo, especificacao) VALUES ('Filtro de arla', 'Bosch F01C600194');

INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario) VALUES ('silva', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Silva', 'Sgt', 'MECANICO');
INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario) VALUES ('souza', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Souza', 'Cb', 'MECANICO');
INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario) VALUES ('pereira', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Pereira', 'Sd', 'MECANICO');
INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario, ativo) VALUES ('lima', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Lima', 'Sd', 'MECANICO', 0);
INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario) VALUES ('admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'Oliveira', 'Ten', 'OPERADOR');
