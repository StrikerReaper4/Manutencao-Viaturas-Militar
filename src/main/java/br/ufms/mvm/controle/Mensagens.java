package br.ufms.mvm.controle;

// Mensagens M01 a M12 do caso de uso Abrir Manutencao
public final class Mensagens {

    public static final String M04 = "A manutenção deve possuir ao menos um mecânico responsável vinculado.";
    public static final String M06 = "Não há pane registrada e não atendida para esta viatura. "
                                   + "O prosseguimento é permitido apenas para manutenção preventiva.";
    public static final String M07 = "A manutenção corretiva exige a indicação da pane a ser atendida.";
    public static final String M08 = "A manutenção preventiva deve prever a substituição de ao menos um filtro.";
    public static final String M10 = "Deseja cancelar a abertura da manutenção? Os dados informados serão descartados.";
    public static final String M11 = "Não foi possível registrar a manutenção. As alterações realizadas foram desfeitas. "
                                   + "Tente novamente.";
    public static final String M12 = "A viatura, a pane ou o mecânico selecionado encontra-se inativo e não pode "
                                   + "ser utilizado na abertura da manutenção.";

    private Mensagens() {
    }

    public static String m01(int id, String eb) {
        return "Manutenção nº " + id + " aberta com sucesso para a viatura EB " + eb + ".";
    }

    public static String m02(String campo) {
        return "O campo " + campo + " é de preenchimento obrigatório. A manutenção não foi registrada.";
    }

    public static String m03(int informado, int atual) {
        return "O odômetro informado (" + informado + ") é inferior ao odômetro atual da viatura ("
                + atual + "). Corrija o valor.";
    }

    public static String m05(String eb, int idManutencao) {
        return "A viatura EB " + eb + " já possui a manutenção nº " + idManutencao + " em andamento. "
                + "Deseja consultá-la?";
    }

    public static String m09(int km) {
        return "Faltam " + km + " km para a próxima manutenção preventiva desta viatura. "
                + "Deseja abrir a manutenção antecipadamente?";
    }
}
