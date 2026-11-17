package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.ControlePreventiva;
import br.ufms.mvm.modelo.ManutencaoEmAndamento;
import br.ufms.mvm.persistencia.ConsultaDAO;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

// Casos de uso Consultar manutencoes em andamento (RF10) e Consultar manutencoes preventivas (RF11).
// Operador e Mecanico podem consultar; nenhuma das duas altera dados (RNF07).
public class ControladoraConsultarManutencoes {

    public static final int KM_PADRAO = 5000;
    public static final int MESES_LIMITE = 6;

    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    public ControladoraConsultarManutencoes() {
        if (Sessao.getUsuarioLogado() == null) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    public List<ManutencaoEmAndamento> consultarEmAndamento() throws SQLException {
        return consultaDAO.listarEmAndamento();
    }

    // RF11: sinaliza quando rodou o limite de km desde a ultima preventiva (5000 se nao informado)
    // ou quando ja passaram 6 meses. As que precisam aparecem primeiro.
    public List<ControlePreventiva> consultarPreventivas(LocalDate hoje) throws SQLException {
        List<ControlePreventiva> lista = consultaDAO.listarControlePreventiva();
        for (ControlePreventiva c : lista) {
            avaliar(c, hoje);
        }
        lista.sort(Comparator.comparing((ControlePreventiva c) -> !c.isNecessita()).thenComparing(ControlePreventiva::getEb));
        return lista;
    }

    void avaliar(ControlePreventiva c, LocalDate hoje) {
        if (c.getDataUltima() == null) {
            c.sinalizar(true, "Sem preventiva registrada");
            return;
        }
        int limite = c.getKmLimite() > 0 ? c.getKmLimite() : KM_PADRAO;
        boolean porKm = c.getKmPercorridos() >= limite;
        LocalDate ultima = Instant.ofEpochMilli(c.getDataUltima()).atZone(ZoneId.systemDefault()).toLocalDate();
        boolean porData = !ultima.plusMonths(MESES_LIMITE).isAfter(hoje);
        if (porKm && porData) {
            c.sinalizar(true, "Limite de km e de " + MESES_LIMITE + " meses atingidos");
        } else if (porKm) {
            c.sinalizar(true, "Rodou " + c.getKmPercorridos() + " km (limite " + limite + ")");
        } else if (porData) {
            c.sinalizar(true, "Mais de " + MESES_LIMITE + " meses desde a última");
        } else {
            c.sinalizar(false, "Faltam " + (limite - c.getKmPercorridos()) + " km");
        }
    }
}
