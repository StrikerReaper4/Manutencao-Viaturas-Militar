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
        boolean novo = !tabelaExiste("TB_Viatura");
        boolean semIteracao2 = !tabelaExiste("TB_Item");
        boolean semIteracao4 = !tabelaExiste("TB_HistoricoSituacao");
        boolean semIteracao5 = !tabelaExiste("TB_Pane_Item");
        executarScript("/banco/schema.sql");
        if (novo) {
            executarScript("/banco/dados.sql");
        }
        // assim quem ja tinha o banco da iteracao 1 recebe os dados novos
        if (semIteracao2) {
            executarScript("/banco/dados_iteracao2.sql");
        }
        // iteracao 4: quem abriu a manutencao (RF07). ALTER TABLE nao tem IF NOT EXISTS no SQLite
        if (!colunaExiste("TB_Manutencao", "idUsuarioAbertura")) {
            try (Statement st = conexao.createStatement()) {
                st.execute("ALTER TABLE TB_Manutencao ADD COLUMN idUsuarioAbertura INTEGER REFERENCES TB_Usuario(idUsuario)");
            }
        }
        if (semIteracao4) {
            executarScript("/banco/dados_iteracao4.sql");
        }
        if (semIteracao5) {
            executarScript("/banco/dados_iteracao5.sql");
        }
    }

    private static boolean colunaExiste(String tabela, String coluna) throws SQLException {
        DatabaseMetaData meta = conexao.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tabela, coluna)) {
            return rs.next();
        }
    }

    private static boolean tabelaExiste(String nome) throws SQLException {
        DatabaseMetaData meta = conexao.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, nome, null)) {
            return rs.next();
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
