package br.ufms.mvm.visao;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;

// Visual das telas, seguindo os mockups
public final class Estilo {

    public static final Color AZUL = new Color(31, 56, 100);
    public static final Color VERDE = new Color(33, 112, 56);
    public static final Color VERMELHO = new Color(158, 42, 43);
    public static final Color DOURADO = new Color(138, 100, 11);
    private static final Color FUNDO = new Color(241, 241, 241);
    private static final Color BORDA = new Color(201, 201, 201);
    private static final Color CINZA = new Color(85, 85, 85);

    private static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font NEGRITO = new Font("Segoe UI", Font.BOLD, 13);

    private Estilo() {
    }

    public static JPanel cabecalho(String titulo, String subtitulo) {
        JLabel barra = new JLabel("MVM — Sistema de Controle de Manutenção de Viaturas Militares");
        barra.setOpaque(true);
        barra.setBackground(AZUL);
        barra.setForeground(Color.WHITE);
        barra.setFont(new Font("Segoe UI", Font.BOLD, 15));
        barra.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(AZUL);
        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(NORMAL);
        lblSub.setForeground(CINZA);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setBackground(FUNDO);
        textos.setBorder(BorderFactory.createEmptyBorder(12, 16, 6, 16));
        textos.add(lblTitulo);
        textos.add(lblSub);

        JPanel p = new JPanel(new BorderLayout());
        p.add(barra, BorderLayout.NORTH);
        p.add(textos, BorderLayout.CENTER);
        return p;
    }

    // painel que empilha os componentes um embaixo do outro, ocupando a largura
    public static JPanel pilha(Color fundo) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(fundo);
        return p;
    }

    public static JPanel corpo() {
        JPanel p = pilha(FUNDO);
        p.setBorder(BorderFactory.createEmptyBorder(4, 16, 8, 16));
        return p;
    }

    public static void empilhar(JPanel pilha, Component c) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = GridBagConstraints.RELATIVE;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 0, 8, 0);
        pilha.add(c, g);
    }

    public static JPanel secao(String titulo) {
        JPanel p = pilha(Color.WHITE);
        TitledBorder borda = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDA), "  " + titulo + "  ");
        borda.setTitleFont(NEGRITO);
        borda.setTitleColor(AZUL);
        p.setBorder(BorderFactory.createCompoundBorder(borda, BorderFactory.createEmptyBorder(8, 12, 6, 12)));
        return p;
    }

    public static JPanel campo(String rotulo, boolean obrigatorio, JComponent componente) {
        String texto = obrigatorio ? "<html>" + rotulo + " <font color='#c0392b'>*</font></html>" : rotulo;
        JLabel lbl = new JLabel(texto);
        lbl.setFont(NORMAL);
        componente.setFont(NORMAL);
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(lbl, BorderLayout.NORTH);
        p.add(componente, BorderLayout.CENTER);
        return p;
    }

    // colunas do mesmo tamanho
    public static JPanel linha(JComponent... componentes) {
        JPanel p = new JPanel(new GridLayout(1, componentes.length, 12, 0));
        p.setOpaque(false);
        for (JComponent c : componentes) {
            p.add(c);
        }
        return p;
    }

    // campo que estica e, na direita, um componente do tamanho dele (ex.: botao Adicionar)
    public static JPanel linhaComLateral(JComponent principal, JComponent lateral) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.add(principal, BorderLayout.CENTER);
        p.add(lateral, BorderLayout.EAST);
        return p;
    }

    public static JPanel areaEtiquetas() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        return p;
    }

    public static void adicionarEtiqueta(JPanel area, String texto) {
        JLabel chip = new JLabel(texto);
        chip.setFont(NORMAL);
        chip.setOpaque(true);
        chip.setBackground(new Color(223, 230, 243));
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(169, 184, 214)),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        JPanel margem = new JPanel(new BorderLayout());
        margem.setOpaque(false);
        margem.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        margem.add(chip);
        area.add(margem);
    }

    public static JLabel aviso(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(NORMAL);
        l.setOpaque(true);
        l.setBackground(new Color(255, 248, 220));
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 196, 108)),
                BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        return l;
    }

    public static JButton botaoPrimario(String texto) {
        return botao(texto, AZUL, Color.WHITE);
    }

    public static JButton botaoSecundario(String texto) {
        return botao(texto, new Color(230, 230, 230), Color.BLACK);
    }

    private static JButton botao(String texto, Color fundo, Color frente) {
        // pinta o fundo na mao porque o visual do Windows ignora a cor do botao
        JButton b = new JButton(texto) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getModel().isPressed() ? fundo.darker() : fundo);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        b.setFont(NEGRITO);
        b.setForeground(frente);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fundo.darker()),
                BorderFactory.createEmptyBorder(7, 20, 7, 20)));
        return b;
    }

    public static JPanel rodape(JButton... botoes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        p.setBackground(FUNDO);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDA));
        for (JButton b : botoes) {
            p.add(b);
        }
        return p;
    }

    // ajusta ao conteudo sem passar do tamanho da tela
    public static void ajustarJanela(JFrame janela) {
        janela.pack();
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = janela.getSize();
        if (d.height > tela.height - 80) {
            janela.setSize(d.width + 20, tela.height - 80);
        }
    }

    // janela de mensagem igual ao mockup; devolve o indice do botao clicado ou -1 se fechou
    public static int dialogo(Component pai, Color cor, String titulo, String texto, String... botoes) {
        final int[] resposta = {-1};
        JDialog d = new JDialog((JFrame) null, titulo, true);
        d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JLabel topo = new JLabel(titulo);
        topo.setOpaque(true);
        topo.setBackground(cor);
        topo.setForeground(Color.WHITE);
        topo.setFont(NEGRITO);
        topo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        String seguro = texto.replace("&", "&amp;").replace("<", "&lt;");
        JLabel msg = new JLabel("<html><body style='width:360px'>" + seguro + "</body></html>", SwingConstants.LEFT);
        msg.setFont(NORMAL);
        msg.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        for (int i = 0; i < botoes.length; i++) {
            final int indice = i;
            JButton b = i == botoes.length - 1 ? botaoPrimario(botoes[i]) : botaoSecundario(botoes[i]);
            b.addActionListener(e -> {
                resposta[0] = indice;
                d.dispose();
            });
            acoes.add(b);
        }

        d.add(topo, BorderLayout.NORTH);
        d.add(msg, BorderLayout.CENTER);
        d.add(acoes, BorderLayout.SOUTH);
        d.pack();
        d.setLocationRelativeTo(pai);
        d.setVisible(true);
        return resposta[0];
    }

    public static boolean perguntar(Component pai, String titulo, String texto) {
        return dialogo(pai, DOURADO, titulo, texto, "Não", "Sim") == 1;
    }
}
