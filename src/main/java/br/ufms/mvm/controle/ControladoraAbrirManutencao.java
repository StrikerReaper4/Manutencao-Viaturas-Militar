package br.ufms.mvm.controle;

import br.ufms.mvm.negocio.Filtros;
import br.ufms.mvm.negocio.ItemManutencao;
import br.ufms.mvm.negocio.Manutencao;
import br.ufms.mvm.negocio.Mecanico;
import br.ufms.mvm.negocio.Pane;
import br.ufms.mvm.negocio.Viatura;
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

public class ControladoraAbrirManutencao {

    private final ViaturaDAO viaturaDAO = new ViaturaDAO();
    private final PaneDAO paneDAO = new PaneDAO();
    private final MecanicoDAO mecanicoDAO = new MecanicoDAO();
    private final FiltroDAO filtroDAO = new FiltroDAO();
    private final ManutencaoDAO manutencaoDAO = new ManutencaoDAO();
    private final ItemManutencaoDAO itemManutencaoDAO = new ItemManutencaoDAO();

    // mecanicos incluidos na abertura em curso (ainda nao gravados)
    private final List<Mecanico> mecanicosIncluidos = new ArrayList<>();

    public long passarData() {
        return System.currentTimeMillis();
    }

    public List<Viatura> consultarViaturasAtivas() throws SQLException {
        return viaturaDAO.listarViatura();
    }

    public List<Pane> listarPane(String eb) throws SQLException {
        return paneDAO.listarNaoAtendidas(eb);
    }

    // RN03
    public boolean validarOdometro(int odometro, String eb) throws SQLException {
        Viatura v = viaturaDAO.buscarPorEB(eb);
        return v != null && odometro >= v.getOdometro();
    }

    public int buscarOdometroAtual(String eb) throws SQLException {
        Viatura v = viaturaDAO.buscarPorEB(eb);
        return v != null ? v.getOdometro() : 0;
    }

    // usado pra mostrar a M05 logo que a viatura eh selecionada (FA02)
    public Integer buscarManutencaoEmAndamento(String eb) throws SQLException {
        return manutencaoDAO.buscarEmAndamento(eb);
    }

    // FAV1.1: quantos km faltam pra proxima preventiva (<= 0 quer dizer que ja venceu)
    public int calcularKmParaPreventiva(String eb, Integer odometro) throws SQLException {
        Viatura v = viaturaDAO.buscarPorEB(eb);
        if (v == null) {
            return 0;
        }
        int odometroReferencia = odometro != null ? odometro : v.getOdometro();
        Integer ultima = manutencaoDAO.buscarOdometroUltimaPreventiva(eb);
        int base = ultima != null ? ultima : 0;
        return base + v.getKmManutencaoPreventiva() - odometroReferencia;
    }

    public Pane buscarPane(int paneID) throws SQLException {
        return paneDAO.buscarPorID(paneID);
    }

    public List<Filtros> listarFiltros(String eb) throws SQLException {
        return filtroDAO.listarPorViatura(eb);
    }

    public List<Mecanico> consultarMecanicosAtivos() throws SQLException {
        return mecanicoDAO.listarAtivos();
    }

    public void incluirMecanico(int idMecanico) throws SQLException {
        Mecanico me = mecanicoDAO.buscarPorID(idMecanico);
        if (me == null || !me.isAtivo()) {
            throw new IllegalArgumentException(Mensagens.M12);
        }
        boolean jaIncluido = mecanicosIncluidos.stream().anyMatch(x -> x.getIdUsuario() == idMecanico);
        if (!jaIncluido) {
            mecanicosIncluidos.add(me);
        }
    }

    public List<Mecanico> getMecanicosIncluidos() {
        return mecanicosIncluidos;
    }

    public void cancelarAbertura() {
        mecanicosIncluidos.clear();
    }

    // Operacao do contrato CO01. Os numeros nos comentarios sao as mensagens do diagrama de comunicacao.
    public int gravarManutencao(String eb, Integer paneID, int odometro, String tipo, long dataAtual, int[] filtros)
            throws SQLException {

        // 1.1 e 1.2
        Viatura v = viaturaDAO.buscarPorEB(eb);
        Pane p = null;
        if (paneID != null) {
            p = paneDAO.buscarPorID(paneID);
        }

        validarAbertura(v, p, paneID, odometro, tipo, filtros);

        Connection con = Conexao.getConexao();
        con.setAutoCommit(false);
        try {
            // 1.3 a 1.7
            Manutencao m = new Manutencao(tipo, dataAtual, odometro);
            m.associarViatura(v);
            if (p != null) {
                m.associarPane(p);
            }
            for (Mecanico me : mecanicosIncluidos) {
                m.adicionarMecanico(me);
            }
            m.setSituacao(Manutencao.EM_ANDAMENTO);

            // 1.8 e 1.9
            if (p != null) {
                p.setSituacao(Pane.EM_ATENDIMENTO);
                paneDAO.atualizar(p);
            }

            // 1.10 e 1.11
            v.setSituacao(Viatura.INDISPONIVEL);
            viaturaDAO.atualizar(v);

            // 1.12 e 1.13 (o 1.13.1 acontece dentro de preverFiltros)
            List<ItemManutencao> itens = new ArrayList<>();
            if (Manutencao.PREVENTIVA.equals(tipo)) {
                List<Filtros> listaFiltros = new ArrayList<>();
                for (int idF : filtros) {
                    Filtros f = filtroDAO.buscarPorID(idF);
                    if (f != null) {
                        listaFiltros.add(f);
                    }
                }
                itens = m.preverFiltros(listaFiltros);
            }

            // 1.14
            int idManutencao = manutencaoDAO.inserir(m);

            // 1.15
            for (ItemManutencao imf : itens) {
                itemManutencaoDAO.inserir(imf);
            }

            con.commit();
            mecanicosIncluidos.clear();
            return idManutencao;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    // pre-condicoes do contrato / regras RN01 a RN05
    private void validarAbertura(Viatura v, Pane p, Integer paneID, int odometro, String tipo, int[] filtros)
            throws SQLException {
        if (v == null || !v.isAtivo()) {
            throw new IllegalArgumentException(Mensagens.M12);
        }
        if (paneID != null && (p == null || !Pane.REGISTRADA.equals(p.getSituacao()))) {
            throw new IllegalArgumentException(Mensagens.M12);
        }

        Integer emAndamento = manutencaoDAO.buscarEmAndamento(v.getEb());
        if (emAndamento != null) {
            throw new IllegalArgumentException(Mensagens.m05(v.getEb(), emAndamento));
        }

        if (odometro < v.getOdometro()) {
            throw new IllegalArgumentException(Mensagens.m03(odometro, v.getOdometro()));
        }

        if (Manutencao.CORRETIVA.equals(tipo) && p == null) {
            throw new IllegalArgumentException(Mensagens.M07);
        }
        if (Manutencao.PREVENTIVA.equals(tipo) && (filtros == null || filtros.length == 0)) {
            throw new IllegalArgumentException(Mensagens.M08);
        }

        if (mecanicosIncluidos.isEmpty()) {
            throw new IllegalArgumentException(Mensagens.M04);
        }
    }
}
