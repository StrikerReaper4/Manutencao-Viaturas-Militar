package br.ufms.mvm.persistencia;

import br.ufms.mvm.negocio.Modelo;
import br.ufms.mvm.negocio.TipoViatura;
import br.ufms.mvm.negocio.Viatura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ViaturaDAO {

    private static final String SELECT_BASE =
            "SELECT v.*, m.nome AS nomeModelo, t.descricao AS nomeTipo FROM TB_Viatura v "
          + "LEFT JOIN TB_Modelo m ON m.idModelo = v.idModelo "
          + "LEFT JOIN TB_TipoViatura t ON t.idTipoViatura = v.idTipoViatura ";

    // so as ativas, que sao as que podem ser escolhidas (RN04)
    public List<Viatura> listarViatura() throws SQLException {
        List<Viatura> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE v.ativo = 1 ORDER BY v.EB");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public Viatura buscarPorEB(String eb) throws SQLException {
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(SELECT_BASE + "WHERE v.EB = ?")) {
            ps.setString(1, eb);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public void inserir(Viatura v) throws SQLException {
        String sql = "INSERT INTO TB_Viatura (EB, placa, kmManutencaoPreventiva, odometroAtual, situacao, ativo, idModelo, idTipoViatura) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getEb());
            ps.setString(2, v.getPlaca());
            ps.setInt(3, v.getKmManutencaoPreventiva());
            ps.setInt(4, v.getOdometro());
            ps.setString(5, v.getSituacao());
            ps.setInt(6, v.isAtivo() ? 1 : 0);
            ps.setObject(7, v.getModelo() != null ? v.getModelo().getIdModelo() : null);
            ps.setObject(8, v.getTipoViatura() != null ? v.getTipoViatura().getIdTipoViatura() : null);
            ps.executeUpdate();
        }
    }

    public void atualizar(Viatura v) throws SQLException {
        String sql = "UPDATE TB_Viatura SET placa = ?, kmManutencaoPreventiva = ?, odometroAtual = ?, situacao = ?, ativo = ? "
                   + "WHERE EB = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setInt(2, v.getKmManutencaoPreventiva());
            ps.setInt(3, v.getOdometro());
            ps.setString(4, v.getSituacao());
            ps.setInt(5, v.isAtivo() ? 1 : 0);
            ps.setString(6, v.getEb());
            ps.executeUpdate();
        }
    }

    private Viatura montar(ResultSet rs) throws SQLException {
        Modelo modelo = new Modelo(rs.getInt("idModelo"), rs.getString("nomeModelo"));
        TipoViatura tipo = new TipoViatura(rs.getInt("idTipoViatura"), rs.getString("nomeTipo"));
        return new Viatura(
                rs.getString("EB"),
                rs.getString("placa"),
                rs.getInt("kmManutencaoPreventiva"),
                rs.getInt("odometroAtual"),
                rs.getString("situacao"),
                rs.getInt("ativo") == 1,
                modelo,
                tipo);
    }
}
