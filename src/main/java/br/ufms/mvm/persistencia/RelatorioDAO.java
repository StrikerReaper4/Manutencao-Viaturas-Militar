package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.ItemUtilizado;
import br.ufms.mvm.modelo.LinhaRelatorioMensal;
import br.ufms.mvm.modelo.Manutencao;
import br.ufms.mvm.modelo.Pane;
import br.ufms.mvm.modelo.ViaturaAguardandoPecas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Consulta e relatorios da iteracao 5 (RF12, RF13 e RF14). So SELECT (RNF07).
// Os relatorios mostram tambem registros inativos, porque sao historico.
public class RelatorioDAO {

    // ultima manutencao encerrada da viatura: e o que faltou NELA que ainda esta pendente
    private static final String ULTIMA_ENCERRADA =
            "m.idManutencao = (SELECT m2.idManutencao FROM TB_Manutencao m2 WHERE m2.EB = m.EB AND m2.situacao = ? "
          + "ORDER BY m2.dataEncerramento DESC, m2.idManutencao DESC LIMIT 1)";
    private static final String VIATURA =
            "JOIN TB_Viatura v ON v.EB = m.EB LEFT JOIN TB_TipoViatura t ON t.idTipoViatura = v.idTipoViatura ";

    // RF12: junta tres fontes do que esta faltando pra cada viatura ativa:
    //  P = pecas indicadas em panes ainda nao atendidas,
    //  P = materiais que faltaram na ultima manutencao encerrada,
    //  S = servico que ficou faltando nessa mesma manutencao.
    public List<ViaturaAguardandoPecas> listarAguardandoPecas() throws SQLException {
        String sql =
                "SELECT m.EB, t.descricao AS tipo, 'P' AS cat, pi.quantidade || ' ' || i.unidadeMedida || ' - ' || i.nome AS texto "
              + "FROM TB_Pane_Item pi JOIN TB_Pane m ON m.idPane = pi.idPane JOIN TB_Item i ON i.idItem = pi.idItem "
              + VIATURA + "WHERE m.situacao <> ? AND v.ativo = 1 "
              + "UNION ALL "
              + "SELECT m.EB, t.descricao, 'P', mm.quantidade || ' ' || i.unidadeMedida || ' - ' || i.nome "
              + "FROM TB_ItemManutencao_Material mm JOIN TB_Manutencao m ON m.idManutencao = mm.idManutencao "
              + "JOIN TB_Item i ON i.idItem = mm.idItem " + VIATURA
              + "WHERE mm.faltante = 1 AND v.ativo = 1 AND " + ULTIMA_ENCERRADA + " "
              + "UNION ALL "
              + "SELECT m.EB, t.descricao, 'S', s.descricao "
              + "FROM TB_ItemManutencao_Servico s JOIN TB_Manutencao m ON m.idManutencao = s.idManutencao " + VIATURA
              + "WHERE s.faltante = 1 AND v.ativo = 1 AND " + ULTIMA_ENCERRADA + " "
              + "ORDER BY 1";
        Map<String, ViaturaAguardandoPecas> porViatura = new LinkedHashMap<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, Pane.ATENDIDA);
            ps.setString(2, Manutencao.ENCERRADA);
            ps.setString(3, Manutencao.ENCERRADA);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String eb = rs.getString(1);
                    ViaturaAguardandoPecas linha = porViatura.computeIfAbsent(eb,
                            k -> new ViaturaAguardandoPecas(k, lerTipo(rs)));
                    if ("P".equals(rs.getString(3))) {
                        linha.adicionarPeca(rs.getString(4));
                    } else {
                        linha.adicionarServico(rs.getString(4));
                    }
                }
            }
        }
        return new ArrayList<>(porViatura.values());
    }

    private static String lerTipo(ResultSet rs) {
        try {
            return rs.getString(2);
        } catch (SQLException e) {
            return "";
        }
    }

    // RF13: soma do que foi usado (faltante = 0) em manutencoes encerradas no periodo
    public List<ItemUtilizado> listarItensUtilizados(long inicio, long fim, String nomeItem) throws SQLException {
        String sql = "SELECT m.EB, t.descricao AS tipo, i.nome, i.unidadeMedida, SUM(mm.quantidade) AS total "
                   + "FROM TB_ItemManutencao_Material mm JOIN TB_Manutencao m ON m.idManutencao = mm.idManutencao "
                   + "JOIN TB_Item i ON i.idItem = mm.idItem " + VIATURA
                   + "WHERE mm.faltante = 0 AND m.situacao = ? AND m.dataEncerramento BETWEEN ? AND ? "
                   + "GROUP BY m.EB, i.idItem ORDER BY m.EB, i.nome";
        // o filtro por nome fica no Java: o LOWER do SQLite nao trata acento ("Óleo" x "óleo")
        String busca = normalizar(nomeItem);
        List<ItemUtilizado> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, Manutencao.ENCERRADA);
            ps.setLong(2, inicio);
            ps.setLong(3, fim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (!normalizar(rs.getString("nome")).contains(busca)) {
                        continue;
                    }
                    lista.add(new ItemUtilizado(rs.getString("EB"), rs.getString("tipo"), rs.getString("nome"),
                            rs.getString("unidadeMedida"), rs.getInt("total")));
                }
            }
        }
        return lista;
    }

    // minusculo e sem acento, pra comparar nomes
    private static String normalizar(String s) {
        if (s == null) {
            return "";
        }
        return java.text.Normalizer.normalize(s.trim().toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    // RF14: manutencoes que comecaram ou terminaram no mes
    public List<LinhaRelatorioMensal> listarMensal(long inicio, long fim) throws SQLException {
        String sql = "SELECT m.idManutencao, m.EB, t.descricao AS tipo, m.tipo AS tipoManutencao, p.descricao AS pane, "
                   + "m.dataInicio, m.dataEncerramento, m.situacaoFinal, "
                   + "(SELECT group_concat(mm.quantidade || ' ' || i.unidadeMedida || ' ' || i.nome, ', ') "
                   + " FROM TB_ItemManutencao_Material mm JOIN TB_Item i ON i.idItem = mm.idItem "
                   + " WHERE mm.idManutencao = m.idManutencao AND mm.faltante = 0) AS pecas, "
                   + "(SELECT group_concat(f.tipo, ', ') FROM TB_ItemManutencao_Filtro imf "
                   + " JOIN TB_Filtro f ON f.idFiltro = imf.idFiltro WHERE imf.idManutencao = m.idManutencao) AS filtros "
                   + "FROM TB_Manutencao m " + VIATURA
                   + "LEFT JOIN TB_Pane p ON p.idPane = m.idPane "
                   + "WHERE m.dataInicio BETWEEN ? AND ? OR m.dataEncerramento BETWEEN ? AND ? "
                   + "ORDER BY m.dataInicio, m.idManutencao";
        List<LinhaRelatorioMensal> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, inicio);
            ps.setLong(2, fim);
            ps.setLong(3, inicio);
            ps.setLong(4, fim);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String servico;
                    if (Manutencao.PREVENTIVA.equals(rs.getString("tipoManutencao"))) {
                        String filtros = rs.getString("filtros");
                        servico = "Preventiva" + (filtros != null ? ": troca de " + filtros : "");
                    } else {
                        String pane = rs.getString("pane");
                        servico = "Corretiva" + (pane != null ? ": " + pane : "");
                    }
                    long enc = rs.getLong("dataEncerramento");
                    Long dataEncerramento = rs.wasNull() ? null : enc;
                    lista.add(new LinhaRelatorioMensal(rs.getInt("idManutencao"), rs.getString("EB"),
                            rs.getString("tipo"), servico, rs.getString("pecas"), rs.getLong("dataInicio"),
                            dataEncerramento, rs.getString("situacaoFinal")));
                }
            }
        }
        return lista;
    }
}
