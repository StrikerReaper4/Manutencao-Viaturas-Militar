package br.ufms.mvm.controladora;

// Mensagens M01 a M17 do caso de uso Encerrar Manutencao
public final class MensagensEncerrar {

    public static final String M04 = "A manutenção deve possuir ao menos um mecânico responsável vinculado.";
    public static final String M05 = "Não há manutenção em andamento. O prosseguimento é permitido apenas para "
                                   + "manutenção executada sem abertura prévia.";
    public static final String M06 = "A manutenção preventiva deve registrar a substituição de ao menos um filtro.";
    public static final String M08 = "O serviço faltante deve ser informado quando o serviço não for concluído.";
    public static final String M09 = "A manutenção foi aberta como preventiva. Ao alterá-la para corretiva, os filtros "
                                   + "previstos serão desconsiderados. Deseja confirmar a alteração?";
    public static final String M10 = "Deseja cancelar o encerramento da manutenção? Os dados informados serão descartados.";
    public static final String M11 = "Não foi possível registrar o encerramento da manutenção. As alterações realizadas "
                                   + "foram desfeitas. Tente novamente.";
    public static final String M12 = "A viatura, o item ou o mecânico selecionado encontra-se inativo e não pode ser "
                                   + "utilizado no registro da manutenção.";
    public static final String M16 = "Esse mecânico já foi adicionado!";
    public static final String M17 = "Esse item já foi adicionado!";

    private MensagensEncerrar() {
    }

    public static String m01(int id, String eb, String situacaoFinal) {
        return "Manutenção nº " + id + " encerrada com sucesso para a viatura EB " + eb
                + ". Situação final: " + situacaoFinal + ".";
    }

    public static String m02(String campo) {
        return "O campo " + campo + " é de preenchimento obrigatório. A manutenção não foi encerrada.";
    }

    public static String m03(int informado, int entrada) {
        return "O odômetro de saída informado (" + informado + ") é inferior ao odômetro de entrada da manutenção ("
                + entrada + "). Corrija o valor.";
    }

    public static String m07(String item) {
        return "A quantidade informada para o item " + item + " deve ser maior que zero.";
    }

    public static String m13(String eb, int idManutencao) {
        return "A viatura EB " + eb + " já possui a manutenção nº " + idManutencao
                + " em andamento. Utilize a opção Manutenção em andamento para encerrá-la.";
    }

    public static String m14(int informado, int atual) {
        return "O odômetro de entrada informado (" + informado + ") é inferior ao odômetro atual da viatura ("
                + atual + "). Corrija o valor.";
    }

    public static String m15(String abertura, String encerramento) {
        return "A data de abertura (" + abertura + ") não pode ser posterior à data de encerramento ("
                + encerramento + ").";
    }
}
