package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.ItemManutencao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemManutencaoDAO {

    public void inserir(ItemManutencao item) throws SQLException {
        String sql = "INSERT INTO TB_ItemManutencao_Filtro (idItemFiltro, idManutencao, especificacao, volumeOleo, idFiltro) "
                   + "VALUES (?, ?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, item.getIdItem());
            ps.setInt(2, item.getManutencao().getIdManutencao());
            ps.setString(3, item.getEspecificacao());
            ps.setObject(4, item.getVolumeOleo());
            ps.setInt(5, item.getFiltro().getIdFiltro());
            ps.executeUpdate();
        }
    }
}
