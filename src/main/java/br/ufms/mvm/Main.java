package br.ufms.mvm;

import br.ufms.mvm.visao.Estilo;
import br.ufms.mvm.visao.TelaAbrirManutencao;
import br.ufms.mvm.visao.TelaEncerrarManutencao;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // se nao der, fica no visual padrao do Java
        }

        // Login fica pra iteracao 3, por enquanto abre direto no menu de manutencoes
        SwingUtilities.invokeLater(() -> criarMenu().setVisible(true));
    }

    static JFrame criarMenu() {
        JFrame menu = new JFrame("MVM - Manutenções");
        menu.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton abrir = Estilo.botaoPrimario("Abrir manutenção");
        JButton encerrar = Estilo.botaoPrimario("Encerrar manutenção");
        abrir.addActionListener(e -> new TelaAbrirManutencao().setVisible(true));
        encerrar.addActionListener(e -> new TelaEncerrarManutencao().setVisible(true));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho("Manutenções", "Selecione a operação desejada."), BorderLayout.NORTH);
        raiz.add(Estilo.rodape(abrir, encerrar), BorderLayout.SOUTH);
        menu.setContentPane(raiz);
        menu.pack();
        menu.setLocationRelativeTo(null);
        return menu;
    }
}
