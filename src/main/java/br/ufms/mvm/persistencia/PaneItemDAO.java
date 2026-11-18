package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.ItemManutencao_Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// RF12: pecas necessarias indicadas no registro da pane (TB_Pane_Item)
public class PaneItemDAO {

    public void inserir(int idPane, ItemManutencao_Material peca) throws SQLException {
        String sql = "INSERT INTO TB_Pane_Item (idPane, idItem, quantidade) VALUES (?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPane);
            ps.setInt(2, peca.getItem().getIdItem());
            ps.setInt(3, peca.getQuantidade());
            ps.executeUpdate();
        }
    }
}
