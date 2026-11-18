package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraEmitirRelatorioMensal;
import br.ufms.mvm.modelo.LinhaRelatorioMensal;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Tela do caso de uso Emitir relatorio mensal (RF14).
// A coluna "Valor (R$)" vem vazia de proposito: eh o campo pra preencher na folha impressa.
public class TelaRelatorioMensal extends TelaConsulta {

    private static final String[] MESES = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho",
            "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

    private final ControladoraEmitirRelatorioMensal controladora = new ControladoraEmitirRelatorioMensal();
    private final JComboBox<String> cbMes = new JComboBox<>(MESES);
    private final JTextField txtAno = new JTextField();

    public TelaRelatorioMensal() {
        super("Relatório mensal de manutenções",
                "Manutenções iniciadas ou encerradas no mês. Campos ainda não preenchidos aparecem em branco.",
                new String[]{"EB da viatura", "Tipo de viatura", "Serviço executado", "Peças utilizadas",
                        "Valor (R$)", "Início", "Encerramento", "Situação final"});
        LocalDate hoje = LocalDate.now();
        cbMes.setSelectedIndex(hoje.getMonthValue() - 1);
        txtAno.setText(String.valueOf(hoje.getYear()));
        Estilo.empilhar(filtros, Estilo.linha(
                Estilo.campo("Mês", true, cbMes),
                Estilo.campo("Ano", true, txtAno)));
        filtros.setVisible(true);
        iniciar();
    }

    @Override
    protected List<Object[]> buscarLinhas() throws SQLException {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        List<Object[]> linhas = new ArrayList<>();
        for (LinhaRelatorioMensal l : controladora.emitir(cbMes.getSelectedIndex() + 1, txtAno.getText())) {
            linhas.add(new Object[]{l.getEb(), l.getTipoViatura(), l.getServico(),
                    l.getPecas() == null ? "" : l.getPecas(),
                    "", // valor: preenchido a mao no relatorio impresso
                    f.format(new Date(l.getDataInicio())),
                    l.getDataEncerramento() == null ? "" : f.format(new Date(l.getDataEncerramento())),
                    l.getSituacaoFinal() == null ? "" : l.getSituacaoFinal()});
        }
        return linhas;
    }

    @Override
    protected String cabecalhoImpressao() {
        return " - " + cbMes.getSelectedItem() + "/" + txtAno.getText().trim();
    }
}
