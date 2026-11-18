package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.Item;
import br.ufms.mvm.modelo.ItemManutencao_Material;
import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.Viatura;
import br.ufms.mvm.persistencia.Conexao;
import br.ufms.mvm.persistencia.ItemDAO;
import br.ufms.mvm.persistencia.PaneDAO;
import br.ufms.mvm.persistencia.PaneItemDAO;
import br.ufms.mvm.persistencia.ViaturaDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

// Caso de uso Registrar pane (RF09), privativo do Operador.
// Na iteracao 5 ganhou as pecas necessarias (regra do RF12): elas entram na lista de
// pecas que a viatura esta aguardando ate a pane ser atendida.
public class ControladoraRegistrarPane {

    public static final int PRIORIDADE_MAXIMA = 1;
    public static final int PRIORIDADE_MINIMA = 5;
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final PaneDAO paneDAO = new PaneDAO();
    private final ViaturaDAO viaturaDAO = new ViaturaDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final PaneItemDAO paneItemDAO = new PaneItemDAO();

    // pecas indicadas na pane em curso (ainda nao gravadas); "faltante" = necessaria
    private final List<ItemManutencao_Material> pecas = new ArrayList<>();

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

    public List<Item> consultarItensAtivos() throws SQLException {
        return itemDAO.listarAtivos();
    }

    public void incluirPeca(int idItem, String quantidade) throws SQLException {
        Item it = itemDAO.buscarPorID(idItem);
        if (it == null || !it.isAtivo()) {
            throw new IllegalArgumentException(MensagensRelatorio.ITEM_INATIVO);
        }
        if (quantidade == null || quantidade.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Quantidade"));
        }
        int qtd;
        try {
            qtd = Integer.parseInt(quantidade.trim());
        } catch (NumberFormatException e) {
            qtd = 0;
        }
        if (qtd <= 0) {
            throw new IllegalArgumentException(MensagensRelatorio.QUANTIDADE_POSITIVA);
        }
        if (pecas.stream().anyMatch(p -> p.getItem().getIdItem() == idItem)) {
            throw new IllegalArgumentException(MensagensRelatorio.PECA_REPETIDA);
        }
        pecas.add(new ItemManutencao_Material(it, qtd, true));
    }

    public void removerPeca(int idItem) {
        pecas.removeIf(p -> p.getItem().getIdItem() == idItem);
    }

    public List<ItemManutencao_Material> getPecas() {
        return pecas;
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

        // pane e pecas na mesma transacao
        Connection con = Conexao.getConexao();
        con.setAutoCommit(false);
        try {
            int id = paneDAO.inserir(p);
            for (ItemManutencao_Material peca : pecas) {
                paneItemDAO.inserir(id, peca);
            }
            con.commit();
            pecas.clear();
            return id;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }
}
