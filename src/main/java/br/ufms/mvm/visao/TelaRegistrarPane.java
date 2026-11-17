package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraRegistrarPane;
import br.ufms.mvm.controladora.MensagensCadastro;
import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.Viatura;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class TelaRegistrarPane extends JFrame {

    private static final String TITULO = "Registrar pane";

    private final ControladoraRegistrarPane controladora = new ControladoraRegistrarPane();

    private final JComboBox<Viatura> cbViatura = new JComboBox<>();
    private final JTextField txtDescricao = new JTextField();
    private final JComboBox<String> cbPrioridade = new JComboBox<>(new String[]{
            "1 - Urgente", "2 - Alta", "3 - Média", "4 - Baixa", "5 - Rotina"});
    private final JTextField txtDataLimite = new JTextField();
    private final JTextField txtMissao = new JTextField();
    private final DefaultTableModel modeloFila = new DefaultTableModel(
            new String[]{"Nº", "EB", "Descrição", "Prioridade", "Data limite", "Missão", "Situação"}, 0) {
        private static final long serialVersionUID = 1L;

        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    public TelaRegistrarPane() {
        super("MVM - Registrar Pane");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel dados = Estilo.secao("Dados da pane");
        Estilo.empilhar(dados, Estilo.linha(
                Estilo.campo("EB da viatura", true, cbViatura),
                Estilo.campo("Prioridade", true, cbPrioridade),
                Estilo.campo("Data limite para atendimento (dd/mm/aaaa)", true, txtDataLimite)));
        Estilo.empilhar(dados, Estilo.campo("Descrição da pane", true, txtDescricao));
        Estilo.empilhar(dados, Estilo.campo("Missão associada", false, txtMissao));

        JTable tabela = new JTable(modeloFila);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane rolagemTabela = new JScrollPane(tabela);
        rolagemTabela.setPreferredSize(new Dimension(900, 240));
        JPanel fila = Estilo.secao("Fila de atendimento da oficina (por prioridade)");
        Estilo.empilhar(fila, rolagemTabela);

        JPanel corpo = Estilo.corpo();
        Estilo.empilhar(corpo, dados);
        Estilo.empilhar(corpo, fila);

        JButton fechar = Estilo.botaoSecundario("Fechar");
        JButton registrar = Estilo.botaoPrimario("Registrar");
        fechar.addActionListener(e -> dispose());
        registrar.addActionListener(e -> registrar());

        JPanel topoCorpo = new JPanel(new BorderLayout());
        topoCorpo.add(corpo, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(topoCorpo);
        rolagem.setBorder(BorderFactory.createEmptyBorder());

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho("Registrar Pane",
                "Registro de defeito identificado em viatura. Os campos assinalados com (*) são de preenchimento obrigatório."),
                BorderLayout.NORTH);
        raiz.add(rolagem, BorderLayout.CENTER);
        raiz.add(Estilo.rodape(fechar, registrar), BorderLayout.SOUTH);
        setContentPane(raiz);

        carregar();
        Estilo.ajustarJanela(this);
        setLocationRelativeTo(null);
    }

    private void carregar() {
        try {
            cbViatura.removeAllItems();
            controladora.consultarViaturasAtivas().forEach(cbViatura::addItem);
            cbViatura.setSelectedIndex(-1);
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            modeloFila.setRowCount(0);
            for (Pane p : controladora.listarFilaAtendimento()) {
                modeloFila.addRow(new Object[]{p.getIdPane(), p.getEb(), p.getDescricao(), p.getPrioridade(),
                        p.getDataLimite() == null ? "" : f.format(new Date(p.getDataLimite())),
                        p.getMissao() == null ? "" : p.getMissao(), p.getSituacao()});
            }
        } catch (SQLException e) {
            erro("Erro ao consultar o banco de dados: " + e.getMessage());
        }
        txtDescricao.setText("");
        txtDataLimite.setText("");
        txtMissao.setText("");
        cbPrioridade.setSelectedIndex(2);
    }

    private void registrar() {
        Viatura v = (Viatura) cbViatura.getSelectedItem();
        try {
            int id = controladora.registrarPane(v == null ? null : v.getEb(), txtDescricao.getText(),
                    cbPrioridade.getSelectedIndex() + 1, txtDataLimite.getText(), txtMissao.getText(), LocalDate.now());
            Estilo.dialogo(this, Estilo.VERDE, TITULO, MensagensCadastro.paneRegistrada(id, v.getEb()), "OK");
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
