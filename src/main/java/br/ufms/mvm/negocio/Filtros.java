package br.ufms.mvm.negocio;

public class Filtros {

    private int idFiltro;
    private String tipo;
    private String especificacao;

    public Filtros(int idFiltro, String tipo, String especificacao) {
        this.idFiltro = idFiltro;
        this.tipo = tipo;
        this.especificacao = especificacao;
    }

    public int getIdFiltro() {
        return idFiltro;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    @Override
    public String toString() {
        return tipo + " (" + especificacao + ")";
    }
}
