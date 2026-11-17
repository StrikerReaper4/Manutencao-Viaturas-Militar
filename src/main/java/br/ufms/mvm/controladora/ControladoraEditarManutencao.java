package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.Filtros;
import br.ufms.mvm.modelo.ItemManutencao;
import br.ufms.mvm.modelo.Manutencao;
import br.ufms.mvm.modelo.Mecanico;
import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.Usuario;
import br.ufms.mvm.modelo.Viatura;
import br.ufms.mvm.persistencia.Conexao;
import br.ufms.mvm.persistencia.FiltroDAO;
import br.ufms.mvm.persistencia.ItemManutencaoDAO;
import br.ufms.mvm.persistencia.ManutencaoDAO;
import br.ufms.mvm.persistencia.MecanicoDAO;
import br.ufms.mvm.persistencia.PaneDAO;
import br.ufms.mvm.persistencia.ViaturaDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Caso de uso Editar manutencao (RF07).
// Regra: o Operador altera qualquer manutencao e adiciona/remove mecanicos a vontade;
// o Mecanico so altera as que abriu ou em que eh responsavel, e so pode ADICIONAR mecanicos.
public class ControladoraEditarManutencao {

    private final ManutencaoDAO manutencaoDAO = new ManutencaoDAO();
    private final ViaturaDAO viaturaDAO = new ViaturaDAO();
    private final PaneDAO paneDAO = new PaneDAO();
    private final MecanicoDAO mecanicoDAO = new MecanicoDAO();
    private final FiltroDAO filtroDAO = new FiltroDAO();
    private final ItemManutencaoDAO itemManutencaoDAO = new ItemManutencaoDAO();

    // manutencao em edicao e a lista de responsaveis ainda nao gravada
    private Manutencao emEdicao;
    private final List<Mecanico> mecanicos = new ArrayList<>();

