package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraConsultarManutencoes;
import br.ufms.mvm.modelo.ControlePreventiva;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Tela do caso de uso Consultar manutencoes preventivas (RF11)
public class TelaConsultarPreventivas extends TelaConsulta {

    private final ControladoraConsultarManutencoes controladora = new ControladoraConsultarManutencoes();
    private int necessitam;

    public TelaConsultarPreventivas() {
        super("Consultar manutenções preventivas",
                "Viaturas que precisam de preventiva: limite de km rodado (5.000 km se não informado) ou 6 meses "
                        + "desde a última.",
                new String[]{"EB da viatura", "Tipo de viatura", "Data da última preventiva",
                        "Km da última preventiva", "Km percorridos", "Necessita?", "Motivo"});
        iniciar();
    }

    @Override
    protected List<Object[]> buscarLinhas() throws SQLException {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        List<Object[]> linhas = new ArrayList<>();
        necessitam = 0;
        for (ControlePreventiva c : controladora.consultarPreventivas(LocalDate.now())) {
            if (c.isNecessita()) {
                necessitam++;
            }
            linhas.add(new Object[]{c.getEb(), c.getTipoViatura(),
                    c.getDataUltima() == null ? "-" : f.format(new Date(c.getDataUltima())),
                    c.getKmUltima() == null ? "-" : c.getKmUltima(),
                    c.getKmPercorridos() == null ? "-" : c.getKmPercorridos(),
                    c.isNecessita() ? "SIM" : "Não", c.getMotivo()});
        }
        return linhas;
    }

    @Override
    protected String resumo(int total) {
        return total + " viatura(s) ativa(s); " + necessitam + " necessita(m) de manutenção preventiva.";
    }
}
