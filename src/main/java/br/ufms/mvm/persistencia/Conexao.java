package br.ufms.mvm.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Uma conexao so pra aplicacao inteira, assim todos os DAOs ficam na mesma transacao
public class Conexao {

    private static final String URL = "jdbc:sqlite:mvm.db";
    private static Connection conexao;

    private Conexao() {
    }

    public static Connection getConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conexao = DriverManager.getConnection(URL);
            try (Statement st = conexao.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
            criarBancoSePreciso();
        }
        return conexao;
    }

    private static void criarBancoSePreciso() throws SQLException {
        boolean novo;
        DatabaseMetaData meta = conexao.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "TB_Viatura", null)) {
            novo = !rs.next();
        }
        executarScript("/banco/schema.sql");
        if (novo) {
            executarScript("/banco/dados.sql");
        }
    }

    private static void executarScript(String caminho) throws SQLException {
        String sql;
        try (InputStream in = Conexao.class.getResourceAsStream(caminho)) {
            if (in == null) {
                throw new SQLException("Script nao encontrado: " + caminho);
            }
            sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SQLException("Erro ao ler " + caminho, e);
        }

        try (Statement st = conexao.createStatement()) {
            for (String comando : sql.split(";")) {
                String limpo = removerComentarios(comando).trim();
                if (!limpo.isEmpty()) {
                    st.execute(limpo);
                }
            }
        }
    }

    private static String removerComentarios(String sql) {
        StringBuilder sb = new StringBuilder();
        for (String linha : sql.split("\n")) {
            if (!linha.trim().startsWith("--")) {
                sb.append(linha).append("\n");
            }
        }
        return sb.toString();
    }
}
