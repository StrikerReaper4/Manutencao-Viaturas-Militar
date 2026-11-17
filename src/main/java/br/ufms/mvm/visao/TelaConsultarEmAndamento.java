package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraConsultarManutencoes;
import br.ufms.mvm.modelo.ManutencaoEmAndamento;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Tela do caso de uso Consultar manutencoes em andamento (RF10)
public class TelaConsultarEmAndamento extends TelaConsulta {

    private final ControladoraConsultarManutencoes controladora = new ControladoraConsultarManutencoes();

    public TelaConsultarEmAndamento() {
        super("Consultar manutenções em andamento",
                "Viaturas que estão na oficina neste momento, com os mecânicos responsáveis.",
                new String[]{"Nº", "EB da viatura", "Tipo de viatura", "Mecânicos responsáveis", "Serviço em execução", "Data"});
        iniciar();
    }

    @Override
    protected List<Object[]> buscarLinhas() throws SQLException {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        List<Object[]> linhas = new ArrayList<>();
        for (ManutencaoEmAndamento m : controladora.consultarEmAndamento()) {
            linhas.add(new Object[]{m.getIdManutencao(), m.getEb(), m.getTipoViatura(),
                    m.getMecanicos() == null ? "" : m.getMecanicos(), m.getServico(),
                    f.format(new Date(m.getDataInicio()))});
        }
        return linhas;
    }
}
