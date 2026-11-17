package br.ufms.mvm;

import br.ufms.mvm.visao.TelaLogin;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // se nao der, fica no visual padrao do Java
        }

        // RNF04: o sistema sempre comeca pela autenticacao
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
