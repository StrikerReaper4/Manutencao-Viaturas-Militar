package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraAutenticarUsuario;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.sql.SQLException;

// RNF08: so os campos de autenticacao, sem opcao de autocadastro
public class TelaLogin extends JFrame {

    private static final String TITULO = "Autenticar usuário";

    private final ControladoraAutenticarUsuario controladora = new ControladoraAutenticarUsuario();
    private final JTextField txtLogin = new JTextField(22);
    private final JPasswordField txtSenha = new JPasswordField(22);

    public TelaLogin() {
        super("MVM - Entrar");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel corpo = Estilo.corpo();
        JPanel dados = Estilo.secao("Acesso ao sistema");
        Estilo.empilhar(dados, Estilo.campo("Login", true, txtLogin));
        Estilo.empilhar(dados, Estilo.campo("Senha", true, txtSenha));
        Estilo.empilhar(corpo, dados);

        JButton sair = Estilo.botaoSecundario("Sair");
        JButton entrar = Estilo.botaoPrimario("Entrar");
        sair.addActionListener(e -> System.exit(0));
        entrar.addActionListener(e -> entrar());
        getRootPane().setDefaultButton(entrar); // Enter faz o login

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho("Entrar", "Informe seu login e senha."), BorderLayout.NORTH);
        raiz.add(corpo, BorderLayout.CENTER);
        raiz.add(Estilo.rodape(sair, entrar), BorderLayout.SOUTH);
        setContentPane(raiz);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void entrar() {
        try {
            controladora.autenticar(txtLogin.getText(), new String(txtSenha.getPassword()));
            new TelaMenu().setVisible(true);
            dispose();
        } catch (IllegalArgumentException e) {
            txtSenha.setText("");
            Estilo.dialogo(this, Estilo.VERMELHO, TITULO, e.getMessage(), "OK");
        } catch (SQLException e) {
            Estilo.dialogo(this, Estilo.VERMELHO, TITULO, "Erro ao consultar o banco de dados: " + e.getMessage(), "OK");
        }
    }
}
