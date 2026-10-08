package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraAbrirManutencao;
import br.ufms.mvm.controladora.Mensagens;
import br.ufms.mvm.modelo.Filtros;
import br.ufms.mvm.modelo.Manutencao;
import br.ufms.mvm.modelo.Mecanico;
import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.Viatura;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TelaAbrirManutencao extends JFrame {

    private static final String ABRIR = "Abrir";
    private static final String CANCELAR = "Cancelar";

    private final ControladoraAbrirManutencao controladora = new ControladoraAbrirManutencao();

    private long dataAtual;
    private String ebSelecionado;
    private Integer paneSelecionada;
    private Integer odometro;
    private String tipoSelecionado;
    private int[] filtrosSelecionados = new int[0];

    private final JLabel lblData = new JLabel();
    private final JComboBox<Viatura> cbViatura = new JComboBox<>();
    private final JComboBox<Pane> cbPane = new JComboBox<>();
    private final JTextField txtOdometro = new JTextField(10);
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{Manutencao.PREVENTIVA, Manutencao.CORRETIVA});
    private final JComboBox<Mecanico> cbMecanico = new JComboBox<>();
    private final DefaultListModel<Mecanico> modeloResponsaveis = new DefaultListModel<>();
    private final JPanel painelFiltros = new JPanel();
    private final JPanel painelListaFiltros = new JPanel();
    private final List<JCheckBox> checkFiltros = new ArrayList<>();
    private final List<Filtros> filtrosDisponiveis = new ArrayList<>();
    private final JPanel painelPane = new JPanel(new GridBagLayout());
    private final JLabel lblPaneDescricao = new JLabel("-");
    private final JLabel lblPanePrioridade = new JLabel("-");
    private final JLabel lblPaneMissao = new JLabel("-");
    private final JLabel lblPaneDataLimite = new JLabel("-");

    // evita disparar os listeners enquanto os combos sao recarregados
    private boolean carregando;

    public TelaAbrirManutencao() {
        super("MVM - Abrir Manutenção");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        montarTela();
        abrirManutencao();
        pack();
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel principal = new JPanel();
        principal.setLayout(new BoxLayout(principal, BoxLayout.Y_AXIS));
        principal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Identificacao da manutencao
        JPanel ident = new JPanel(new GridBagLayout());
        ident.setBorder(BorderFactory.createTitledBorder("Identificação da manutenção"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        adicionarLinha(ident, c, 0, "Data:", lblData);
        adicionarLinha(ident, c, 1, "Viatura (EB):", cbViatura);
        adicionarLinha(ident, c, 2, "Pane a atender:", cbPane);
        adicionarLinha(ident, c, 3, "Odômetro de entrada:", txtOdometro);
        adicionarLinha(ident, c, 4, "Tipo de manutenção:", cbTipo);
        cbPane.setPreferredSize(new Dimension(380, cbPane.getPreferredSize().height));

        // Mecanicos responsaveis
        JPanel mec = new JPanel(new BorderLayout(5, 5));
        mec.setBorder(BorderFactory.createTitledBorder("Mecânicos responsáveis"));
        JPanel linhaMec = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdicionar = new JButton("Adicionar");
        linhaMec.add(cbMecanico);
        linhaMec.add(btnAdicionar);
        JList<Mecanico> listaResponsaveis = new JList<>(modeloResponsaveis);
        JScrollPane scrollMec = new JScrollPane(listaResponsaveis);
        scrollMec.setPreferredSize(new Dimension(380, 80));
        mec.add(linhaMec, BorderLayout.NORTH);
        mec.add(scrollMec, BorderLayout.CENTER);

        // Filtros a substituir (so na preventiva)
        painelFiltros.setLayout(new BorderLayout());
        painelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros a substituir"));
        painelListaFiltros.setLayout(new BoxLayout(painelListaFiltros, BoxLayout.Y_AXIS));
        painelFiltros.add(painelListaFiltros, BorderLayout.CENTER);
        painelFiltros.setVisible(false);

        // Dados da pane (so na corretiva)
        painelPane.setBorder(BorderFactory.createTitledBorder("Dados da pane"));
        GridBagConstraints cp = new GridBagConstraints();
        cp.insets = new Insets(2, 4, 2, 4);
        cp.anchor = GridBagConstraints.WEST;
        cp.fill = GridBagConstraints.HORIZONTAL;
        adicionarLinha(painelPane, cp, 0, "Descrição:", lblPaneDescricao);
        adicionarLinha(painelPane, cp, 1, "Prioridade:", lblPanePrioridade);
        adicionarLinha(painelPane, cp, 2, "Missão:", lblPaneMissao);
        adicionarLinha(painelPane, cp, 3, "Data limite:", lblPaneDataLimite);
        painelPane.setVisible(false);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAbrir = new JButton("Abrir manutenção");
        JButton btnCancelar = new JButton("Cancelar");
        botoes.add(btnAbrir);
        botoes.add(btnCancelar);

        principal.add(ident);
        principal.add(mec);
        principal.add(painelPane);
        principal.add(painelFiltros);
        principal.add(botoes);
        setContentPane(principal);

        // eventos
        cbViatura.addActionListener(e -> {
            if (!carregando && cbViatura.getSelectedItem() != null) {
                selecionarViatura(((Viatura) cbViatura.getSelectedItem()).getEb());
            }
        });
        cbPane.addActionListener(e -> {
            if (!carregando) {
                Pane p = (Pane) cbPane.getSelectedItem();
                selecionarPane(p != null ? p.getIdPane() : null);
            }
        });
        txtOdometro.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                lerOdometro();
            }
        });
        cbTipo.addActionListener(e -> {
            if (!carregando) {
                selecionarTipoManutencao((String) cbTipo.getSelectedItem());
            }
        });
        btnAdicionar.addActionListener(e -> {
            Mecanico me = (Mecanico) cbMecanico.getSelectedItem();
            if (me != null) {
                adicionarMecanico(me.getIdUsuario());
            }
        });
        btnAbrir.addActionListener(e -> selecionarOpcao(ABRIR));
        btnCancelar.addActionListener(e -> selecionarOpcao(CANCELAR));
    }

    private void adicionarLinha(JPanel painel, GridBagConstraints c, int linha, String rotulo, java.awt.Component campo) {
        c.gridx = 0;
        c.gridy = linha;
        c.weightx = 0;
        painel.add(new JLabel(rotulo), c);
        c.gridx = 1;
        c.weightx = 1;
        painel.add(campo, c);
    }

    // passos 1 a 3 do DSS: data atual e listas iniciais
    private void abrirManutencao() {
        try {
            dataAtual = controladora.passarData();
            lblData.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(dataAtual)));

            carregando = true;
            cbViatura.removeAllItems();
            for (Viatura v : controladora.consultarViaturasAtivas()) {
                cbViatura.addItem(v);
            }
            cbViatura.setSelectedIndex(-1);

            cbMecanico.removeAllItems();
            for (Mecanico me : controladora.consultarMecanicosAtivos()) {
                cbMecanico.addItem(me);
            }
            cbTipo.setSelectedIndex(-1);
            carregando = false;
        } catch (SQLException e) {
            carregando = false;
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    public void selecionarViatura(String eb) {
        ebSelecionado = eb;
        paneSelecionada = null;
        try {
            // FA02
            Integer emAndamento = controladora.buscarManutencaoEmAndamento(eb);
            if (emAndamento != null) {
                // A consulta de manutencao eh de outra iteracao, por enquanto "Consultar" so encerra
                Object[] opcoes = {"Consultar", "Voltar"};
                int resp = JOptionPane.showOptionDialog(this, Mensagens.m05(eb, emAndamento), "MVM",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[1]);
                if (resp == JOptionPane.YES_OPTION) {
                    controladora.cancelarAbertura();
                    System.exit(0);
                }
                carregando = true;
                cbViatura.setSelectedIndex(-1);
                cbPane.removeAllItems();
                carregando = false;
                ebSelecionado = null;
                return;
            }

            List<Pane> panes = controladora.listarPane(eb);
            carregando = true;
            cbPane.removeAllItems();
            for (Pane p : panes) {
                cbPane.addItem(p);
            }
            cbPane.setSelectedIndex(-1);
            carregando = false;

            // FA03: sem pane, so deixa preventiva
            if (panes.isEmpty()) {
                exibirMensagem(Mensagens.M06);
                cbTipo.setSelectedItem(Manutencao.PREVENTIVA);
                cbTipo.setEnabled(false);
            } else {
                cbTipo.setEnabled(true);
            }

            // se o tipo ja estava escolhido, refaz a variante pra nova viatura
            if (tipoSelecionado != null && cbTipo.isEnabled()) {
                selecionarTipoManutencao(tipoSelecionado);
            }
        } catch (SQLException e) {
            carregando = false;
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    public void selecionarPane(Integer paneID) {
        paneSelecionada = paneID;
        if (Manutencao.CORRETIVA.equals(tipoSelecionado)) {
            exibirDadosPane();
        }
    }

    // Variante 2: mostra os dados da pane escolhida
    private void exibirDadosPane() {
        Pane p = null;
        try {
            if (paneSelecionada != null) {
                p = controladora.buscarPane(paneSelecionada);
            }
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }

        if (p == null) {
            lblPaneDescricao.setText("-");
            lblPanePrioridade.setText("-");
            lblPaneMissao.setText("-");
            lblPaneDataLimite.setText("-");
        } else {
            lblPaneDescricao.setText(p.getDescricao());
            lblPanePrioridade.setText(String.valueOf(p.getPrioridade()));
            lblPaneMissao.setText(p.getMissao() != null ? p.getMissao() : "-");
            lblPaneDataLimite.setText(p.getDataLimite() != null
                    ? new SimpleDateFormat("dd/MM/yyyy").format(new Date(p.getDataLimite())) : "-");
        }
        painelPane.setVisible(true);
        pack();
    }

    private void lerOdometro() {
        String texto = txtOdometro.getText().trim();
        if (texto.isEmpty()) {
            odometro = null;
            return;
        }
        try {
            informarOdometro(Integer.parseInt(texto));
        } catch (NumberFormatException e) {
            exibirMensagem("O campo Odômetro de entrada aceita apenas números.");
            txtOdometro.setText("");
            odometro = null;
        }
    }

    public void informarOdometro(int valor) {
        odometro = valor;
        if (ebSelecionado == null) {
            return;
        }
        try {
            // FA05
            if (!controladora.validarOdometro(valor, ebSelecionado)) {
                int atual = controladora.buscarOdometroAtual(ebSelecionado);
                exibirMensagem(Mensagens.m03(valor, atual));
                odometro = null;
                txtOdometro.setText("");
                txtOdometro.requestFocusInWindow();
            }
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    public void selecionarTipoManutencao(String tipo) {
        tipoSelecionado = tipo;
        painelFiltros.setVisible(false);
        painelPane.setVisible(false);
        filtrosSelecionados = new int[0];

        if (Manutencao.PREVENTIVA.equals(tipo)) {
            // Variante 1 (passando antes pelo FAV1.1)
            if (!confirmarPreventivaAntecipada()) {
                tipoSelecionado = null;
                carregando = true;
                cbTipo.setSelectedIndex(-1);
                carregando = false;
                pack();
                return;
            }
            carregarFiltros();
            painelFiltros.setVisible(true);
        } else if (Manutencao.CORRETIVA.equals(tipo)) {
            // Variante 2
            exibirDadosPane();
        }
        pack();
    }

    // FAV1.1: se a viatura ainda nao chegou na km da preventiva, pergunta (M09)
    private boolean confirmarPreventivaAntecipada() {
        if (ebSelecionado == null) {
            return true;
        }
        try {
            int faltam = controladora.calcularKmParaPreventiva(ebSelecionado, odometro);
            if (faltam > 0) {
                int resp = JOptionPane.showConfirmDialog(this, Mensagens.m09(faltam), "MVM",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                return resp == JOptionPane.YES_OPTION;
            }
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
        return true;
    }

    // Variante 1: o sistema ja traz os filtros marcados
    private void carregarFiltros() {
        if (ebSelecionado == null) {
            return;
        }
        try {
            filtrosDisponiveis.clear();
            filtrosDisponiveis.addAll(controladora.listarFiltros(ebSelecionado));
            painelListaFiltros.removeAll();
            checkFiltros.clear();
            for (Filtros f : filtrosDisponiveis) {
                JCheckBox cb = new JCheckBox(f.toString(), true);
                cb.addActionListener(e -> selecionarFiltros(filtrosMarcados()));
                checkFiltros.add(cb);
                painelListaFiltros.add(cb);
            }
            selecionarFiltros(filtrosMarcados());
            painelListaFiltros.revalidate();
            pack();
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    private List<Filtros> filtrosMarcados() {
        List<Filtros> marcados = new ArrayList<>();
        for (int i = 0; i < checkFiltros.size(); i++) {
            if (checkFiltros.get(i).isSelected()) {
                marcados.add(filtrosDisponiveis.get(i));
            }
        }
        return marcados;
    }

    public void selecionarFiltros(List<Filtros> listaFiltros) {
        filtrosSelecionados = listaFiltros.stream().mapToInt(Filtros::getIdFiltro).toArray();
    }

    public void adicionarMecanico(int idMecanico) {
        try {
            controladora.incluirMecanico(idMecanico);
            modeloResponsaveis.clear();
            for (Mecanico me : controladora.getMecanicosIncluidos()) {
                modeloResponsaveis.addElement(me);
            }
        } catch (IllegalArgumentException e) {
            exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    public void selecionarOpcao(String opcao) {
        if (ABRIR.equals(opcao)) {
            confirmarAbertura();
        } else if (CANCELAR.equals(opcao)) {
            // Variante 4
            int resp = JOptionPane.showConfirmDialog(this, Mensagens.M10, "Cancelar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                controladora.cancelarAbertura();
                dispose();
            }
        }
    }

    // Variante 3
    private void confirmarAbertura() {
        lerOdometro();
        if (ebSelecionado == null) {
            exibirMensagem(Mensagens.m02("Viatura (EB)"));
            return;
        }
        if (odometro == null) {
            exibirMensagem(Mensagens.m02("Odômetro de entrada"));
            return;
        }
        if (tipoSelecionado == null) {
            exibirMensagem(Mensagens.m02("Tipo de manutenção"));
            return;
        }

        try {
            int id = controladora.gravarManutencao(ebSelecionado, paneSelecionada, odometro,
                    tipoSelecionado, dataAtual, filtrosSelecionados);
            exibirMensagem(Mensagens.m01(id, ebSelecionado));
            dispose();
        } catch (IllegalArgumentException e) {
            exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            exibirMensagem(Mensagens.M11);
        }
    }

    public void exibirMensagem(String msg) {
        JOptionPane.showMessageDialog(this, msg, "MVM", JOptionPane.INFORMATION_MESSAGE);
    }
}
