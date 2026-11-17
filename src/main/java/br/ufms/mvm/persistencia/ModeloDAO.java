package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModeloDAO {

    public List<Modelo> listar() throws SQLException {
        List<Modelo> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Modelo ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Modelo(rs.getInt("idModelo"), rs.getString("nome")));
            }
        }
        return lista;
    }
}
