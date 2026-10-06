package br.ufms.mvm.negocio;

// Filtro previsto numa manutencao preventiva (tabela TB_ItemManutencao_Filtro)
public class ItemManutencao {

    private int idItem;
    private String especificacao;
    private Double volumeOleo;   // so vai ser preenchido no encerramento
    private Filtros filtro;
    private Manutencao manutencao;

    public ItemManutencao(Filtros f) {
        setFiltro(f);
    }

    public void setFiltro(Filtros f) {
        this.filtro = f;
        this.especificacao = f.getEspecificacao();
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public Double getVolumeOleo() {
        return volumeOleo;
    }

    public Filtros getFiltro() {
        return filtro;
    }

    public Manutencao getManutencao() {
        return manutencao;
    }

    public void setManutencao(Manutencao manutencao) {
        this.manutencao = manutencao;
    }
}
