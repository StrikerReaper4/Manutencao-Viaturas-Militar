package br.ufms.mvm.modelo;

public class Item {

    private int idItem;
    private String nome;
    private String unidadeMedida;
    private int quantidadeEstoque;
    private boolean ativo;

    public Item(int idItem, String nome, String unidadeMedida, int quantidadeEstoque, boolean ativo) {
        this.idItem = idItem;
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.quantidadeEstoque = quantidadeEstoque;
        this.ativo = ativo;
    }

    public int getIdItem() {
        return idItem;
    }

    public String getNome() {
        return nome;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public boolean isAtivo() {
        return ativo;
    }

    @Override
    public String toString() {
        return nome + " (" + unidadeMedida + ")";
    }
}
