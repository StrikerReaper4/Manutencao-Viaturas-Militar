package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraAutenticarUsuario;
import br.ufms.mvm.controladora.Sessao;
import br.ufms.mvm.modelo.Usuario;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.function.Supplier;

// Menu principal: so aparece o que a classe do usuario pode fazer (RF05 / RNF05)
public class TelaMenu extends JFrame {

    public TelaMenu() {
        super("MVM - Menu principal");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Usuario u = Sessao.getUsuarioLogado();
        boolean operador = Sessao.isOperador();

        JPanel corpo = Estilo.corpo();

        JPanel manut = Estilo.secao("Manutenções");
        JPanel botoesManut = grade();
        botoesManut.add(botao("Abrir manutenção", TelaAbrirManutencao::new));
        botoesManut.add(botao("Editar manutenção", TelaEditarManutencao::new));
        botoesManut.add(botao("Encerrar manutenção", TelaEncerrarManutencao::new));
        if (operador) {
            botoesManut.add(botao("Registrar pane", TelaRegistrarPane::new));
        }
        Estilo.empilhar(manut, botoesManut);
        Estilo.empilhar(corpo, manut);

        JPanel cons = Estilo.secao("Consultas");
        JPanel botoesCons = grade();
        botoesCons.add(botao("Manutenções em andamento", TelaConsultarEmAndamento::new));
        botoesCons.add(botao("Manutenções preventivas", TelaConsultarPreventivas::new));
        Estilo.empilhar(cons, botoesCons);
        Estilo.empilhar(corpo, cons);

        JPanel cad = Estilo.secao("Cadastros");
        JPanel botoesCad = grade();
        botoesCad.add(botao("Gerenciar item", TelaGerenciarItem::new));
        if (operador) {
            botoesCad.add(botao("Gerenciar viatura", TelaGerenciarViatura::new));
            botoesCad.add(botao("Gerenciar usuário", TelaGerenciarUsuario::new));
            botoesCad.add(botao("Atualizar situação de registro", TelaAtualizarSituacao::new));
        }
        Estilo.empilhar(cad, botoesCad);
        Estilo.empilhar(corpo, cad);

        JButton sair = Estilo.botaoSecundario("Sair");
        sair.addActionListener(e -> {
            new ControladoraAutenticarUsuario().sair();
            dispose();
            new TelaLogin().setVisible(true);
        });

        String classe = operador ? "Operador" : "Mecânico";
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho("Menu principal", "Usuário: " + u + " (" + classe + ")"), BorderLayout.NORTH);
        raiz.add(corpo, BorderLayout.CENTER);
        raiz.add(Estilo.rodape(sair), BorderLayout.SOUTH);
        setContentPane(raiz);
        pack();
        setLocationRelativeTo(null);
    }

    private static JPanel grade() {
        JPanel p = new JPanel(new GridLayout(0, 3, 10, 10));
        p.setOpaque(false);
        return p;
    }

    private JButton botao(String texto, Supplier<JFrame> tela) {
        JButton b = Estilo.botaoPrimario(texto);
        b.addActionListener(e -> {
            try {
                tela.get().setVisible(true);
            } catch (IllegalStateException ex) {
                Estilo.dialogo(this, Estilo.VERMELHO, texto, ex.getMessage(), "OK");
            }
        });
        return b;
    }
}
