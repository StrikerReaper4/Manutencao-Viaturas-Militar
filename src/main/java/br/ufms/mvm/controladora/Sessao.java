package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.Administrador;
import br.ufms.mvm.modelo.Usuario;

// Guarda o usuario que fez login (RF05), usado pra liberar as funcionalidades da classe dele
public final class Sessao {

    private static Usuario usuarioLogado;

    private Sessao() {
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    static void iniciar(Usuario u) {
        usuarioLogado = u;
    }

    public static void encerrar() {
        usuarioLogado = null;
    }

    public static boolean isOperador() {
        return usuarioLogado instanceof Administrador;
    }
}
