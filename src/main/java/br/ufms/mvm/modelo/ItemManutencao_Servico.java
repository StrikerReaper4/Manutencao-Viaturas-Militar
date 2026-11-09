package br.ufms.mvm.modelo;

public class ItemManutencao_Servico {

    private int idItemServico;
    private String descricao;
    private boolean faltante;
    private Manutencao manutencao;

    public ItemManutencao_Servico(String descricao, boolean faltante) {
        this.descricao = descricao;
        this.faltante = faltante;
    }

    public int getIdItemServico() {
        return idItemServico;
    }

    public void setIdItemServico(int idItemServico) {
        this.idItemServico = idItemServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isFaltante() {
        return faltante;
    }

    public Manutencao getManutencao() {
        return manutencao;
    }

    public void setManutencao(Manutencao manutencao) {
        this.manutencao = manutencao;
    }
}
