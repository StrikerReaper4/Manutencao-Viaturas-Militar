package br.ufms.mvm.negocio;

// Eh o Operador dos requisitos (no banco fica tipoUsuario = OPERADOR)
public class Administrador extends Usuario {

    public Administrador(int idUsuario, String login, String senha, String nome, String graduacao, boolean ativo) {
        super(idUsuario, login, senha, nome, graduacao, ativo);
    }
}
