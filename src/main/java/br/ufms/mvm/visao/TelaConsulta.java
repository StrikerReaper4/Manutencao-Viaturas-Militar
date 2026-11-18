package br.ufms.mvm.visao;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

// Base das telas de consulta: uma tabela so de leitura (RNF07) e o botao Atualizar.
// A subclasse diz como buscar as linhas; filtros extras podem ir no painel "filtros".
public abstract class TelaConsulta extends JFrame {

    protected final String titulo;
    protected final DefaultTableModel modelo;
    protected final JTable tabela;
    protected final JPanel filtros = Estilo.secao("Filtros");
    private final JLabel lblTotal = new JLabel(" ");

    protected TelaConsulta(String titulo, String subtitulo, String[] colunas) {
        super("MVM - " + titulo);
        this.titulo = titulo;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        modelo = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setAutoCreateRowSorter(true); // da pra ordenar clicando no cabecalho
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        JScrollPane rolagemTabela = new JScrollPane(tabela);
        rolagemTabela.setPreferredSize(new Dimension(1000, 420));

        filtros.setVisible(false);
        JPanel corpo = Estilo.corpo();
        Estilo.empilhar(corpo, filtros);
        JPanel resultado = Estilo.secao("Resultado");
        Estilo.empilhar(resultado, rolagemTabela);
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        Estilo.empilhar(resultado, lblTotal);
        Estilo.empilhar(corpo, resultado);

        JButton fechar = Estilo.botaoSecundario("Fechar");
        JButton imprimir = Estilo.botaoSecundario("Imprimir");
        JButton atualizar = Estilo.botaoPrimario("Consultar");
        fechar.addActionListener(e -> dispose());
        imprimir.addActionListener(e -> imprimir());
        atualizar.addActionListener(e -> consultar());

        JPanel topoCorpo = new JPanel(new BorderLayout());
        topoCorpo.add(corpo, BorderLayout.NORTH);
        JScrollPane rolagem = new JScrollPane(topoCorpo);
        rolagem.setBorder(BorderFactory.createEmptyBorder());

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(Estilo.cabecalho(titulo, subtitulo), BorderLayout.NORTH);
        raiz.add(rolagem, BorderLayout.CENTER);
        raiz.add(Estilo.rodape(fechar, imprimir, atualizar), BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    // chamado pela subclasse no fim do construtor
    protected void iniciar() {
        consultar();
        Estilo.ajustarJanela(this);
        setLocationRelativeTo(null);
    }

    protected void consultar() {
        try {
            List<Object[]> linhas = buscarLinhas();
            modelo.setRowCount(0);
            for (Object[] l : linhas) {
                modelo.addRow(l);
            }
            lblTotal.setText(resumo(linhas.size()));
        } catch (IllegalArgumentException e) {
            Estilo.dialogo(this, Estilo.VERMELHO, titulo, e.getMessage(), "OK");
        } catch (SQLException e) {
            Estilo.dialogo(this, Estilo.VERMELHO, titulo, "Erro ao consultar o banco de dados: " + e.getMessage(), "OK");
        }
    }

    // usa a janela de impressao do sistema (da pra salvar em PDF por ela)
    private void imprimir() {
        try {
            tabela.print(JTable.PrintMode.FIT_WIDTH, new java.text.MessageFormat("MVM - " + titulo + cabecalhoImpressao()),
                    new java.text.MessageFormat("Página {0}"));
        } catch (java.awt.print.PrinterException e) {
            Estilo.dialogo(this, Estilo.VERMELHO, titulo, "Não foi possível imprimir: " + e.getMessage(), "OK");
        }
    }

    // texto extra no topo da impressao (ex.: periodo do relatorio)
    protected String cabecalhoImpressao() {
        return "";
    }

    protected String resumo(int total) {
        return total == 0 ? "Nenhum registro encontrado." : total + " registro(s) encontrado(s).";
    }

    protected abstract List<Object[]> buscarLinhas() throws SQLException;
}
