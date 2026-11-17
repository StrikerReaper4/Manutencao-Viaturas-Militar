package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraEditarManutencao;
import br.ufms.mvm.controladora.MensagensCadastro;
import br.ufms.mvm.controladora.MensagensEdicao;
import br.ufms.mvm.modelo.Filtros;
import br.ufms.mvm.modelo.Manutencao;
import br.ufms.mvm.modelo.Mecanico;
import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.Viatura;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Tela do caso de uso Editar manutencao (RF07)
public class TelaEditarManutencao extends JFrame {

    private static final String TITULO = "Editar manutenção";

    private final ControladoraEditarManutencao controladora = new ControladoraEditarManutencao();

    private final JComboBox<Manutencao> cbManutencao = new JComboBox<>();
    private final JLabel lblViatura = new JLabel("-");
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{Manutencao.PREVENTIVA, Manutencao.CORRETIVA});
    private final JTextField txtOdometro = new JTextField();
    private final JComboBox<Pane> cbPane = new JComboBox<>();
    private final JComboBox<Mecanico> cbMecanico = new JComboBox<>();
    private final DefaultListModel<Mecanico> modeloResponsaveis = new DefaultListModel<>();
    private final JList<Mecanico> listaResponsaveis = new JList<>(modeloResponsaveis);
    private final JPanel painelDados = Estilo.secao("Dados da manutenção");
    private final JPanel painelMecanicos = Estilo.secao("Mecânicos responsáveis");
    private final JPanel painelFiltros = Estilo.secao("Filtros a substituir (preventiva)");
    private final JPanel listaFiltros = new JPanel(new GridLayout(0, 3, 12, 4));
    private final List<JCheckBox> checkFiltros = new ArrayList<>();
    private final List<Filtros> filtros = new ArrayList<>();

    private boolean carregando;

    public TelaEditarManutencao() {
        super("MVM - Editar Manutenção");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        montarTela();
        carregarLista();
        Estilo.ajustarJanela(this);
        setLocationRelativeTo(null);
    }

    private void montarTela() {
        JPanel corpo = Estilo.corpo();

        JPanel selecao = Estilo.secao("Manutenção em andamento");
        Estilo.empilhar(selecao, Estilo.campo("Selecione a manutenção", true, cbManutencao));
        Estilo.empilhar(corpo, selecao);

        txtOdometro.setColumns(10);
        Estilo.empilhar(painelDados, Estilo.linha(
                Estilo.campo("Viatura", false, lblViatura),
                Estilo.campo("Tipo de manutenção", true, cbTipo),
                Estilo.campo("Odômetro de entrada", true, txtOdometro)));
        Estilo.empilhar(painelDados, Estilo.campo("Pane a ser atendida (obrigatória na corretiva)", false, cbPane));
        Estilo.empilhar(corpo, painelDados);

        JButton adicionar = Estilo.botaoSecundario("Adicionar");
        JButton remover = Estilo.botaoSecundario("Remover selecionado");
        adicionar.addActionListener(e -> adicionarMecanico());
        remover.addActionListener(e -> removerMecanico());
        remover.setVisible(controladora.podeRemoverMecanico()); // so o Operador remove
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setOpaque(false);
        lateral.add(adicionar, BorderLayout.SOUTH);
        Estilo.empilhar(painelMecanicos, Estilo.linhaComLateral(Estilo.campo("Selecionar mecânico", false, cbMecanico), lateral));
        JScrollPane rolagemLista = new JScrollPane(listaResponsaveis);
        rolagemLista.setPreferredSize(new Dimension(400, 90));
        Estilo.empilhar(painelMecanicos, rolagemLista);
        JPanel acoes = new JPanel(new BorderLayout());
        acoes.setOpaque(false);
        acoes.add(remover, BorderLayout.WEST);
        Estilo.empilhar(painelMecanicos, acoes);
        Estilo.empilhar(corpo, painelMecanicos);

        listaFiltros.setOpaque(false);
        Estilo.empilhar(painelFiltros, listaFiltros);
        Estilo.empilhar(corpo, painelFiltros);

        mostrarEdicao(false);

        cbManutencao.addActionListener(e -> {
            if (!carregando) {
                selecionarManutencao();
            }
        });
        cbTipo.addActionListener(e -> painelFiltros.setVisible(painelDados.isVisible()
                && Manutencao.PREVENTIVA.equals(cbTipo.getSelectedItem())));

        JButton fechar = Estilo.botaoSecundario("Fechar");
        JButton salvar = Estilo.botaoPrimario("Salvar alterações");
        fechar.addActionListener(e -> fechar());
        salvar.addActionListener(e -> salvar());

        JPanel topoCorpo = new JPanel(new BorderLayout());
        topoCorpo.add(corpo, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(topoCorpo);
        rolagem.setBorder(BorderFactory.createEmptyBorder());

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho("Editar Manutenção",
                "Alteração de manutenção em andamento e administração dos mecânicos responsáveis."),
                BorderLayout.NORTH);
        raiz.add(rolagem, BorderLayout.CENTER);
        raiz.add(Estilo.rodape(fechar, salvar), BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    private void mostrarEdicao(boolean visivel) {
        painelDados.setVisible(visivel);
        painelMecanicos.setVisible(visivel);
        painelFiltros.setVisible(visivel && Manutencao.PREVENTIVA.equals(cbTipo.getSelectedItem()));
    }

    private void carregarLista() {
        carregando = true;
        try {
            cbManutencao.removeAllItems();
            controladora.listarManutencoesEditaveis().forEach(cbManutencao::addItem);
            cbManutencao.setSelectedIndex(-1);
            cbMecanico.removeAllItems();
            controladora.consultarMecanicosAtivos().forEach(cbMecanico::addItem);
            cbMecanico.setSelectedIndex(-1);
        } catch (SQLException e) {
            erro("Erro ao consultar o banco de dados: " + e.getMessage());
        } finally {
            carregando = false;
        }
        mostrarEdicao(false);
        if (cbManutencao.getItemCount() == 0) {
            Estilo.dialogo(this, Estilo.AZUL, TITULO, "Não há manutenção em andamento que você possa alterar.", "OK");
        }
    }

    private void selecionarManutencao() {
        Manutencao sel = (Manutencao) cbManutencao.getSelectedItem();
        if (sel == null) {
            mostrarEdicao(false);
            return;
        }
        try {
            Manutencao m = controladora.carregarManutencao(sel.getIdManutencao());
            Viatura v = controladora.buscarViatura(m.getEb());
            lblViatura.setText(v != null ? v.toString() : m.getEb());
            cbTipo.setSelectedItem(m.getTipo());
            txtOdometro.setText(String.valueOf(m.getOdometroEntrada()));

            cbPane.removeAllItems();
            cbPane.addItem(null); // sem pane (preventiva)
            for (Pane p : controladora.listarPanes()) {
                cbPane.addItem(p);
            }
            cbPane.setSelectedIndex(m.getIdPane() != null && cbPane.getItemCount() > 1 ? 1 : 0);

            listaFiltros.removeAll();
            checkFiltros.clear();
            filtros.clear();
            List<Integer> previstos = controladora.filtrosPrevistos();
            for (Filtros f : controladora.listarFiltros()) {
                JCheckBox c = new JCheckBox(f.toString(), previstos.contains(f.getIdFiltro()));
                c.setOpaque(false);
                checkFiltros.add(c);
                filtros.add(f);
                listaFiltros.add(c);
            }
            atualizarResponsaveis();
            mostrarEdicao(true);
            Estilo.ajustarJanela(this);
        } catch (IllegalArgumentException e) {
            erro(e.getMessage());
        } catch (SQLException e) {
            erro("Erro ao consultar o banco de dados: " + e.getMessage());
        }
    }

    private void atualizarResponsaveis() {
        modeloResponsaveis.clear();
        controladora.getMecanicos().forEach(modeloResponsaveis::addElement);
    }

    private void adicionarMecanico() {
        Mecanico me = (Mecanico) cbMecanico.getSelectedItem();
        if (me == null) {
            erro(MensagensCadastro.obrigatorio("Selecionar mecânico"));
            return;
        }
        try {
            controladora.incluirMecanico(me.getIdUsuario());
            atualizarResponsaveis();
        } catch (IllegalArgumentException e) {
            erro(e.getMessage());
        } catch (SQLException e) {
            erro("Erro ao consultar o banco de dados: " + e.getMessage());
        }
    }

    private void removerMecanico() {
        Mecanico me = listaResponsaveis.getSelectedValue();
        if (me == null) {
            erro(MensagensCadastro.SELECIONE);
            return;
        }
        try {
            controladora.removerMecanico(me.getIdUsuario());
            atualizarResponsaveis();
        } catch (IllegalArgumentException e) {
            erro(e.getMessage());
        }
    }

    private void salvar() {
        if (cbManutencao.getSelectedItem() == null) {
            erro(MensagensCadastro.obrigatorio("Manutenção"));
            return;
        }
        int odometro;
        try {
            odometro = Integer.parseInt(txtOdometro.getText().trim());
        } catch (NumberFormatException e) {
            erro(MensagensCadastro.numeroInvalido("Odômetro de entrada"));
            return;
        }
        String tipo = (String) cbTipo.getSelectedItem();
        Pane p = (Pane) cbPane.getSelectedItem();
        List<Integer> marcados = new ArrayList<>();
        for (int i = 0; i < checkFiltros.size(); i++) {
            if (checkFiltros.get(i).isSelected()) {
                marcados.add(filtros.get(i).getIdFiltro());
            }
        }
        try {
            int id = controladora.gravarEdicao(tipo, p != null ? p.getIdPane() : null, odometro,
                    marcados.stream().mapToInt(Integer::intValue).toArray());
            Estilo.dialogo(this, Estilo.VERDE, TITULO, MensagensEdicao.manutencaoAlterada(id), "OK");
            carregarLista();
        } catch (IllegalArgumentException e) {
            erro(e.getMessage());
        } catch (SQLException e) {
            erro(MensagensCadastro.ERRO_GRAVAR);
        }
    }

    private void fechar() {
        if (painelDados.isVisible() && !Estilo.perguntar(this, TITULO, MensagensEdicao.CANCELAR)) {
            return;
        }
        controladora.cancelarEdicao();
        dispose();
    }

    private void erro(String msg) {
        Estilo.dialogo(this, Estilo.VERMELHO, TITULO, msg, "OK");
    }
}
