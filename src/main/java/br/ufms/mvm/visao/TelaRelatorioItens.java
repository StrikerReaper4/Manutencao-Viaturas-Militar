package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraEmitirRelatorioItens;
import br.ufms.mvm.modelo.ItemUtilizado;

import javax.swing.JTextField;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Tela do caso de uso Emitir relatorio de itens utilizados (RF13)
public class TelaRelatorioItens extends TelaConsulta {

    private final ControladoraEmitirRelatorioItens controladora = new ControladoraEmitirRelatorioItens();
    private final JTextField txtInicio = new JTextField();
    private final JTextField txtFim = new JTextField();
    private final JTextField txtItem = new JTextField();

    public TelaRelatorioItens() {
        super("Relatório de itens utilizados",
                "Quantidade de cada peça/material usada por viatura nas manutenções encerradas no período.",
                new String[]{"EB da viatura", "Tipo de viatura", "Nome do item", "Quantidade utilizada"});
        Estilo.empilhar(filtros, Estilo.linha(
                Estilo.campo("Data inicial (dd/mm/aaaa)", false, txtInicio),
                Estilo.campo("Data final (dd/mm/aaaa)", false, txtFim),
                Estilo.campo("Nome do item (contém)", false, txtItem)));
        Estilo.empilhar(filtros, Estilo.aviso("Sem período informado, o relatório considera o dia de hoje."));
        filtros.setVisible(true);
        iniciar();
    }

    @Override
    protected List<Object[]> buscarLinhas() throws SQLException {
        List<Object[]> linhas = new ArrayList<>();
        for (ItemUtilizado i : controladora.emitir(txtInicio.getText(), txtFim.getText(), txtItem.getText(), LocalDate.now())) {
            linhas.add(new Object[]{i.getEb(), i.getTipoViatura(), i.getNomeItem(),
                    i.getQuantidade() + " " + i.getUnidadeMedida()});
        }
        return linhas;
    }

    @Override
    protected String cabecalhoImpressao() {
        String de = txtInicio.getText().isBlank() ? "hoje" : txtInicio.getText().trim();
        String ate = txtFim.getText().isBlank() ? "hoje" : txtFim.getText().trim();
        return " (" + de + " a " + ate + ")";
    }
}
