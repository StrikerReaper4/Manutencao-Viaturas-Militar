package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.LinhaRelatorioMensal;
import br.ufms.mvm.persistencia.RelatorioDAO;

import java.sql.SQLException;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

// Caso de uso Emitir relatorio mensal (RF14), privativo do Operador.
// Traz as manutencoes que comecaram ou terminaram no mes. O que ainda nao foi
// preenchido (encerramento, situacao final, valor) sai em branco.
public class ControladoraEmitirRelatorioMensal {

    private final RelatorioDAO relatorioDAO = new RelatorioDAO();

    public ControladoraEmitirRelatorioMensal() {
        if (!Sessao.isOperador()) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    public List<LinhaRelatorioMensal> emitir(int mes, String ano) throws SQLException {
        int a;
        try {
            a = Integer.parseInt(ano == null ? "" : ano.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MensagensRelatorio.MES_INVALIDO);
        }
        if (a < 2000 || a > 2100 || mes < 1 || mes > 12) {
            throw new IllegalArgumentException(MensagensRelatorio.MES_INVALIDO);
        }
        YearMonth ym = YearMonth.of(a, mes);
        ZoneId zona = ZoneId.systemDefault();
        long de = ym.atDay(1).atStartOfDay(zona).toInstant().toEpochMilli();
        long ate = ym.plusMonths(1).atDay(1).atStartOfDay(zona).toInstant().toEpochMilli() - 1;
        return relatorioDAO.listarMensal(de, ate);
    }
}
