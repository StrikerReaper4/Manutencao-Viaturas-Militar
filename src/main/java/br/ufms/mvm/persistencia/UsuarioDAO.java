package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.Administrador;
import br.ufms.mvm.modelo.Mecanico;
import br.ufms.mvm.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Operador (Administrador) e Mecanico ficam na mesma tabela, separados pelo tipoUsuario
public class UsuarioDAO {

    public static final String OPERADOR = "OPERADOR";
    public static final String MECANICO = "MECANICO";

    public Usuario buscarPorLogin(String login) throws SQLException {
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Usuario WHERE login = ?")) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public Usuario buscarPorID(int id) throws SQLException {
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Usuario WHERE idUsuario = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM TB_Usuario ORDER BY nome");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public int inserir(Usuario u) throws SQLException {
        String sql = "INSERT INTO TB_Usuario (login, senha, nome, graduacao, tipoUsuario, ativo) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getLogin());
            ps.setString(2, u.getSenha());
            ps.setString(3, u.getNome());
            ps.setString(4, u.getGraduacao());
            ps.setString(5, tipo(u));
            ps.setInt(6, u.isAtivo() ? 1 : 0);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement("SELECT last_insert_rowid()");
             ResultSet rs = ps.executeQuery()) {
            return rs.getInt(1);
        }
    }

    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE TB_Usuario SET login = ?, senha = ?, nome = ?, graduacao = ?, tipoUsuario = ? WHERE idUsuario = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getLogin());
            ps.setString(2, u.getSenha());
            ps.setString(3, u.getNome());
            ps.setString(4, u.getGraduacao());
            ps.setString(5, tipo(u));
            ps.setInt(6, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    public static String tipo(Usuario u) {
        return u instanceof Administrador ? OPERADOR : MECANICO;
    }

    private Usuario montar(ResultSet rs) throws SQLException {
        int id = rs.getInt("idUsuario");
        String login = rs.getString("login");
        String senha = rs.getString("senha");
        String nome = rs.getString("nome");
        String graduacao = rs.getString("graduacao");
        boolean ativo = rs.getInt("ativo") == 1;
        if (OPERADOR.equals(rs.getString("tipoUsuario"))) {
            return new Administrador(id, login, senha, nome, graduacao, ativo);
        }
        return new Mecanico(id, login, senha, nome, graduacao, ativo);
    }
}
