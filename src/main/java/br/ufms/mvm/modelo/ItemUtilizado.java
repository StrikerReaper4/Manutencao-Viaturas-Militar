package br.ufms.mvm.modelo;

// Uma linha do relatorio de itens utilizados (RF13): quanto de um item foi usado numa viatura no periodo
public class ItemUtilizado {

    private final String eb;
    private final String tipoViatura;
    private final String nomeItem;
    private final String unidadeMedida;
    private final int quantidade;

    public ItemUtilizado(String eb, String tipoViatura, String nomeItem, String unidadeMedida, int quantidade) {
        this.eb = eb;
        this.tipoViatura = tipoViatura;
        this.nomeItem = nomeItem;
        this.unidadeMedida = unidadeMedida;
        this.quantidade = quantidade;
    }

    public String getEb() {
        return eb;
    }

    public String getTipoViatura() {
        return tipoViatura;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
