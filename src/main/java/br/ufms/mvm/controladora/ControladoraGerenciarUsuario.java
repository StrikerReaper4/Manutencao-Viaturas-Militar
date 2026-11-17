package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.Administrador;
import br.ufms.mvm.modelo.Mecanico;
import br.ufms.mvm.modelo.Usuario;
import br.ufms.mvm.persistencia.UsuarioDAO;

import java.sql.SQLException;
import java.util.List;

// Caso de uso Gerenciar usuario (RF02): so o Operador cria contas, sem autocadastro.
// Nao tem remocao, so inclusao e alteracao (a inativacao fica no RF04, iteracao 4).
public class ControladoraGerenciarUsuario {

    public static final String OPERADOR = "Operador";
    public static final String MECANICO = "Mecânico";
    private static final int SENHA_MINIMA = 6;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public ControladoraGerenciarUsuario() {
        if (!Sessao.isOperador()) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    public static String classe(Usuario u) {
        return u instanceof Administrador ? OPERADOR : MECANICO;
    }

    // idUsuario null inclui; na alteracao a senha em branco mantem a atual
    public void gravarUsuario(Integer idUsuario, String login, String senha, String graduacao,
                              String nome, String classe) throws SQLException {
        login = login == null ? "" : login.trim().toLowerCase();
        if (login.isEmpty()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Login"));
        }
        if (graduacao == null || graduacao.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Graduação"));
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Nome do militar"));
        }
        if (classe == null) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Classe de usuário"));
        }
        boolean senhaInformada = senha != null && !senha.isEmpty();
        if (idUsuario == null && !senhaInformada) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Senha"));
        }
        if (senhaInformada && senha.length() < SENHA_MINIMA) {
            throw new IllegalArgumentException(MensagensCadastro.SENHA_CURTA);
        }

        Usuario mesmoLogin = usuarioDAO.buscarPorLogin(login);
        if (mesmoLogin != null && (idUsuario == null || mesmoLogin.getIdUsuario() != idUsuario)) {
            throw new IllegalArgumentException(MensagensCadastro.LOGIN_REPETIDO);
        }

        if (idUsuario == null) {
            usuarioDAO.inserir(montar(0, login, Criptografia.hash(senha), nome.trim(), graduacao.trim(), true, classe));
            return;
        }
        Usuario existente = usuarioDAO.buscarPorID(idUsuario);
        if (existente == null) {
            throw new IllegalArgumentException(MensagensCadastro.SELECIONE);
        }
        // evita o operador tirar o proprio acesso
        if (existente.getIdUsuario() == Sessao.getUsuarioLogado().getIdUsuario() && !classe.equals(classe(existente))) {
            throw new IllegalArgumentException(MensagensCadastro.PROPRIA_CLASSE);
        }
        String hash = senhaInformada ? Criptografia.hash(senha) : existente.getSenha();
        usuarioDAO.atualizar(montar(idUsuario, login, hash, nome.trim(), graduacao.trim(), existente.isAtivo(), classe));
    }

    private Usuario montar(int id, String login, String hash, String nome, String graduacao, boolean ativo, String classe) {
        if (OPERADOR.equals(classe)) {
            return new Administrador(id, login, hash, nome, graduacao, ativo);
        }
        return new Mecanico(id, login, hash, nome, graduacao, ativo);
    }
}
