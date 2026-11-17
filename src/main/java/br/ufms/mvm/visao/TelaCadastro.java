package br.ufms.mvm.visao;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Base das telas de cadastro (viatura, item, usuario): lista em cima e formulario embaixo.
// Selecionar uma linha carrega o registro pra alterar; "Novo" limpa pra incluir.
public abstract class TelaCadastro<T> extends JFrame {

    protected final String titulo;
    protected final List<T> registros = new ArrayList<>();
    protected final DefaultTableModel modeloTabela;
    protected final JTable tabela;
    protected final JPanel formulario;
    protected T selecionado;

    protected TelaCadastro(String titulo, String subtitulo, String tituloFormulario, String[] colunas, boolean permiteRemover) {
        super("MVM - " + titulo);
        this.titulo = titulo;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // RNF07 vale pra lista tambem: nao edita direto na tabela
        modeloTabela = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linha = tabela.getSelectedRow();
                selecionado = linha >= 0 ? registros.get(linha) : null;
                if (selecionado != null) {
                    preencherFormulario(selecionado);
                }
            }
        });
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setPreferredSize(new Dimension(900, 260));

        formulario = Estilo.secao(tituloFormulario);

        JPanel corpo = Estilo.corpo();
        JPanel secaoLista = Estilo.secao("Registros cadastrados");
        Estilo.empilhar(secaoLista, rolagem);
        Estilo.empilhar(corpo, secaoLista);
        Estilo.empilhar(corpo, formulario);

        JButton novo = Estilo.botaoSecundario("Novo");
        JButton remover = Estilo.botaoSecundario("Remover");
        JButton fechar = Estilo.botaoSecundario("Fechar");
        JButton salvar = Estilo.botaoPrimario("Salvar");
        novo.addActionListener(e -> novo());
        remover.addActionListener(e -> remover());
        fechar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> salvar());

        JPanel topoCorpo = new JPanel(new BorderLayout());
        topoCorpo.add(corpo, BorderLayout.NORTH);
        JScrollPane rolagemCorpo = new JScrollPane(topoCorpo);
        rolagemCorpo.setBorder(BorderFactory.createEmptyBorder());

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho(titulo, subtitulo), BorderLayout.NORTH);
        raiz.add(rolagemCorpo, BorderLayout.CENTER);
        raiz.add(permiteRemover ? Estilo.rodape(fechar, novo, remover, salvar) : Estilo.rodape(fechar, novo, salvar),
                BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    // chamado pela subclasse depois de montar o formulario
    protected void iniciar() {
        recarregar();
        Estilo.ajustarJanela(this);
        setLocationRelativeTo(null);
    }

    protected void recarregar() {
        try {
            registros.clear();
            registros.addAll(listar());
            modeloTabela.setRowCount(0);
            for (T r : registros) {
                modeloTabela.addRow(linha(r));
            }
        } catch (SQLException e) {
            erro("Erro ao consultar o banco de dados: " + e.getMessage());
        }
        novo();
    }

    private void novo() {
        tabela.clearSelection();
        selecionado = null;
        limparFormulario();
    }

    private void salvar() {
        try {
            String registro = gravar();
            Estilo.dialogo(this, Estilo.VERDE, titulo, registro, "OK");
            recarregar();
        } catch (IllegalArgumentException e) {
            erro(e.getMessage());
        } catch (SQLException e) {
            erro(br.ufms.mvm.controladora.MensagensCadastro.ERRO_GRAVAR);
        }
    }

    private void remover() {
        if (selecionado == null) {
            erro(br.ufms.mvm.controladora.MensagensCadastro.SELECIONE);
            return;
        }
        if (!Estilo.perguntar(this, titulo, br.ufms.mvm.controladora.MensagensCadastro.confirmarRemocao(descrever(selecionado)))) {
            return;
        }
        try {
            String registro = excluir(selecionado);
            Estilo.dialogo(this, Estilo.VERDE, titulo, registro, "OK");
            recarregar();
        } catch (IllegalArgumentException e) {
            erro(e.getMessage());
        } catch (SQLException e) {
            erro(br.ufms.mvm.controladora.MensagensCadastro.ERRO_GRAVAR);
        }
    }

    protected void erro(String msg) {
        Estilo.dialogo(this, Estilo.VERMELHO, titulo, msg, "OK");
    }

    protected abstract List<T> listar() throws SQLException;

    protected abstract Object[] linha(T registro);

    protected abstract void preencherFormulario(T registro);

    protected abstract void limparFormulario();

    // grava o formulario (inclui se selecionado == null) e devolve a mensagem de sucesso
    protected abstract String gravar() throws SQLException;

    protected String excluir(T registro) throws SQLException {
        throw new UnsupportedOperationException();
    }

    protected String descrever(T registro) {
        return String.valueOf(registro);
    }
}
