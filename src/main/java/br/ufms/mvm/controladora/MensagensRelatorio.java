package br.ufms.mvm.controladora;

// Mensagens dos casos de uso da iteracao 5 (pecas da pane, consulta e relatorios)
public final class MensagensRelatorio {

    public static final String ITEM_INATIVO = "O item selecionado encontra-se inativo e não pode ser indicado.";
    public static final String QUANTIDADE_POSITIVA = "O campo Quantidade deve conter um número inteiro maior que zero.";
    public static final String PECA_REPETIDA = "Essa peça já foi indicada para a pane.";
    public static final String PERIODO_INVERTIDO = "A data inicial não pode ser posterior à data final.";
    public static final String MES_INVALIDO = "O campo Ano deve conter um ano válido (ex.: 2026).";

    private MensagensRelatorio() {
    }

    public static String dataInvalida(String campo) {
        return "O campo " + campo + " deve estar no formato dd/mm/aaaa.";
    }
}