    public ControladoraEditarManutencao() {
        if (Sessao.getUsuarioLogado() == null) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    // so as manutencoes em andamento que o usuario pode alterar (RNF14: encerradas ficam de fora)
    public List<Manutencao> listarManutencoesEditaveis() throws SQLException {
        List<Manutencao> lista = new ArrayList<>();
        for (Manutencao m : manutencaoDAO.listarEmAndamento()) {
            if (podeEditar(m)) {
                lista.add(m);
            }
        }
        return lista;
    }

    private boolean podeEditar(Manutencao m) {
        if (Sessao.isOperador()) {
            return true;
        }
        int eu = Sessao.getUsuarioLogado().getIdUsuario();
        if (Objects.equals(m.getIdUsuarioAbertura(), eu)) {
            return true;
        }
        return m.getMecanicos().stream().anyMatch(me -> me != null && me.getIdUsuario() == eu);
    }

    public boolean podeRemoverMecanico() {
        return Sessao.isOperador();
    }

    public Manutencao carregarManutencao(int idManutencao) throws SQLException {
        Manutencao m = manutencaoDAO.buscarPorID(idManutencao);
        if (m == null || !Manutencao.EM_ANDAMENTO.equals(m.getSituacao())) {
            throw new IllegalArgumentException(MensagensEdicao.NAO_EDITAVEL);
        }
        if (!podeEditar(m)) {
            throw new IllegalArgumentException(MensagensEdicao.SEM_ACESSO_MANUTENCAO);
        }
        emEdicao = m;
        mecanicos.clear();
        for (Mecanico me : m.getMecanicos()) {
            if (me != null) {
                mecanicos.add(me);
            }
        }
        return m;
    }

    public Viatura buscarViatura(String eb) throws SQLException {
        return viaturaDAO.buscarPorEB(eb);
    }

    // panes que podem ser escolhidas: as ainda nao atendidas da viatura + a que ja esta na manutencao
    public List<Pane> listarPanes() throws SQLException {
        List<Pane> lista = new ArrayList<>();
        if (emEdicao.getIdPane() != null) {
            Pane atual = paneDAO.buscarPorID(emEdicao.getIdPane());
            if (atual != null) {
                lista.add(atual);
            }
        }
        lista.addAll(paneDAO.listarNaoAtendidas(emEdicao.getEb()));
        return lista;
    }

    public List<Filtros> listarFiltros() throws SQLException {
        return filtroDAO.listarPorViatura(emEdicao.getEb());
    }

    public List<Integer> filtrosPrevistos() {
        List<Integer> ids = new ArrayList<>();
        for (ItemManutencao imf : emEdicao.getItens()) {
            ids.add(imf.getFiltro().getIdFiltro());
        }
        return ids;
    }

    public List<Mecanico> consultarMecanicosAtivos() throws SQLException {
        return mecanicoDAO.listarAtivos();
    }

    public List<Mecanico> getMecanicos() {
        return mecanicos;
    }

    public void incluirMecanico(int idMecanico) throws SQLException {
        Mecanico me = mecanicoDAO.buscarPorID(idMecanico);
        if (me == null || !me.isAtivo()) {
            throw new IllegalArgumentException(MensagensEdicao.MECANICO_INATIVO);
        }
        if (mecanicos.stream().anyMatch(x -> x.getIdUsuario() == idMecanico)) {
            throw new IllegalArgumentException(MensagensEdicao.MECANICO_REPETIDO);
        }
        mecanicos.add(me);
    }

    public void removerMecanico(int idMecanico) {
        if (!podeRemoverMecanico()) {
            throw new IllegalArgumentException(MensagensEdicao.SO_OPERADOR_REMOVE);
        }
        mecanicos.removeIf(x -> x.getIdUsuario() == idMecanico);
    }

    public void cancelarEdicao() {
        emEdicao = null;
        mecanicos.clear();
    }

    // grava a edicao numa transacao so: manutencao, pane antiga/nova, filtros e mecanicos
    public int gravarEdicao(String tipo, Integer paneID, int odometroEntrada, int[] filtros) throws SQLException {
        if (emEdicao == null) {
            throw new IllegalArgumentException(MensagensEdicao.NAO_EDITAVEL);
        }
        Manutencao m = emEdicao;
        Viatura v = viaturaDAO.buscarPorEB(m.getEb());
        Pane novaPane = paneID != null ? paneDAO.buscarPorID(paneID) : null;
        Integer paneAntiga = m.getIdPane();

        // mesmas regras da abertura
        if (v != null && odometroEntrada < v.getOdometro()) {
            throw new IllegalArgumentException(MensagensEdicao.odometroMenor(odometroEntrada, v.getOdometro()));
        }
        if (Manutencao.CORRETIVA.equals(tipo) && novaPane == null) {
            throw new IllegalArgumentException(MensagensEdicao.CORRETIVA_SEM_PANE);
        }
        boolean mesmaPane = paneID != null && paneID.equals(paneAntiga);
        if (novaPane != null && !mesmaPane
                && (!Pane.REGISTRADA.equals(novaPane.getSituacao()) || !novaPane.getEb().equals(m.getEb()))) {
            throw new IllegalArgumentException(MensagensEdicao.PANE_INVALIDA);
        }
        if (Manutencao.PREVENTIVA.equals(tipo) && (filtros == null || filtros.length == 0)) {
            throw new IllegalArgumentException(MensagensEdicao.PREVENTIVA_SEM_FILTRO);
        }
        if (mecanicos.isEmpty()) {
            throw new IllegalArgumentException(MensagensEdicao.SEM_MECANICO);
        }

        Connection con = Conexao.getConexao();
        con.setAutoCommit(false);
        try {
            m.alterarAbertura(tipo, odometroEntrada);

            // pane trocada: a antiga volta pra fila, a nova entra em atendimento
            if (!mesmaPane) {
                if (paneAntiga != null) {
                    Pane antiga = paneDAO.buscarPorID(paneAntiga);
                    antiga.setSituacao(Pane.REGISTRADA);
                    paneDAO.atualizar(antiga);
                }
                m.removerPane();
                if (novaPane != null) {
                    novaPane.setSituacao(Pane.EM_ATENDIMENTO);
                    paneDAO.atualizar(novaPane);
                    m.associarPane(novaPane);
                }
            }
            manutencaoDAO.atualizarAbertura(m);

            // filtros previstos: refaz a lista (so na preventiva)
            itemManutencaoDAO.removerPorManutencao(m.getIdManutencao());
            m.limparItens();
            if (Manutencao.PREVENTIVA.equals(tipo)) {
                List<Filtros> lista = new ArrayList<>();
                for (int idF : filtros) {
                    Filtros f = filtroDAO.buscarPorID(idF);
                    if (f != null) {
                        lista.add(f);
                    }
                }
                for (ItemManutencao imf : m.preverFiltros(lista)) {
                    itemManutencaoDAO.inserir(imf);
                }
            }

            m.getMecanicos().clear();
            for (Mecanico me : mecanicos) {
                m.adicionarMecanico(me);
            }
            manutencaoDAO.substituirMecanicos(m);

            con.commit();
            int id = m.getIdManutencao();
            cancelarEdicao();
            return id;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public Usuario getUsuarioLogado() {
        return Sessao.getUsuarioLogado();
    }
}
