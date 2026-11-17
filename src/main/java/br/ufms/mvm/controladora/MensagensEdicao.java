package br.ufms.mvm.controladora;

// Mensagens dos casos de uso da iteracao 4 (edicao, situacao de registro e consultas)
public final class MensagensEdicao {

    // Editar manutencao
    public static final String SEM_ACESSO_MANUTENCAO = "Você só pode alterar manutenções que abriu ou nas quais "
                                                     + "foi incluído como responsável.";
    public static final String NAO_EDITAVEL = "Apenas manutenções em andamento podem ser alteradas. Manutenções "
                                            + "encerradas não podem ser modificadas.";
    public static final String MECANICO_REPETIDO = "Esse mecânico já é responsável pela manutenção.";
    public static final String SO_OPERADOR_REMOVE = "Somente o Operador pode remover mecânicos da manutenção.";
    public static final String SEM_MECANICO = "A manutenção deve possuir ao menos um mecânico responsável vinculado.";
    public static final String CORRETIVA_SEM_PANE = "A manutenção corretiva exige a indicação da pane a ser atendida.";
    public static final String PREVENTIVA_SEM_FILTRO = "A manutenção preventiva deve prever a substituição de ao "
                                                     + "menos um filtro.";
    public static final String PANE_INVALIDA = "A pane selecionada não está disponível para atendimento.";
    public static final String MECANICO_INATIVO = "O mecânico selecionado encontra-se inativo.";
    public static final String CANCELAR = "Deseja descartar as alterações feitas nesta manutenção?";

    // Atualizar situacao de registro
    public static final String PROPRIO_USUARIO = "Não é permitido inativar o seu próprio usuário.";
    public static final String VIATURA_EM_MANUTENCAO = "A viatura possui manutenção em andamento e não pode ser "
                                                     + "inativada antes do seu encerramento.";

    private MensagensEdicao() {
    }

    public static String manutencaoAlterada(int id) {
        return "Manutenção nº " + id + " alterada com sucesso.";
    }

    public static String odometroMenor(int informado, int atual) {
        return "O odômetro de entrada informado (" + informado + ") é inferior ao odômetro atual da viatura ("
                + atual + "). Corrija o valor.";
    }

    // RNF10: confirmacao antes de inativar
    public static String confirmarInativacao(String registro) {
        return "Deseja inativar " + registro + "? Ele deixará de aparecer em novos lançamentos, mas continuará "
                + "visível no histórico.";
    }

    public static String confirmarReativacao(String registro) {
        return "Deseja reativar " + registro + "?";
    }

    public static String situacaoAlterada(String registro, boolean ativo) {
        return registro + (ativo ? " reativado" : " inativado") + " com sucesso.";
    }
}
