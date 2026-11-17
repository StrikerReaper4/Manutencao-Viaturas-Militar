package br.ufms.mvm.controladora;

// Mensagens dos casos de uso da iteracao 3 (autenticacao, cadastros e pane)
public final class MensagensCadastro {

    // Autenticar usuario
    public static final String LOGIN_INVALIDO = "Login ou senha inválidos. Verifique os dados informados.";
    public static final String USUARIO_INATIVO = "O usuário informado encontra-se inativo e não pode acessar o sistema.";

    // comuns aos cadastros
    public static final String SEM_PERMISSAO = "Sua classe de usuário não tem permissão para esta operação.";
    public static final String SELECIONE = "Selecione um registro na lista.";
    public static final String ERRO_GRAVAR = "Não foi possível gravar os dados. As alterações realizadas foram desfeitas. "
                                           + "Tente novamente.";

    // Gerenciar viatura
    public static final String EB_REPETIDO = "Já existe uma viatura cadastrada com esse EB.";
    public static final String VIATURA_COM_VINCULO = "A viatura possui pane ou manutenção registrada e não pode ser "
                                                   + "removida. Utilize a inativação do registro.";

    // Gerenciar item
    public static final String ITEM_COM_VINCULO = "O item já foi utilizado em manutenção e não pode ser removido. "
                                                + "Utilize a inativação do registro.";

    // Gerenciar usuario
    public static final String LOGIN_REPETIDO = "Já existe um usuário com esse login.";
    public static final String SENHA_CURTA = "O campo Senha deve ter no mínimo 6 caracteres.";
    public static final String PROPRIA_CLASSE = "Não é permitido alterar a classe do seu próprio usuário.";

    // Registrar pane
    public static final String VIATURA_INATIVA = "A viatura selecionada encontra-se inativa e não pode receber pane.";
    public static final String DATA_INVALIDA = "O campo Data limite deve estar no formato dd/mm/aaaa.";
    public static final String DATA_PASSADA = "O campo Data limite não pode ser anterior à data atual.";

    private MensagensCadastro() {
    }

    // RNF09: diz o campo e o motivo
    public static String obrigatorio(String campo) {
        return "O campo " + campo + " é de preenchimento obrigatório.";
    }

    public static String numeroInvalido(String campo) {
        return "O campo " + campo + " deve conter um número inteiro maior ou igual a zero.";
    }

    public static String odometroMenor(int informado, int atual) {
        return "O campo Odômetro atual (" + informado + ") não pode ser inferior ao odômetro já registrado ("
                + atual + ").";
    }

    public static String gravado(String registro) {
        return registro + " gravado com sucesso.";
    }

    public static String removido(String registro) {
        return registro + " removido com sucesso.";
    }

    public static String confirmarRemocao(String registro) {
        return "Deseja remover " + registro + "? Esta operação não pode ser desfeita.";
    }

    public static String paneRegistrada(int id, String eb) {
        return "Pane nº " + id + " registrada com sucesso para a viatura EB " + eb + ".";
    }
}
