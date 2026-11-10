package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraEncerrarManutencao;
import br.ufms.mvm.controladora.MensagensEncerrar;
import br.ufms.mvm.modelo.Filtros;
import br.ufms.mvm.modelo.Item;
import br.ufms.mvm.modelo.ItemManutencao_Material;
import br.ufms.mvm.modelo.Manutencao;
import br.ufms.mvm.modelo.Mecanico;
import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.Viatura;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TelaEncerrarManutencao extends JFrame {

    private static final String TITULO = "Encerrar manutenção";
    private static final String ENCERRAR = "Encerrar";
    private static final String CANCELAR = "Cancelar";
    private static final String EM_ANDAMENTO = "EmAndamento";
    private static final String SEM_ABERTURA = "SemAbertura";
    private static final String[] SITUACOES = {Viatura.DISPONIVEL, Viatura.INDISPONIVEL, Viatura.DISPONIVEL_RESTRICAO};

    private final ControladoraEncerrarManutencao controladora = new ControladoraEncerrarManutencao();
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

    private long dataAtual;
    private String origem = EM_ANDAMENTO;
    private Manutencao manutencaoSelecionada;
    private String ebSelecionado;
    private Integer paneSelecionada;
    private Integer odometroSaida;
    private String tipoSelecionado;
    private String situacaoFinal;
    private boolean servicoConcluido = true;
    private String servicoFaltante;
    private List<Filtros> filtrosInformados = new ArrayList<>();

    private final JRadioButton rbAndamento = new JRadioButton("Manutenção em andamento", true);
    private final JRadioButton rbSemAbertura = new JRadioButton("Manutenção executada sem abertura prévia");
    private final JComboBox<Manutencao> cbManutencao = new JComboBox<>();
    private final JComboBox<Viatura> cbViatura = new JComboBox<>();
    private final JComboBox<Pane> cbPane = new JComboBox<>();
    private final JTextField txtDataAbertura = new JTextField();
    private final JTextField txtOdometroEntrada = new JTextField();
    private final JTextField txtOdometroSaida = new JTextField();
    private final JTextField txtDataEncerramento = new JTextField();
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{Manutencao.PREVENTIVA, Manutencao.CORRETIVA});
    private final JComboBox<String> cbSituacao = new JComboBox<>(SITUACOES);
    private final JComboBox<Mecanico> cbMecanico = new JComboBox<>();
    private final JComboBox<Item> cbItem = new JComboBox<>();
    private final JTextField txtQuantidade = new JTextField();
    private final JComboBox<Item> cbMaterial = new JComboBox<>();
    private final JTextField txtQuantidadeMaterial = new JTextField();
    private final JCheckBox ckNaoConcluido = new JCheckBox("Serviço não concluído");
    private final JTextField txtServico = new JTextField();

    private final JPanel linhaManutencao;
    private final JPanel linhaVariante;
    private final JPanel blocoMecanicos = Estilo.pilha(Color.WHITE);
    private final JPanel etiquetasMecanicos = Estilo.areaEtiquetas();
    private final JPanel etiquetasItens = Estilo.areaEtiquetas();
    private final JPanel etiquetasMateriais = Estilo.areaEtiquetas();
    private final JPanel secaoFiltros = Estilo.secao("Filtros substituídos");
    private final JPanel tabelaFiltros = new JPanel(new GridBagLayout());
    private final JPanel secaoPane = Estilo.secao("Dados da pane");
    private final JLabel lblPane = new JLabel("-");
    private final JPanel blocoFaltantes = Estilo.pilha(Color.WHITE);
    private final JButton btnAdicionarMaterial = Estilo.botaoSecundario("Adicionar");

    private final List<LinhaFiltro> linhasFiltro = new ArrayList<>();

    // evita disparar os listeners enquanto os combos sao recarregados
    private boolean carregando;

    private static class LinhaFiltro {
        Filtros filtro;
        JCheckBox marcado;
        JTextField especificacao;
        JTextField volume;
    }

    public TelaEncerrarManutencao() {
        super("MVM - Encerrar Manutenção");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        linhaManutencao = Estilo.linha(Estilo.campo("Manutenção em andamento", true, cbManutencao));
        linhaVariante = Estilo.linha(
                Estilo.campo("EB da viatura", true, cbViatura),
                Estilo.campo("Data de abertura (dd/mm/aaaa)", true, txtDataAbertura),
                Estilo.campo("Pane atendida", false, cbPane));
        montarTela();
        encerrarManutencao();
        Estilo.ajustarJanela(this);
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel corpo = Estilo.corpo();

        JPanel ident = Estilo.secao("Identificação da manutenção");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbAndamento);
        grupo.add(rbSemAbertura);
        rbAndamento.setOpaque(false);
        rbSemAbertura.setOpaque(false);
        JPanel radios = Estilo.linha(rbAndamento, rbSemAbertura);
        Estilo.empilhar(ident, Estilo.campo("Origem do registro", true, radios));
        Estilo.empilhar(ident, linhaManutencao);
        Estilo.empilhar(ident, linhaVariante);
        txtOdometroEntrada.setEditable(false);
        txtDataEncerramento.setEditable(false);
        Estilo.empilhar(ident, Estilo.linha(
                Estilo.campo("Odômetro de entrada", true, txtOdometroEntrada),
                Estilo.campo("Odômetro de saída", true, txtOdometroSaida),
                Estilo.campo("Data de encerramento", true, txtDataEncerramento),
                Estilo.campo("Tipo de manutenção", true, cbTipo),
                Estilo.campo("Situação final da viatura", true, cbSituacao)));

        JButton btnAdicionarMecanico = Estilo.botaoSecundario("Adicionar");
        Estilo.empilhar(blocoMecanicos, Estilo.linhaComLateral(
                Estilo.campo("Mecânicos responsáveis", true, cbMecanico), noFundo(btnAdicionarMecanico)));
        Estilo.empilhar(blocoMecanicos, etiquetasMecanicos);
        Estilo.empilhar(ident, blocoMecanicos);
        Estilo.empilhar(corpo, ident);

        Estilo.empilhar(secaoPane, lblPane);
        Estilo.empilhar(corpo, secaoPane);

        JPanel itens = Estilo.secao("Itens utilizados");
        JButton btnAdicionarItem = Estilo.botaoSecundario("Adicionar");
        txtQuantidade.setPreferredSize(new Dimension(100, txtQuantidade.getPreferredSize().height));
        Estilo.empilhar(itens, Estilo.linhaComLateral(Estilo.campo("Selecionar item", false, cbItem),
                lateral(Estilo.campo("Quantidade", false, txtQuantidade), btnAdicionarItem)));
        Estilo.empilhar(itens, etiquetasItens);
        Estilo.empilhar(corpo, itens);

        tabelaFiltros.setOpaque(false);
        Estilo.empilhar(secaoFiltros, Estilo.aviso("Manutenção preventiva: confirme os filtros substituídos e informe a especificação e o volume de óleo."));
        Estilo.empilhar(secaoFiltros, tabelaFiltros);
        Estilo.empilhar(corpo, secaoFiltros);

        JPanel faltantes = Estilo.secao("Serviço e material faltantes");
        ckNaoConcluido.setOpaque(false);
        Estilo.empilhar(faltantes, ckNaoConcluido);
        Estilo.empilhar(blocoFaltantes, Estilo.campo("Serviço faltante", true, txtServico));
        txtQuantidadeMaterial.setPreferredSize(new Dimension(100, txtQuantidadeMaterial.getPreferredSize().height));
        Estilo.empilhar(blocoFaltantes, Estilo.linhaComLateral(Estilo.campo("Material faltante", false, cbMaterial),
                lateral(Estilo.campo("Quantidade", false, txtQuantidadeMaterial), btnAdicionarMaterial)));
        Estilo.empilhar(blocoFaltantes, etiquetasMateriais);
        Estilo.empilhar(faltantes, blocoFaltantes);
        Estilo.empilhar(corpo, faltantes);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho("Encerrar Manutenção",
                "Registro do término do serviço na oficina. Os campos assinalados com (*) são de preenchimento obrigatório."),
                BorderLayout.NORTH);
        JPanel topoCorpo = new JPanel(new BorderLayout());
        topoCorpo.setBackground(new Color(241, 241, 241));
        topoCorpo.add(corpo, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(topoCorpo);
        rolagem.setBorder(BorderFactory.createEmptyBorder());
        rolagem.getVerticalScrollBar().setUnitIncrement(16);
        raiz.add(rolagem, BorderLayout.CENTER);

        JButton btnCancelar = Estilo.botaoSecundario("Cancelar");
        JButton btnEncerrar = Estilo.botaoPrimario("Encerrar manutenção");
        raiz.add(Estilo.rodape(btnCancelar, btnEncerrar), BorderLayout.SOUTH);
        setContentPane(raiz);

        rbAndamento.addActionListener(e -> selecionarOrigem(EM_ANDAMENTO));
        rbSemAbertura.addActionListener(e -> selecionarOrigem(SEM_ABERTURA));
        cbManutencao.addActionListener(e -> {
            if (!carregando && cbManutencao.getSelectedItem() != null) {
                selecionarManutencao(((Manutencao) cbManutencao.getSelectedItem()).getIdManutencao());
            }
        });
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
        cbTipo.addActionListener(e -> {
            if (!carregando) {
                selecionarTipoManutencao((String) cbTipo.getSelectedItem());
            }
        });
        cbSituacao.addActionListener(e -> {
            if (!carregando) {
                selecionarSituacaoFinal((String) cbSituacao.getSelectedItem());
            }
        });
        txtOdometroSaida.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (EM_ANDAMENTO.equals(origem) && manutencaoSelecionada != null) {
                    lerOdometroSaida();
                }
            }
        });
        btnAdicionarMecanico.addActionListener(e -> {
            Mecanico me = (Mecanico) cbMecanico.getSelectedItem();
            if (me != null) {
                adicionarMecanico(me.getIdUsuario());
            }
        });
        btnAdicionarItem.addActionListener(e -> {
            Item it = (Item) cbItem.getSelectedItem();
            if (it != null) {
                adicionarItem(it.getIdItem(), lerQuantidade(txtQuantidade));
            }
        });
        btnAdicionarMaterial.addActionListener(e -> {
            Item it = (Item) cbMaterial.getSelectedItem();
            if (it != null) {
                adicionarMaterialFaltante(it.getIdItem(), lerQuantidade(txtQuantidadeMaterial));
            }
        });
        ckNaoConcluido.addActionListener(e -> informarConclusao(!ckNaoConcluido.isSelected()));
        btnEncerrar.addActionListener(e -> selecionarOpcao(ENCERRAR));
        btnCancelar.addActionListener(e -> selecionarOpcao(CANCELAR));

        linhaVariante.setVisible(false);
        blocoMecanicos.setVisible(false);
        secaoFiltros.setVisible(false);
        secaoPane.setVisible(false);
        blocoFaltantes.setVisible(false);
    }

    // alinha o botao embaixo, na altura do campo
    private JPanel noFundo(JButton botao) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(botao, BorderLayout.SOUTH);
        return p;
    }

    private JPanel lateral(JComponent campo, JButton botao) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.add(campo, BorderLayout.CENTER);
        p.add(noFundo(botao), BorderLayout.EAST);
        return p;
    }

    private int lerQuantidade(JTextField campo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // mensagens 1 a 3 do DSS e listas iniciais
    private void encerrarManutencao() {
        try {
            dataAtual = controladora.passarData();
            txtDataEncerramento.setText(formatoData.format(new Date(dataAtual)));

            carregando = true;
            List<Manutencao> emAndamento = controladora.consultarManutencoesEmAndamento();
            for (Manutencao m : emAndamento) {
                cbManutencao.addItem(m);
            }
            cbManutencao.setSelectedIndex(-1);
            for (Viatura v : controladora.consultarViaturasAtivas()) {
                cbViatura.addItem(v);
            }
            cbViatura.setSelectedIndex(-1);
            for (Mecanico me : controladora.consultarMecanicosAtivos()) {
                cbMecanico.addItem(me);
            }
            for (Item it : controladora.consultarItensAtivos()) {
                cbItem.addItem(it);
                cbMaterial.addItem(it);
            }
            cbTipo.setSelectedIndex(-1);
            cbSituacao.setSelectedIndex(-1);
            carregando = false;

            // FA01
            if (emAndamento.isEmpty()) {
                exibir(Estilo.AZUL, MensagensEncerrar.M05);
                rbAndamento.setEnabled(false);
                rbSemAbertura.setSelected(true);
                selecionarOrigem(SEM_ABERTURA);
            }
        } catch (SQLException e) {
            carregando = false;
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    public void selecionarOrigem(String novaOrigem) {
        origem = novaOrigem;
        boolean variante = SEM_ABERTURA.equals(origem);
        linhaManutencao.setVisible(!variante);
        linhaVariante.setVisible(variante);
        blocoMecanicos.setVisible(variante);
        txtOdometroEntrada.setEditable(variante);
        txtOdometroEntrada.setText("");
        manutencaoSelecionada = null;
        ebSelecionado = null;
        paneSelecionada = null;
        carregando = true;
        cbManutencao.setSelectedIndex(-1);
        cbViatura.setSelectedIndex(-1);
        cbPane.removeAllItems();
        cbTipo.setSelectedIndex(-1);
        carregando = false;
        tipoSelecionado = null;
        secaoFiltros.setVisible(false);
        secaoPane.setVisible(false);
        Estilo.ajustarJanela(this);
    }

    public void selecionarManutencao(int idManutencao) {
        try {
            manutencaoSelecionada = controladora.buscarManutencao(idManutencao);
            ebSelecionado = manutencaoSelecionada.getEb();
            paneSelecionada = manutencaoSelecionada.getIdPane();
            txtOdometroEntrada.setText(String.valueOf(manutencaoSelecionada.getOdometroEntrada()));
            carregando = true;
            cbTipo.setSelectedItem(manutencaoSelecionada.getTipo());
            carregando = false;
            aplicarTipo(manutencaoSelecionada.getTipo());
        } catch (SQLException e) {
            carregando = false;
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    public void selecionarViatura(String eb) {
        try {
            // FAV1.2
            Integer emAndamento = controladora.buscarManutencaoEmAndamento(eb);
            if (emAndamento != null) {
                exibirMensagem(MensagensEncerrar.m13(eb, emAndamento));
                carregando = true;
                cbViatura.setSelectedIndex(-1);
                cbPane.removeAllItems();
                carregando = false;
                ebSelecionado = null;
                return;
            }
            ebSelecionado = eb;
            paneSelecionada = null;
            carregando = true;
            cbPane.removeAllItems();
            for (Pane p : controladora.listarPanes(eb)) {
                cbPane.addItem(p);
            }
            cbPane.setSelectedIndex(-1);
            carregando = false;
            if (tipoSelecionado != null) {
                aplicarTipo(tipoSelecionado);
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

    private void lerOdometroSaida() {
        String texto = txtOdometroSaida.getText().trim();
        if (texto.isEmpty()) {
            odometroSaida = null;
            return;
        }
        try {
            informarOdometroSaida(Integer.parseInt(texto));
        } catch (NumberFormatException e) {
            exibirMensagem("O campo Odômetro de saída aceita apenas números.");
            txtOdometroSaida.setText("");
            odometroSaida = null;
        }
    }

    public void informarOdometroSaida(int valor) {
        odometroSaida = valor;
        // FA05
        if (!controladora.validarOdometroSaida(valor)) {
            exibirMensagem(MensagensEncerrar.m03(valor, controladora.getOdometroEntrada()));
            odometroSaida = null;
            txtOdometroSaida.setText("");
            txtOdometroSaida.requestFocusInWindow();
        }
    }

    public void selecionarTipoManutencao(String tipo) {
        // FA06
        boolean eraPreventiva = manutencaoSelecionada != null && Manutencao.PREVENTIVA.equals(manutencaoSelecionada.getTipo());
        if (eraPreventiva && Manutencao.CORRETIVA.equals(tipo)
                && !Estilo.perguntar(this, TITULO, MensagensEncerrar.M09)) {
            carregando = true;
            cbTipo.setSelectedItem(Manutencao.PREVENTIVA);
            carregando = false;
            return;
        }
        aplicarTipo(tipo);
    }

    private void aplicarTipo(String tipo) {
        tipoSelecionado = tipo;
        secaoFiltros.setVisible(false);
        secaoPane.setVisible(false);
        if (Manutencao.PREVENTIVA.equals(tipo)) {
            carregarFiltros();   // Variante 2
            secaoFiltros.setVisible(true);
        } else if (Manutencao.CORRETIVA.equals(tipo)) {
            exibirDadosPane();   // Variante 3
        }
        Estilo.ajustarJanela(this);
    }

    private void exibirDadosPane() {
        String texto = "Nenhuma pane informada.";
        try {
            Pane p = paneSelecionada != null ? controladora.buscarPane(paneSelecionada) : null;
            if (p != null) {
                String limite = p.getDataLimite() != null ? formatoData.format(new Date(p.getDataLimite())) : "-";
                String missao = p.getMissao() != null ? p.getMissao() : "-";
                texto = p.getDescricao() + "  |  Prioridade: " + p.getPrioridade() + "  |  Missão: " + missao
                        + "  |  Data limite: " + limite;
            }
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
        lblPane.setText(texto);
        secaoPane.setVisible(true);
        Estilo.ajustarJanela(this);
    }

    private void carregarFiltros() {
        tabelaFiltros.removeAll();
        linhasFiltro.clear();
        if (ebSelecionado == null) {
            return;
        }
        adicionarCelula(new JLabel("Filtro"), 0, 0, 0.35);
        adicionarCelula(new JLabel("Especificação"), 1, 0, 0.4);
        adicionarCelula(new JLabel("Volume de óleo (L)"), 2, 0, 0.25);
        try {
            Integer id = manutencaoSelecionada != null ? manutencaoSelecionada.getIdManutencao() : null;
            int y = 1;
            for (Filtros f : controladora.listarFiltros(id, ebSelecionado)) {
                LinhaFiltro l = new LinhaFiltro();
                l.filtro = f;
                l.marcado = new JCheckBox(f.getTipo(), true);
                l.marcado.setOpaque(false);
                l.especificacao = new JTextField(f.getEspecificacao());
                l.volume = new JTextField();
                // o volume so faz sentido no filtro de oleo
                l.volume.setEnabled(temOleo(f));
                l.marcado.addActionListener(e -> {
                    l.especificacao.setEnabled(l.marcado.isSelected());
                    l.volume.setEnabled(l.marcado.isSelected() && temOleo(l.filtro));
                });
                adicionarCelula(l.marcado, 0, y, 0.35);
                adicionarCelula(l.especificacao, 1, y, 0.4);
                adicionarCelula(l.volume, 2, y, 0.25);
                linhasFiltro.add(l);
                y++;
            }
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
        tabelaFiltros.revalidate();
    }

    private boolean temOleo(Filtros f) {
        return f.getTipo().toLowerCase().contains("óleo");
    }

    private void adicionarCelula(JComponent c, int x, int y, double peso) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.weightx = peso;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(2, 0, 2, 12);
        tabelaFiltros.add(c, g);
    }

    // Monta a lista com o que foi digitado; devolve falso se faltou algum campo
    private boolean lerFiltros() {
        List<Filtros> lista = new ArrayList<>();
        for (LinhaFiltro l : linhasFiltro) {
            if (!l.marcado.isSelected()) {
                continue;
            }
            String especificacao = l.especificacao.getText().trim();
            if (especificacao.isEmpty()) {
                exibirMensagem(MensagensEncerrar.m02("Especificação (" + l.filtro.getTipo() + ")"));
                return false;
            }
            Double volume = null;
            if (temOleo(l.filtro)) {
                try {
                    volume = Double.valueOf(l.volume.getText().trim().replace(',', '.'));
                } catch (NumberFormatException e) {
                    exibirMensagem(MensagensEncerrar.m02("Volume de óleo (" + l.filtro.getTipo() + ")"));
                    return false;
                }
            }
            Filtros f = new Filtros(l.filtro.getIdFiltro(), l.filtro.getTipo(), especificacao);
            f.setVolumeOleo(volume);
            lista.add(f);
        }
        informarFiltros(lista);
        return true;
    }

    public void informarFiltros(List<Filtros> listaFiltros) {
        filtrosInformados = listaFiltros;
    }

    public void adicionarMecanico(int idMecanico) {
        try {
            controladora.incluirMecanico(idMecanico);
            etiquetasMecanicos.removeAll();
            for (Mecanico me : controladora.getMecanicosIncluidos()) {
                Estilo.adicionarEtiqueta(etiquetasMecanicos, me.toString());
            }
            Estilo.ajustarJanela(this);
        } catch (IllegalArgumentException e) {
            exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    public void adicionarItem(int idItem, int quantidade) {
        try {
            controladora.incluirItem(idItem, quantidade);
            redesenhar(etiquetasItens, controladora.getItensIncluidos());
            txtQuantidade.setText("");
        } catch (IllegalArgumentException e) {
            exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    public void adicionarMaterialFaltante(int idItem, int quantidade) {
        try {
            controladora.incluirMaterialFaltante(idItem, quantidade);
            redesenhar(etiquetasMateriais, controladora.getMateriaisFaltantes());
            txtQuantidadeMaterial.setText("");
        } catch (IllegalArgumentException e) {
            exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            exibirMensagem("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }

    private void redesenhar(JPanel area, List<ItemManutencao_Material> lista) {
        area.removeAll();
        for (ItemManutencao_Material it : lista) {
            Estilo.adicionarEtiqueta(area, it.toString());
        }
        Estilo.ajustarJanela(this);
    }

    public void selecionarSituacaoFinal(String situacao) {
        situacaoFinal = situacao;
    }

    public void informarConclusao(boolean concluido) {
        servicoConcluido = concluido;
        blocoFaltantes.setVisible(!concluido);
        Estilo.ajustarJanela(this);
    }

    public void informarServicoFaltante(String servico) {
        servicoFaltante = servico;
    }

    public void selecionarOpcao(String opcao) {
        if (ENCERRAR.equals(opcao)) {
            confirmarEncerramento();
        } else if (CANCELAR.equals(opcao) && Estilo.perguntar(this, TITULO, MensagensEncerrar.M10)) {
            // Variante 6
            controladora.cancelarEncerramento();
            dispose();
        }
    }

    // Variante 5
    private void confirmarEncerramento() {
        boolean variante = SEM_ABERTURA.equals(origem);
        if (variante ? ebSelecionado == null : manutencaoSelecionada == null) {
            exibirMensagem(MensagensEncerrar.m02(variante ? "EB da viatura" : "Manutenção em andamento"));
            return;
        }

        long dataAbertura = 0;
        int odometroEntrada = 0;
        if (variante) {
            try {
                formatoData.setLenient(false);
                dataAbertura = formatoData.parse(txtDataAbertura.getText().trim()).getTime();
            } catch (ParseException e) {
                exibirMensagem(MensagensEncerrar.m02("Data de abertura"));
                return;
            }
            try {
                odometroEntrada = Integer.parseInt(txtOdometroEntrada.getText().trim());
            } catch (NumberFormatException e) {
                exibirMensagem(MensagensEncerrar.m02("Odômetro de entrada"));
                return;
            }
            try {
                // FAV1.5.1 e FAV1.5.2
                if (!controladora.validarAbertura(ebSelecionado, dataAbertura, odometroEntrada, dataAtual)) {
                    exibirMensagem(controladora.getErroAbertura());
                    return;
                }
            } catch (SQLException e) {
                exibirMensagem(MensagensEncerrar.M11);
                return;
            }
        }

        lerOdometroSaida();
        if (odometroSaida == null) {
            if (txtOdometroSaida.getText().trim().isEmpty()) {
                exibirMensagem(MensagensEncerrar.m02("Odômetro de saída"));
            }
            return;
        }
        if (tipoSelecionado == null) {
            exibirMensagem(MensagensEncerrar.m02("Tipo de manutenção"));
            return;
        }
        if (situacaoFinal == null) {
            exibirMensagem(MensagensEncerrar.m02("Situação final da viatura"));
            return;
        }

        List<Filtros> filtros = new ArrayList<>();
        if (Manutencao.PREVENTIVA.equals(tipoSelecionado)) {
            if (!lerFiltros()) {
                return;
            }
            filtros = filtrosInformados;
        }

        informarServicoFaltante(null);
        if (!servicoConcluido) {
            String texto = txtServico.getText().trim();
            if (texto.isEmpty()) {
                exibirMensagem(MensagensEncerrar.M08);   // FAV4.1
                return;
            }
            informarServicoFaltante(texto);
        }

        try {
            int id;
            if (variante) {
                id = controladora.registrarManutencaoExecutada(ebSelecionado, paneSelecionada, dataAbertura, odometroEntrada,
                        odometroSaida, tipoSelecionado, situacaoFinal, dataAtual, filtros, servicoFaltante);
            } else {
                id = controladora.gravarEncerramento(manutencaoSelecionada.getIdManutencao(), odometroSaida,
                        tipoSelecionado, situacaoFinal, dataAtual, filtros, servicoFaltante);
            }
            exibir(Estilo.VERDE, MensagensEncerrar.m01(id, ebSelecionado, situacaoFinal));
            dispose();
        } catch (IllegalArgumentException e) {
            exibirMensagem(e.getMessage());
        } catch (SQLException e) {
            exibirMensagem(MensagensEncerrar.M11);
        }
    }

    public void exibirMensagem(String msg) {
        exibir(Estilo.VERMELHO, msg);
    }

    private void exibir(Color cor, String msg) {
        Estilo.dialogo(this, cor, TITULO, msg, "OK");
    }
}
