package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.Filtros;
import br.ufms.mvm.modelo.ItemManutencao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // se o filtro nao tinha sido previsto na abertura, entra como item novo
    public void atualizar(ItemManutencao item) throws SQLException {
        String sql = "UPDATE TB_ItemManutencao_Filtro SET especificacao = ?, volumeOleo = ? "
                   + "WHERE idItemFiltro = ? AND idManutencao = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, item.getEspecificacao());
            ps.setObject(2, item.getVolumeOleo());
            ps.setInt(3, item.getIdItem());
            ps.setInt(4, item.getManutencao().getIdManutencao());
            if (ps.executeUpdate() == 0) {
                inserir(item);
            }
        }
    }

    public void removerPorManutencao(int id) throws SQLException {
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM TB_ItemManutencao_Filtro WHERE idManutencao = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<ItemManutencao> listarPorManutencao(int id) throws SQLException {
        List<ItemManutencao> lista = new ArrayList<>();
        String sql = "SELECT i.idItemFiltro, i.especificacao AS espItem, i.volumeOleo, f.idFiltro, f.tipo, f.especificacao "
                   + "FROM TB_ItemManutencao_Filtro i JOIN TB_Filtro f ON f.idFiltro = i.idFiltro "
                   + "WHERE i.idManutencao = ? ORDER BY i.idItemFiltro";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemManutencao imf = new ItemManutencao(
                            new Filtros(rs.getInt("idFiltro"), rs.getString("tipo"), rs.getString("especificacao")));
                    imf.setIdItem(rs.getInt("idItemFiltro"));
                    double lido = rs.getDouble("volumeOleo");
                    Double volume = rs.wasNull() ? null : lido;
                    imf.registrarSubstituicao(rs.getString("espItem"), volume);
                    lista.add(imf);
                }
            }
        }
        return lista;
    }
}
