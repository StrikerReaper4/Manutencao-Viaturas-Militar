package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraAbrirManutencao;
import br.ufms.mvm.controladora.Mensagens;
import br.ufms.mvm.modelo.Filtros;
import br.ufms.mvm.modelo.Manutencao;
import br.ufms.mvm.modelo.Mecanico;
import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.Viatura;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TelaAbrirManutencao extends JFrame {

    private static final String TITULO = "Abrir manutenção";
    private static final String ABRIR = "Abrir";
    private static final String CANCELAR = "Cancelar";

    private final ControladoraAbrirManutencao controladora = new ControladoraAbrirManutencao();

    private long dataAtual;
    private String ebSelecionado;
    private Integer paneSelecionada;
    private Integer odometro;
    private String tipoSelecionado;
    private int[] filtrosSelecionados = new int[0];

    private final JTextField txtData = new JTextField();
    private final JComboBox<Viatura> cbViatura = new JComboBox<>();
    private final JComboBox<Pane> cbPane = new JComboBox<>();
    private final JTextField txtOdometro = new JTextField(10);
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{Manutencao.PREVENTIVA, Manutencao.CORRETIVA});
    private final JComboBox<Mecanico> cbMecanico = new JComboBox<>();
    private final JPanel etiquetasResponsaveis = Estilo.areaEtiquetas();
    private final JPanel painelFiltros = Estilo.secao("Filtros a substituir");
    private final JPanel painelListaFiltros = new JPanel(new GridLayout(0, 3, 12, 4));
    private final List<JCheckBox> checkFiltros = new ArrayList<>();
    private final List<Filtros> filtrosDisponiveis = new ArrayList<>();
    private final JPanel painelPane = Estilo.secao("Dados da pane");
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
        Estilo.ajustarJanela(this);
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel corpo = Estilo.corpo();

        // Identificacao da manutencao
        JPanel ident = Estilo.secao("Identificação da manutenção");
        txtData.setEditable(false);
        Estilo.empilhar(ident, Estilo.linha(
                Estilo.campo("EB da viatura", true, cbViatura),
                Estilo.campo("Odômetro de entrada", true, txtOdometro),
                Estilo.campo("Data de abertura", true, txtData),
                Estilo.campo("Tipo de manutenção", true, cbTipo)));
        Estilo.empilhar(ident, Estilo.campo("Pane a ser atendida", false, cbPane));
        Estilo.empilhar(corpo, ident);

        // Mecanicos responsaveis
        JPanel mec = Estilo.secao("Mecânicos responsáveis");
        JButton btnAdicionar = Estilo.botaoSecundario("Adicionar");
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setOpaque(false);
        lateral.add(btnAdicionar, BorderLayout.SOUTH);
        Estilo.empilhar(mec, Estilo.linhaComLateral(Estilo.campo("Selecionar mecânico", true, cbMecanico), lateral));
        Estilo.empilhar(mec, etiquetasResponsaveis);
        Estilo.empilhar(corpo, mec);

        // Dados da pane (so na corretiva)
        Estilo.empilhar(painelPane, Estilo.linha(
                Estilo.campo("Descrição", false, lblPaneDescricao),
                Estilo.campo("Prioridade", false, lblPanePrioridade),
                Estilo.campo("Missão", false, lblPaneMissao),
                Estilo.campo("Data limite", false, lblPaneDataLimite)));
        painelPane.setVisible(false);
        Estilo.empilhar(corpo, painelPane);

        // Filtros a substituir (so na preventiva)
        painelListaFiltros.setOpaque(false);
        Estilo.empilhar(painelFiltros, Estilo.aviso("Manutenção preventiva: o sistema indicou automaticamente os filtros que necessitam ser substituídos."));
        Estilo.empilhar(painelFiltros, painelListaFiltros);
        painelFiltros.setVisible(false);
        Estilo.empilhar(corpo, painelFiltros);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho("Abrir Manutenção",
                "Registro do início do serviço na oficina. Os campos assinalados com (*) são de preenchimento obrigatório."),
                BorderLayout.NORTH);
        raiz.add(corpo, BorderLayout.CENTER);
        JButton btnAbrir = Estilo.botaoPrimario("Abrir manutenção");
        JButton btnCancelar = Estilo.botaoSecundario("Cancelar");
        raiz.add(Estilo.rodape(btnCancelar, btnAbrir), BorderLayout.SOUTH);
        setContentPane(raiz);

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

    // passos 1 a 3 do DSS: data atual e listas iniciais
    private void abrirManutencao() {
        try {
            dataAtual = controladora.passarData();
            txtData.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date(dataAtual)));

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
                int resp = Estilo.dialogo(this, Estilo.DOURADO, TITULO, Mensagens.m05(eb, emAndamento), "Voltar", "Consultar");
                if (resp == 1) {
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

            List<Pane> panes = controladora.listarPanes(eb);
            carregando = true;
            cbPane.removeAllItems();
            for (Pane p : panes) {
                cbPane.addItem(p);
            }
            cbPane.setSelectedIndex(-1);
            carregando = false;

            // FA03: sem pane, so deixa preventiva
            if (panes.isEmpty()) {
                Estilo.dialogo(this, Estilo.AZUL, TITULO, Mensagens.M06, "OK");
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
        Estilo.ajustarJanela(this);
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
            if (!controladora.validarOdometro(ebSelecionado, valor)) {
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
                Estilo.ajustarJanela(this);
                return;
            }
            carregarFiltros();
            painelFiltros.setVisible(true);
        } else if (Manutencao.CORRETIVA.equals(tipo)) {
            // Variante 2
            exibirDadosPane();
        }
        Estilo.ajustarJanela(this);
    }

    // FAV1.1: se a viatura ainda nao chegou na km da preventiva, pergunta (M09)
    private boolean confirmarPreventivaAntecipada() {
        if (ebSelecionado == null) {
            return true;
        }
        try {
            int faltam = controladora.calcularKmParaPreventiva(ebSelecionado, odometro);
            if (faltam > 0) {
                return Estilo.perguntar(this, TITULO, Mensagens.m09(faltam));
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
                cb.setOpaque(false);
                cb.addActionListener(e -> selecionarFiltros(filtrosMarcados()));
                checkFiltros.add(cb);
                painelListaFiltros.add(cb);
            }
            selecionarFiltros(filtrosMarcados());
            painelListaFiltros.revalidate();
            Estilo.ajustarJanela(this);
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
            etiquetasResponsaveis.removeAll();
            for (Mecanico me : controladora.getMecanicosIncluidos()) {
                Estilo.adicionarEtiqueta(etiquetasResponsaveis, me.toString());
            }
            Estilo.ajustarJanela(this);
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
            if (Estilo.perguntar(this, TITULO, Mensagens.M10)) {
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
            Estilo.dialogo(this, Estilo.VERDE, TITULO, Mensagens.m01(id, ebSelecionado), "OK");
            dispose();
        } catch (IllegalArgumentException e) {
            exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            exibirMensagem(Mensagens.M11);
        }
    }

    public void exibirMensagem(String msg) {
        Estilo.dialogo(this, Estilo.VERMELHO, TITULO, msg, "OK");
    }
}
