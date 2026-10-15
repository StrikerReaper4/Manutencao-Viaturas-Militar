package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.Pane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaneDAO {

    public Pane buscarPorID(int idPane) throws SQLException {
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Pane WHERE idPane = ?")) {
            ps.setInt(1, idPane);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    // panes que ainda nao foram atendidas, a de maior prioridade primeiro
    public List<Pane> listarNaoAtendidas(String eb) throws SQLException {
        List<Pane> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_Pane WHERE EB = ? AND situacao = ? ORDER BY prioridade";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, eb);
            ps.setString(2, Pane.REGISTRADA);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
        }
        return lista;
    }

    public void atualizar(Pane p) throws SQLException {
        String sql = "UPDATE TB_Pane SET descricao = ?, prioridade = ?, dataLimite = ?, missao = ?, situacao = ? WHERE idPane = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getDescricao());
            ps.setInt(2, p.getPrioridade());
            ps.setObject(3, p.getDataLimite());
            ps.setString(4, p.getMissao());
            ps.setString(5, p.getSituacao());
            ps.setInt(6, p.getIdPane());
            ps.executeUpdate();
        }
    }

    private Pane montar(ResultSet rs) throws SQLException {
        long dataLimite = rs.getLong("dataLimite");
        return new Pane(
                rs.getInt("idPane"),
                rs.getString("descricao"),
                rs.getInt("prioridade"),
                rs.wasNull() ? null : dataLimite,
                rs.getString("missao"),
                rs.getString("situacao"),
                rs.getString("EB"));
    }
}
