package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.ItemManutencao;
import br.ufms.mvm.modelo.Manutencao;
import br.ufms.mvm.modelo.Mecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoDAO {

    // grava a manutencao e os mecanicos responsaveis, devolve o id gerado
    public int inserir(Manutencao m) throws SQLException {
        String sql = "INSERT INTO TB_Manutencao (tipo, dataInicio, dataEncerramento, odometroEntrada, odometroSaida, "
                   + "situacao, situacaoFinal, EB, idPane) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        int id;
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getTipo());
            ps.setLong(2, m.getDataInicio());
            ps.setObject(3, m.getDataEncerramento());
            ps.setInt(4, m.getOdometroEntrada());
            ps.setObject(5, m.getOdometroSaida());
            ps.setString(6, m.getSituacao());
            ps.setString(7, m.getSituacaoFinal());
            ps.setString(8, m.getViatura().getEb());
            ps.setObject(9, m.getPane() != null ? m.getPane().getIdPane() : null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt(1);
            }
        }
        m.setIdManutencao(id);

        String sqlMecanico = "INSERT INTO TB_Manutencao_Mecanico (idManutencao, idUsuario) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sqlMecanico)) {
            for (Mecanico me : m.getMecanicos()) {
                ps.setInt(1, id);
                ps.setInt(2, me.getIdUsuario());
                ps.executeUpdate();
            }
        }
        return id;
    }

    public void atualizar(Manutencao m) throws SQLException {
        String sql = "UPDATE TB_Manutencao SET tipo = ?, dataEncerramento = ?, odometroSaida = ?, situacao = ?, "
                   + "situacaoFinal = ? WHERE idManutencao = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getTipo());
            ps.setObject(2, m.getDataEncerramento());
            ps.setObject(3, m.getOdometroSaida());
            ps.setString(4, m.getSituacao());
            ps.setString(5, m.getSituacaoFinal());
            ps.setInt(6, m.getIdManutencao());
            ps.executeUpdate();
        }
    }

    // traz a manutencao com os itens de filtro e os mecanicos; viatura e pane vem so pela chave
    public Manutencao buscarPorID(int id) throws SQLException {
        Connection con = Conexao.getConexao();
        Manutencao m;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Manutencao WHERE idManutencao = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                m = new Manutencao(rs.getString("tipo"), rs.getLong("dataInicio"), rs.getInt("odometroEntrada"));
                m.setIdManutencao(rs.getInt("idManutencao"));
                m.setSituacao(rs.getString("situacao"));
                int idPane = rs.getInt("idPane");
                m.setChaves(rs.getString("EB"), rs.wasNull() ? null : idPane);
            }
        }

        for (ItemManutencao imf : new ItemManutencaoDAO().listarPorManutencao(id)) {
            m.carregarItem(imf);
        }
        MecanicoDAO mecanicoDAO = new MecanicoDAO();
        for (int idUsuario : listarInteiros("SELECT idUsuario FROM TB_Manutencao_Mecanico WHERE idManutencao = ?", id)) {
            m.adicionarMecanico(mecanicoDAO.buscarPorID(idUsuario));
        }
        return m;
    }

    public List<Manutencao> listarEmAndamento() throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT idManutencao FROM TB_Manutencao WHERE situacao = ? ORDER BY idManutencao";
        try (PreparedStatement ps = Conexao.getConexao().prepareStatement(sql)) {
            ps.setString(1, Manutencao.EM_ANDAMENTO);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        }
        List<Manutencao> lista = new ArrayList<>();
        for (int id : ids) {
            lista.add(buscarPorID(id));
        }
        return lista;
    }

    private List<Integer> listarInteiros(String sql, int parametro) throws SQLException {
        List<Integer> valores = new ArrayList<>();
        try (PreparedStatement ps = Conexao.getConexao().prepareStatement(sql)) {
            ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    valores.add(rs.getInt(1));
                }
            }
        }
        return valores;
    }

    // odometro de saida da ultima preventiva encerrada (null se nunca teve)
    public Integer buscarOdometroUltimaPreventiva(String eb) throws SQLException {
        String sql = "SELECT odometroSaida FROM TB_Manutencao WHERE EB = ? AND tipo = ? AND situacao = ? "
                   + "ORDER BY dataEncerramento DESC LIMIT 1";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, eb);
            ps.setString(2, Manutencao.PREVENTIVA);
            ps.setString(3, Manutencao.ENCERRADA);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    // RN05: uma viatura nao pode ter duas manutencoes em andamento
    public Integer buscarEmAndamento(String eb) throws SQLException {
        String sql = "SELECT idManutencao FROM TB_Manutencao WHERE EB = ? AND situacao = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, eb);
            ps.setString(2, Manutencao.EM_ANDAMENTO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
}
