package br.ufms.mvm.modelo;

public class Filtros {

    private int idFiltro;
    private String tipo;
    private String especificacao;
    private Double volumeOleo;   // so leva o valor da tela ate o item, nao vai pra TB_Filtro

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

    public Double getVolumeOleo() {
        return volumeOleo;
    }

    public void setVolumeOleo(Double volumeOleo) {
        this.volumeOleo = volumeOleo;
    }

    @Override
    public String toString() {
        return tipo + " (" + especificacao + ")";
    }
}
