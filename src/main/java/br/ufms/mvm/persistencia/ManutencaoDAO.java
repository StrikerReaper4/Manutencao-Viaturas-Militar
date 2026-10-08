package br.ufms.mvm.persistencia;

import br.ufms.mvm.negocio.Manutencao;
import br.ufms.mvm.negocio.Mecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    // retorna so os dados basicos, sem viatura/pane carregados
    public Manutencao buscarPorID(int id) throws SQLException {
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Manutencao WHERE idManutencao = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Manutencao m = new Manutencao(rs.getString("tipo"), rs.getLong("dataInicio"), rs.getInt("odometroEntrada"));
                m.setIdManutencao(rs.getInt("idManutencao"));
                m.setSituacao(rs.getString("situacao"));
                return m;
            }
        }
    }

    // odometro da ultima preventiva da viatura (null se nunca teve)
    public Integer buscarOdometroUltimaPreventiva(String eb) throws SQLException {
        String sql = "SELECT odometroEntrada FROM TB_Manutencao WHERE EB = ? AND tipo = ? "
                   + "ORDER BY dataInicio DESC LIMIT 1";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, eb);
            ps.setString(2, Manutencao.PREVENTIVA);
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
