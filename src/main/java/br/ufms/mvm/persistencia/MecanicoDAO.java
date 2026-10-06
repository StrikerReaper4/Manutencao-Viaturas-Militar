package br.ufms.mvm.persistencia;

import br.ufms.mvm.negocio.Mecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Mecanico fica na TB_Usuario (heranca em tabela unica)
public class MecanicoDAO {

    private static final String TIPO = "MECANICO";

    public Mecanico buscarPorID(int id) throws SQLException {
        String sql = "SELECT * FROM TB_Usuario WHERE idUsuario = ? AND tipoUsuario = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, TIPO);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public List<Mecanico> listarAtivos() throws SQLException {
        List<Mecanico> lista = new ArrayList<>();
        String sql = "SELECT * FROM TB_Usuario WHERE tipoUsuario = ? AND ativo = 1 ORDER BY nome";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, TIPO);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montar(rs));
                }
            }
        }
        return lista;
    }

    private Mecanico montar(ResultSet rs) throws SQLException {
        return new Mecanico(
                rs.getInt("idUsuario"),
                rs.getString("login"),
                rs.getString("senha"),
                rs.getString("nome"),
                rs.getString("graduacao"),
                rs.getInt("ativo") == 1);
    }
}
