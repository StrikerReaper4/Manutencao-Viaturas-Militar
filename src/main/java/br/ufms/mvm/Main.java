package br.ufms.mvm;

import br.ufms.mvm.persistencia.Conexao;
import br.ufms.mvm.visao.TelaLogin;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        // "MVM.exe --verificar": abre o banco e sai sem mostrar tela. O gerar-exe.ps1 usa isso
        // pra conferir que o executavel gerado funciona (codigo de saida 0 = tudo certo).
        if (args.length > 0 && "--verificar".equals(args[0])) {
            System.exit(verificar());
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // se nao der, fica no visual padrao do Java
        }

        // RNF04: o sistema sempre comeca pela autenticacao
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }

    private static int verificar() {
        try (Statement st = Conexao.getConexao().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM TB_Usuario")) {
            return rs.next() && rs.getInt(1) > 0 ? 0 : 2;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
