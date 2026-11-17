package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.Usuario;
import br.ufms.mvm.persistencia.UsuarioDAO;

import java.sql.SQLException;

// Caso de uso Autenticar usuario (RF05)
public class ControladoraAutenticarUsuario {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // devolve o usuario logado ou lanca IllegalArgumentException com a mensagem
    public Usuario autenticar(String login, String senha) throws SQLException {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Login"));
        }
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Senha"));
        }
        Usuario u = usuarioDAO.buscarPorLogin(login.trim());
        // mesma mensagem pra login e senha errados, pra nao dizer qual dos dois existe
        if (u == null || !u.getSenha().equals(Criptografia.hash(senha))) {
            throw new IllegalArgumentException(MensagensCadastro.LOGIN_INVALIDO);
        }
        if (!u.isAtivo()) {
            throw new IllegalArgumentException(MensagensCadastro.USUARIO_INATIVO);
        }
        Sessao.iniciar(u);
        return u;
    }

    public void sair() {
        Sessao.encerrar();
    }
}
