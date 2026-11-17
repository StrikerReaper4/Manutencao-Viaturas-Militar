package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraAtualizarSituacao;
import br.ufms.mvm.controladora.MensagensCadastro;
import br.ufms.mvm.controladora.MensagensEdicao;
import br.ufms.mvm.modelo.RegistroSituacao;
import br.ufms.mvm.modelo.RegistroSituacao.Tipo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Tela do caso de uso Atualizar situacao de registro (RF04)
public class TelaAtualizarSituacao extends JFrame {

    private static final String TITULO = "Atualizar situação de registro";

    private final ControladoraAtualizarSituacao controladora = new ControladoraAtualizarSituacao();

    private final JComboBox<Tipo> cbTipo = new JComboBox<>(Tipo.values());
    private final List<RegistroSituacao> registros = new ArrayList<>();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Registro", "Situação", "Última alteração", "Responsável"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private final JButton btnAlterar = Estilo.botaoPrimario("Inativar");

    public TelaAtualizarSituacao() {
        super("MVM - Atualizar Situação de Registro");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        // o texto do botao acompanha o registro selecionado
        tabela.getSelectionModel().addListSelectionListener(e -> {
            RegistroSituacao r = selecionado();
            btnAlterar.setText(r != null && !r.isAtivo() ? "Reativar" : "Inativar");
        });
        JScrollPane rolagemTabela = new JScrollPane(tabela);
        rolagemTabela.setPreferredSize(new Dimension(900, 360));

        JPanel corpo = Estilo.corpo();
        JPanel filtro = Estilo.secao("Tipo de registro");
        Estilo.empilhar(filtro, Estilo.campo("Mostrar", true, cbTipo));
        Estilo.empilhar(corpo, filtro);
        JPanel lista = Estilo.secao("Registros");
        Estilo.empilhar(lista, Estilo.aviso("Registros inativos não aparecem para seleção em novos lançamentos, "
                + "mas continuam visíveis nas manutenções e relatórios anteriores."));
        Estilo.empilhar(lista, rolagemTabela);
        Estilo.empilhar(corpo, lista);

        cbTipo.addActionListener(e -> carregar());
        JButton fechar = Estilo.botaoSecundario("Fechar");
        fechar.addActionListener(e -> dispose());
        btnAlterar.addActionListener(e -> alterar());

        JPanel topoCorpo = new JPanel(new BorderLayout());
        topoCorpo.add(corpo, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(topoCorpo);
        rolagem.setBorder(BorderFactory.createEmptyBorder());

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho("Atualizar Situação de Registro",
                "Inativação e reativação de viaturas, itens e usuários (inclusive mecânicos)."), BorderLayout.NORTH);
        raiz.add(rolagem, BorderLayout.CENTER);
        raiz.add(Estilo.rodape(fechar, btnAlterar), BorderLayout.SOUTH);
        setContentPane(raiz);

        carregar();
        Estilo.ajustarJanela(this);
        setLocationRelativeTo(null);
    }

    private RegistroSituacao selecionado() {
        int linha = tabela.getSelectedRow();
        return linha >= 0 ? registros.get(linha) : null;
    }

    private void carregar() {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            registros.clear();
            registros.addAll(controladora.listarRegistros((Tipo) cbTipo.getSelectedItem()));
            modelo.setRowCount(0);
            for (RegistroSituacao r : registros) {
                modelo.addRow(new Object[]{r.getDescricao(), r.isAtivo() ? "Ativo" : "Inativo",
                        r.getDataUltimaAlteracao() == null ? "" : f.format(new Date(r.getDataUltimaAlteracao())),
                        r.getUsuarioUltimaAlteracao() == null ? "" : r.getUsuarioUltimaAlteracao()});
            }
        } catch (SQLException e) {
            erro("Erro ao consultar o banco de dados: " + e.getMessage());
        }
        btnAlterar.setText("Inativar");
    }

    private void alterar() {
        RegistroSituacao r = selecionado();
        if (r == null) {
            erro(MensagensCadastro.SELECIONE);
            return;
        }
        // RNF10: confirma antes de efetivar
        String pergunta = r.isAtivo() ? MensagensEdicao.confirmarInativacao(r.toString())
                                      : MensagensEdicao.confirmarReativacao(r.toString());
        if (!Estilo.perguntar(this, TITULO, pergunta)) {
            return;
        }
        try {
            boolean ativo = controladora.alterarSituacao(r, System.currentTimeMillis());
            Estilo.dialogo(this, Estilo.VERDE, TITULO,
                    MensagensEdicao.situacaoAlterada(r.getTipo() + " " + r.getDescricao(), ativo), "OK");
            carregar();
        } catch (IllegalArgumentException e) {
            erro(e.getMessage());
        } catch (SQLException e) {
            erro(MensagensCadastro.ERRO_GRAVAR);
        }
    }

    private void erro(String msg) {
        Estilo.dialogo(this, Estilo.VERMELHO, TITULO, msg, "OK");
    }
}
