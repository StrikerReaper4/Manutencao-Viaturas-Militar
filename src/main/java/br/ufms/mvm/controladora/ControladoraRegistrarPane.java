package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.Viatura;
import br.ufms.mvm.persistencia.PaneDAO;
import br.ufms.mvm.persistencia.ViaturaDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

// Caso de uso Registrar pane (RF09), privativo do Operador
public class ControladoraRegistrarPane {

    public static final int PRIORIDADE_MAXIMA = 1;
    public static final int PRIORIDADE_MINIMA = 5;
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final PaneDAO paneDAO = new PaneDAO();
    private final ViaturaDAO viaturaDAO = new ViaturaDAO();

    public ControladoraRegistrarPane() {
        if (!Sessao.isOperador()) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    public List<Viatura> consultarViaturasAtivas() throws SQLException {
        return viaturaDAO.listarViatura();
    }

    public List<Pane> listarFilaAtendimento() throws SQLException {
        return paneDAO.listarFilaAtendimento();
    }

    public int registrarPane(String eb, String descricao, int prioridade, String dataLimite,
                             String missao, LocalDate hoje) throws SQLException {
        if (eb == null) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("EB da viatura"));
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Descrição da pane"));
        }
        // regra do RF09: toda pane tem prioridade
        if (prioridade < PRIORIDADE_MAXIMA || prioridade > PRIORIDADE_MINIMA) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Prioridade"));
        }
        if (dataLimite == null || dataLimite.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Data limite"));
        }
        LocalDate limite;
        try {
            limite = LocalDate.parse(dataLimite.trim(), FORMATO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(MensagensCadastro.DATA_INVALIDA);
        }
        if (limite.isBefore(hoje)) {
            throw new IllegalArgumentException(MensagensCadastro.DATA_PASSADA);
        }
        Viatura v = viaturaDAO.buscarPorEB(eb);
        if (v == null || !v.isAtivo()) {
            throw new IllegalArgumentException(MensagensCadastro.VIATURA_INATIVA);
        }

        long millis = limite.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String missaoLimpa = missao == null || missao.isBlank() ? null : missao.trim();
        Pane p = new Pane(0, descricao.trim(), prioridade, millis, missaoLimpa, Pane.REGISTRADA, eb);
        return paneDAO.inserir(p);
    }
}
