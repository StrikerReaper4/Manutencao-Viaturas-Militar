package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.Filtros;
import br.ufms.mvm.modelo.Item;
import br.ufms.mvm.modelo.ItemManutencao;
import br.ufms.mvm.modelo.ItemManutencao_Material;
import br.ufms.mvm.modelo.Manutencao;
import br.ufms.mvm.modelo.Mecanico;
import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.Viatura;
import br.ufms.mvm.persistencia.Conexao;
import br.ufms.mvm.persistencia.FiltroDAO;
import br.ufms.mvm.persistencia.ItemDAO;
import br.ufms.mvm.persistencia.ItemManutencaoDAO;
import br.ufms.mvm.persistencia.ItemManutencaoMaterialDAO;
import br.ufms.mvm.persistencia.ItemManutencaoServicoDAO;
import br.ufms.mvm.persistencia.ManutencaoDAO;
import br.ufms.mvm.persistencia.MecanicoDAO;
import br.ufms.mvm.persistencia.PaneDAO;
import br.ufms.mvm.persistencia.ViaturaDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControladoraEncerrarManutencao {

    private final ManutencaoDAO manutencaoDAO = new ManutencaoDAO();
    private final ViaturaDAO viaturaDAO = new ViaturaDAO();
    private final PaneDAO paneDAO = new PaneDAO();
    private final MecanicoDAO mecanicoDAO = new MecanicoDAO();
    private final FiltroDAO filtroDAO = new FiltroDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final ItemManutencaoDAO itemManutencaoDAO = new ItemManutencaoDAO();
    private final ItemManutencaoMaterialDAO materialDAO = new ItemManutencaoMaterialDAO();
    private final ItemManutencaoServicoDAO servicoDAO = new ItemManutencaoServicoDAO();

    // dados do encerramento em curso (ainda nao gravados)
    private final List<ItemManutencao_Material> itensIncluidos = new ArrayList<>();
    private final List<ItemManutencao_Material> materiaisFaltantes = new ArrayList<>();
    private final List<Mecanico> mecanicosIncluidos = new ArrayList<>();
    private Integer odometroEntrada;
    private String erroAbertura;

    public long passarData() {
        return System.currentTimeMillis();
    }

    public List<Manutencao> consultarManutencoesEmAndamento() throws SQLException {
        return manutencaoDAO.listarEmAndamento();
    }

    public Manutencao buscarManutencao(int idManutencao) throws SQLException {
        Manutencao m = manutencaoDAO.buscarPorID(idManutencao);
        if (m != null) {
            odometroEntrada = m.getOdometroEntrada();
        }
        return m;
    }

    public List<Viatura> consultarViaturasAtivas() throws SQLException {
        return viaturaDAO.listarViatura();
    }

    public List<Pane> listarPanes(String eb) throws SQLException {
        return paneDAO.listarNaoAtendidas(eb);
    }

    public Pane buscarPane(int paneID) throws SQLException {
        return paneDAO.buscarPorID(paneID);
    }

    // usado pra mostrar a M13 logo que a viatura eh selecionada (FAV1.2)
    public Integer buscarManutencaoEmAndamento(String eb) throws SQLException {
        return manutencaoDAO.buscarEmAndamento(eb);
    }

    // FAV1.5.1 e FAV1.5.2. Se for falso, getErroAbertura diz qual mensagem mostrar
    public boolean validarAbertura(String eb, long dataAbertura, int odometro, long dataAtual) throws SQLException {
        erroAbertura = null;
        Viatura v = viaturaDAO.buscarPorEB(eb);
        if (v == null || !v.isAtivo()) {
            erroAbertura = MensagensEncerrar.M12;
        } else if (odometro < v.getOdometro()) {
            erroAbertura = MensagensEncerrar.m14(odometro, v.getOdometro());
        } else if (dataAbertura > dataAtual) {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            erroAbertura = MensagensEncerrar.m15(f.format(new Date(dataAbertura)), f.format(new Date(dataAtual)));
        }
        if (erroAbertura == null) {
            odometroEntrada = odometro;
        }
        return erroAbertura == null;
    }

    public String getErroAbertura() {
        return erroAbertura;
    }

    public List<Mecanico> consultarMecanicosAtivos() throws SQLException {
        return mecanicoDAO.listarAtivos();
    }

    public void incluirMecanico(int idMecanico) throws SQLException {
        Mecanico me = mecanicoDAO.buscarPorID(idMecanico);
        if (me == null || !me.isAtivo()) {
            throw new IllegalArgumentException(MensagensEncerrar.M12);
        }
        boolean jaIncluido = mecanicosIncluidos.stream().anyMatch(x -> x.getIdUsuario() == idMecanico);
        if (jaIncluido) {
            throw new IllegalArgumentException(MensagensEncerrar.M16);
        }
        mecanicosIncluidos.add(me);
    }

    public List<Mecanico> getMecanicosIncluidos() {
        return mecanicosIncluidos;
    }

    // RN02
    public boolean validarOdometroSaida(int odometroSaida) {
        return odometroEntrada != null && odometroSaida >= odometroEntrada;
    }

    public int getOdometroEntrada() {
        return odometroEntrada != null ? odometroEntrada : 0;
    }

    // filtros previstos na abertura ou, se nao houver, os indicados pra viatura
    public List<Filtros> listarFiltros(Integer idManutencao, String eb) throws SQLException {
        if (idManutencao != null) {
            List<Filtros> previstos = new ArrayList<>();
            for (ItemManutencao imf : itemManutencaoDAO.listarPorManutencao(idManutencao)) {
                previstos.add(imf.getFiltro());
            }
            if (!previstos.isEmpty()) {
                return previstos;
            }
        }
        return filtroDAO.listarPorViatura(eb);
    }

    public List<Item> consultarItensAtivos() throws SQLException {
        return itemDAO.listarAtivos();
    }

    public void incluirItem(int idItem, int quantidade) throws SQLException {
        incluir(itensIncluidos, idItem, quantidade, false);
    }

    public void incluirMaterialFaltante(int idItem, int quantidade) throws SQLException {
        incluir(materiaisFaltantes, idItem, quantidade, true);
    }

    private void incluir(List<ItemManutencao_Material> lista, int idItem, int quantidade, boolean faltante)
            throws SQLException {
        Item it = itemDAO.buscarPorID(idItem);
        if (it == null || !it.isAtivo()) {
            throw new IllegalArgumentException(MensagensEncerrar.M12);
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException(MensagensEncerrar.m07(it.getNome()));
        }
        boolean jaIncluido = lista.stream().anyMatch(x -> x.getItem().getIdItem() == idItem);
        if (jaIncluido) {
            throw new IllegalArgumentException(MensagensEncerrar.M17);
        }
        lista.add(new ItemManutencao_Material(it, quantidade, faltante));
    }

    public List<ItemManutencao_Material> getItensIncluidos() {
        return itensIncluidos;
    }

    public List<ItemManutencao_Material> getMateriaisFaltantes() {
        return materiaisFaltantes;
    }

    public void cancelarEncerramento() {
        itensIncluidos.clear();
        materiaisFaltantes.clear();
        mecanicosIncluidos.clear();
        odometroEntrada = null;
    }

    // Operacao do contrato CO02. Os numeros nos comentarios sao as mensagens do diagrama de comunicacao.
    public int gravarEncerramento(int idManutencao, int odometroSaida, String tipo, String situacaoFinal,
                                  long dataAtual, List<Filtros> filtros, String servicoFaltante) throws SQLException {

        // 1.1 a 1.3
        Manutencao m = manutencaoDAO.buscarPorID(idManutencao);
        if (m == null || !Manutencao.EM_ANDAMENTO.equals(m.getSituacao())) {
            throw new IllegalArgumentException(MensagensEncerrar.M11);
        }
        Viatura v = viaturaDAO.buscarPorEB(m.getEb());
        Pane p = null;
        if (m.getIdPane() != null) {
            p = paneDAO.buscarPorID(m.getIdPane());
        }

        if (v == null || !v.isAtivo()) {
            throw new IllegalArgumentException(MensagensEncerrar.M12);
        }
        validarEncerramento(odometroSaida, m.getOdometroEntrada(), tipo, situacaoFinal, filtros);

        Connection con = Conexao.getConexao();
        con.setAutoCommit(false);
        try {
            // 1.4 e 1.5
            m.encerrar(dataAtual, odometroSaida, tipo, situacaoFinal);
            m.setSituacao(Manutencao.ENCERRADA);

            // 1.6 a 1.8 (os <<create>> acontecem dentro de Manutencao)
            registrarMateriais(m, servicoFaltante);

            // 1.9 e 1.9.1
            if (Manutencao.PREVENTIVA.equals(tipo)) {
                m.registrarFiltros(filtros);
            }

            // 1.10 e 1.11
            if (p != null) {
                if (servicoFaltante == null) {
                    p.setSituacao(Pane.ATENDIDA);
                }
                paneDAO.atualizar(p);
            }

            // 1.12 a 1.14
            v.setSituacao(situacaoFinal);
            v.setOdometro(odometroSaida);
            viaturaDAO.atualizar(v);

            // 1.15
            manutencaoDAO.atualizar(m);

            // 1.16 e 1.17
            gravarMateriais(m);

            // 1.18 e 1.19
            if (Manutencao.PREVENTIVA.equals(tipo)) {
                for (ItemManutencao imf : m.getItens()) {
                    itemManutencaoDAO.atualizar(imf);
                }
            } else if (Manutencao.CORRETIVA.equals(tipo)) {
                itemManutencaoDAO.removerPorManutencao(idManutencao);
            }

            con.commit();
            cancelarEncerramento();
            return idManutencao;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    // Variante 1: abre e encerra de uma vez, valem tambem as regras do Abrir (RN08)
    public int registrarManutencaoExecutada(String eb, Integer paneID, long dataAbertura, int odometroEntradaInformado,
                                            int odometroSaida, String tipo, String situacaoFinal, long dataAtual,
                                            List<Filtros> filtros, String servicoFaltante) throws SQLException {
        Integer emAndamento = manutencaoDAO.buscarEmAndamento(eb);
        if (emAndamento != null) {
            throw new IllegalArgumentException(MensagensEncerrar.m13(eb, emAndamento));
        }
        if (!validarAbertura(eb, dataAbertura, odometroEntradaInformado, dataAtual)) {
            throw new IllegalArgumentException(erroAbertura);
        }
        if (mecanicosIncluidos.isEmpty()) {
            throw new IllegalArgumentException(MensagensEncerrar.M04);
        }
        validarEncerramento(odometroSaida, odometroEntradaInformado, tipo, situacaoFinal, filtros);

        Viatura v = viaturaDAO.buscarPorEB(eb);
        Pane p = paneID != null ? paneDAO.buscarPorID(paneID) : null;

        Connection con = Conexao.getConexao();
        con.setAutoCommit(false);
        try {
            Manutencao m = new Manutencao(tipo, dataAbertura, odometroEntradaInformado);
            m.associarViatura(v);
            if (p != null) {
                m.associarPane(p);
            }
            for (Mecanico me : mecanicosIncluidos) {
                m.adicionarMecanico(me);
            }
            m.encerrar(dataAtual, odometroSaida, tipo, situacaoFinal);
            m.setSituacao(Manutencao.ENCERRADA);
            registrarMateriais(m, servicoFaltante);
            if (Manutencao.PREVENTIVA.equals(tipo)) {
                m.registrarFiltros(filtros);
            }

            if (p != null) {
                p.setSituacao(servicoFaltante == null ? Pane.ATENDIDA : Pane.EM_ATENDIMENTO);
                paneDAO.atualizar(p);
            }
            v.setSituacao(situacaoFinal);
            v.setOdometro(odometroSaida);
            viaturaDAO.atualizar(v);

            int id = manutencaoDAO.inserir(m);
            gravarMateriais(m);
            for (ItemManutencao imf : m.getItens()) {
                itemManutencaoDAO.inserir(imf);
            }

            con.commit();
            cancelarEncerramento();
            return id;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    private void registrarMateriais(Manutencao m, String servicoFaltante) {
        for (ItemManutencao_Material it : itensIncluidos) {
            m.registrarMaterial(it.getItem(), it.getQuantidade(), false);
        }
        for (ItemManutencao_Material it : materiaisFaltantes) {
            m.registrarMaterial(it.getItem(), it.getQuantidade(), true);
        }
        if (servicoFaltante != null) {
            m.registrarServicoFaltante(servicoFaltante);
        }
    }

    private void gravarMateriais(Manutencao m) throws SQLException {
        for (ItemManutencao_Material imm : m.getMateriais()) {
            materialDAO.inserir(imm);
        }
        if (m.getServicoFaltante() != null) {
            servicoDAO.inserir(m.getServicoFaltante());
        }
    }

    // pre-condicoes do contrato / regras RN01, RN02 e RN04
    private void validarEncerramento(int odometroSaida, int entrada, String tipo, String situacaoFinal,
                                     List<Filtros> filtros) {
        if (odometroSaida < entrada) {
            throw new IllegalArgumentException(MensagensEncerrar.m03(odometroSaida, entrada));
        }
        if (situacaoFinal == null) {
            throw new IllegalArgumentException(MensagensEncerrar.m02("Situação final da viatura"));
        }
        if (Manutencao.PREVENTIVA.equals(tipo) && (filtros == null || filtros.isEmpty())) {
            throw new IllegalArgumentException(MensagensEncerrar.M06);
        }
    }
}
