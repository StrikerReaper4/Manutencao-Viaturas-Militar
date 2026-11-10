package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.ItemManutencao_Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemManutencaoMaterialDAO {

    public void inserir(ItemManutencao_Material imm) throws SQLException {
        String sql = "INSERT INTO TB_ItemManutencao_Material (idItemMaterial, idManutencao, quantidade, faltante, idItem) "
                   + "VALUES (?, ?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, imm.getIdItemMaterial());
            ps.setInt(2, imm.getManutencao().getIdManutencao());
            ps.setInt(3, imm.getQuantidade());
            ps.setInt(4, imm.isFaltante() ? 1 : 0);
            ps.setInt(5, imm.getItem().getIdItem());
            ps.executeUpdate();
        }
    }
}
