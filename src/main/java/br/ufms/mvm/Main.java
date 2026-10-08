package br.ufms.mvm;

import br.ufms.mvm.visao.TelaAbrirManutencao;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // se nao der, fica no visual padrao do Java
        }

        // Login fica pra outra iteracao, por enquanto abre direto na tela do caso de uso
        SwingUtilities.invokeLater(() -> new TelaAbrirManutencao().setVisible(true));
    }
}
