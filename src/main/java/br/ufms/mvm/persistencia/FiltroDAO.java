package br.ufms.mvm.persistencia;

import br.ufms.mvm.negocio.Filtros;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FiltroDAO {

    public Filtros buscarPorID(int id) throws SQLException {
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Filtro WHERE idFiltro = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    // Por enquanto o banco nao liga filtro a viatura, entao todos os filtros
    // cadastrados sao sugeridos pra troca na preventiva
    public List<Filtros> listarPorViatura(String eb) throws SQLException {
        List<Filtros> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Filtro ORDER BY idFiltro");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    private Filtros montar(ResultSet rs) throws SQLException {
        return new Filtros(rs.getInt("idFiltro"), rs.getString("tipo"), rs.getString("especificacao"));
    }
}
