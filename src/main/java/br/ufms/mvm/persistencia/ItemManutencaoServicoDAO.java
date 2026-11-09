package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.ItemManutencao_Servico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemManutencaoServicoDAO {

    public void inserir(ItemManutencao_Servico ims) throws SQLException {
        String sql = "INSERT INTO TB_ItemManutencao_Servico (idItemServico, idManutencao, descricao, faltante) "
                   + "VALUES (?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ims.getIdItemServico());
            ps.setInt(2, ims.getManutencao().getIdManutencao());
            ps.setString(3, ims.getDescricao());
            ps.setInt(4, ims.isFaltante() ? 1 : 0);
            ps.executeUpdate();
        }
    }
}
