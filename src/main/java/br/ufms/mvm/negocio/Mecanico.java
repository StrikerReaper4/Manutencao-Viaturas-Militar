package br.ufms.mvm.negocio;

public class Mecanico extends Usuario {

    public Mecanico(int idUsuario, String login, String senha, String nome, String graduacao, boolean ativo) {
        super(idUsuario, login, senha, nome, graduacao, ativo);
    }
}
