package br.ufms.mvm.modelo;

public abstract class Usuario {

    private int idUsuario;
    private String login;
    private String senha;
    private String nome;
    private String graduacao;
    private boolean ativo;

    public Usuario(int idUsuario, String login, String senha, String nome, String graduacao, boolean ativo) {
        this.idUsuario = idUsuario;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.graduacao = graduacao;
        this.ativo = ativo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public String getNome() {
        return nome;
    }

    public String getGraduacao() {
        return graduacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    @Override
    public String toString() {
        return graduacao + " " + nome;
    }
}
