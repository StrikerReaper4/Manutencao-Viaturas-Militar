package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.TipoViatura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoViaturaDAO {

    public List<TipoViatura> listar() throws SQLException {
        List<TipoViatura> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_TipoViatura ORDER BY descricao");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new TipoViatura(rs.getInt("idTipoViatura"), rs.getString("descricao")));
            }
        }
        return lista;
    }
}
