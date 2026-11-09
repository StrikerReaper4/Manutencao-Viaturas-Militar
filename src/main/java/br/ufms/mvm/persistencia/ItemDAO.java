package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public List<Item> listarAtivos() throws SQLException {
        List<Item> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Item WHERE ativo = 1 ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public Item buscarPorID(int id) throws SQLException {
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Item WHERE idItem = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    private Item montar(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("idItem"),
                rs.getString("nome"),
                rs.getString("unidadeMedida"),
                rs.getInt("quantidadeEstoque"),
                rs.getInt("ativo") == 1);
    }
}
