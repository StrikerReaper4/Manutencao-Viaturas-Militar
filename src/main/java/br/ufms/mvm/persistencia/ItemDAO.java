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
        return listar("SELECT * FROM TB_Item WHERE ativo = 1 ORDER BY nome");
    }

    // todos, inclusive os inativos, pro cadastro (RF03)
    public List<Item> listarTodos() throws SQLException {
        return listar("SELECT * FROM TB_Item ORDER BY nome");
    }

    private List<Item> listar(String sql) throws SQLException {
        List<Item> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql);
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

    public void inserir(Item it) throws SQLException {
        String sql = "INSERT INTO TB_Item (nome, unidadeMedida, quantidadeEstoque, ativo) VALUES (?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, it.getNome());
            ps.setString(2, it.getUnidadeMedida());
            ps.setInt(3, it.getQuantidadeEstoque());
            ps.setInt(4, it.isAtivo() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    public void atualizar(Item it) throws SQLException {
        String sql = "UPDATE TB_Item SET nome = ?, unidadeMedida = ?, quantidadeEstoque = ? WHERE idItem = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, it.getNome());
            ps.setString(2, it.getUnidadeMedida());
            ps.setInt(3, it.getQuantidadeEstoque());
            ps.setInt(4, it.getIdItem());
            ps.executeUpdate();
        }
    }

    // RNF12: item usado em alguma manutencao nao pode ser excluido
    public boolean possuiVinculo(int idItem) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_ItemManutencao_Material WHERE idItem = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idItem);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void remover(int idItem) throws SQLException {
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM TB_Item WHERE idItem = ?")) {
            ps.setInt(1, idItem);
            ps.executeUpdate();
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
