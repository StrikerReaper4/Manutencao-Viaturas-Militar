package br.ufms.mvm.modelo;

// Item usado na manutencao ou, com faltante = true, material que ficou faltando
public class ItemManutencao_Material {

    private int idItemMaterial;
    private int quantidade;
    private boolean faltante;
    private Item item;
    private Manutencao manutencao;

    public ItemManutencao_Material(Item item, int quantidade, boolean faltante) {
        this.item = item;
        this.quantidade = quantidade;
        this.faltante = faltante;
    }

    public int getIdItemMaterial() {
        return idItemMaterial;
    }

    public void setIdItemMaterial(int idItemMaterial) {
        this.idItemMaterial = idItemMaterial;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public boolean isFaltante() {
        return faltante;
    }

    public Item getItem() {
        return item;
    }

    public Manutencao getManutencao() {
        return manutencao;
    }

    public void setManutencao(Manutencao manutencao) {
        this.manutencao = manutencao;
    }

    @Override
    public String toString() {
        return item.getNome() + " - " + quantidade + " " + item.getUnidadeMedida();
    }
}
