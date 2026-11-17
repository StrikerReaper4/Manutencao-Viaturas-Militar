package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.RegistroSituacao;
import br.ufms.mvm.modelo.RegistroSituacao.Tipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// RF04: liga/desliga a coluna "ativo" das tabelas e grava o historico (data + usuario)
public class SituacaoRegistroDAO {

    // ultima alteracao registrada no historico pra cada registro
    private static final String ULTIMA =
            "LEFT JOIN (SELECT h.chave, h.data, u.graduacao || ' ' || u.nome AS quem FROM TB_HistoricoSituacao h "
          + "JOIN TB_Usuario u ON u.idUsuario = h.idUsuario WHERE h.tipoRegistro = ? AND h.idHistorico = "
          + "(SELECT MAX(h2.idHistorico) FROM TB_HistoricoSituacao h2 WHERE h2.tipoRegistro = h.tipoRegistro "
          + "AND h2.chave = h.chave)) ult ON ult.chave = ";

    public List<RegistroSituacao> listar(Tipo tipo) throws SQLException {
        String sql;
        switch (tipo) {
            case VIATURA:
                sql = "SELECT v.EB AS chave, v.EB || ' - ' || IFNULL(m.nome, '') AS descricao, v.ativo, ult.data, ult.quem "
                    + "FROM TB_Viatura v LEFT JOIN TB_Modelo m ON m.idModelo = v.idModelo "
                    + ULTIMA + "v.EB ORDER BY v.EB";
                break;
            case ITEM:
                sql = "SELECT CAST(i.idItem AS TEXT) AS chave, i.nome || ' (' || i.unidadeMedida || ')' AS descricao, "
                    + "i.ativo, ult.data, ult.quem FROM TB_Item i "
                    + ULTIMA + "CAST(i.idItem AS TEXT) ORDER BY i.nome";
                break;
            default:
                sql = "SELECT CAST(u.idUsuario AS TEXT) AS chave, u.graduacao || ' ' || u.nome || ' - ' || "
                    + "CASE u.tipoUsuario WHEN 'OPERADOR' THEN 'Operador' ELSE 'Mecânico' END AS descricao, "
                    + "u.ativo, ult.data, ult.quem FROM TB_Usuario u "
                    + ULTIMA + "CAST(u.idUsuario AS TEXT) ORDER BY u.nome";
        }
        List<RegistroSituacao> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long data = rs.getLong("data");
                    Long quando = rs.wasNull() ? null : data;
                    lista.add(new RegistroSituacao(tipo, rs.getString("chave"), rs.getString("descricao"),
                            rs.getInt("ativo") == 1, quando, rs.getString("quem")));
                }
            }
        }
        return lista;
    }

    public void alterarSituacao(Tipo tipo, String chave, boolean ativo, long data, int idUsuario) throws SQLException {
        String sql;
        switch (tipo) {
            case VIATURA:
                sql = "UPDATE TB_Viatura SET ativo = ? WHERE EB = ?";
                break;
            case ITEM:
                sql = "UPDATE TB_Item SET ativo = ? WHERE CAST(idItem AS TEXT) = ?";
                break;
            default:
                sql = "UPDATE TB_Usuario SET ativo = ? WHERE CAST(idUsuario AS TEXT) = ?";
        }
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ativo ? 1 : 0);
            ps.setString(2, chave);
            ps.executeUpdate();
        }
        String hist = "INSERT INTO TB_HistoricoSituacao (tipoRegistro, chave, ativo, data, idUsuario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(hist)) {
            ps.setString(1, tipo.name());
            ps.setString(2, chave);
            ps.setInt(3, ativo ? 1 : 0);
            ps.setLong(4, data);
            ps.setInt(5, idUsuario);
            ps.executeUpdate();
        }
    }
}
