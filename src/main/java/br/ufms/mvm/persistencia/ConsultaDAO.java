package br.ufms.mvm.persistencia;

import br.ufms.mvm.modelo.ControlePreventiva;
import br.ufms.mvm.modelo.Manutencao;
import br.ufms.mvm.modelo.ManutencaoEmAndamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Consultas da iteracao 4 (RF10 e RF11). Sao so SELECT, nada aqui altera dados (RNF07).
public class ConsultaDAO {

    public List<ManutencaoEmAndamento> listarEmAndamento() throws SQLException {
        String sql = "SELECT m.idManutencao, m.EB, t.descricao AS tipoViatura, m.tipo, p.descricao AS pane, m.dataInicio, "
                   + "(SELECT group_concat(u.graduacao || ' ' || u.nome, ', ') FROM TB_Manutencao_Mecanico mm "
                   + " JOIN TB_Usuario u ON u.idUsuario = mm.idUsuario WHERE mm.idManutencao = m.idManutencao) AS mecanicos "
                   + "FROM TB_Manutencao m JOIN TB_Viatura v ON v.EB = m.EB "
                   + "LEFT JOIN TB_TipoViatura t ON t.idTipoViatura = v.idTipoViatura "
                   + "LEFT JOIN TB_Pane p ON p.idPane = m.idPane "
                   + "WHERE m.situacao = ? ORDER BY m.dataInicio";
        List<ManutencaoEmAndamento> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, Manutencao.EM_ANDAMENTO);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // servico em execucao: na preventiva eh a troca de filtros, na corretiva eh a pane
                    String servico = Manutencao.PREVENTIVA.equals(rs.getString("tipo"))
                            ? "Preventiva - troca de filtros"
                            : "Corretiva - " + (rs.getString("pane") != null ? rs.getString("pane") : "sem pane");
                    lista.add(new ManutencaoEmAndamento(rs.getInt("idManutencao"), rs.getString("EB"),
                            rs.getString("tipoViatura"), rs.getString("mecanicos"), servico, rs.getLong("dataInicio")));
                }
            }
        }
        return lista;
    }

    // dados de cada viatura ativa + a ultima preventiva encerrada dela
    public List<ControlePreventiva> listarControlePreventiva() throws SQLException {
        String ultima = "FROM TB_Manutencao m WHERE m.EB = v.EB AND m.tipo = ? AND m.situacao = ? "
                      + "ORDER BY m.dataEncerramento DESC LIMIT 1";
        String sql = "SELECT v.EB, t.descricao AS tipoViatura, v.odometroAtual, v.kmManutencaoPreventiva, "
                   + "(SELECT m.dataEncerramento " + ultima + ") AS dataUltima, "
                   + "(SELECT m.odometroSaida " + ultima + ") AS kmUltima "
                   + "FROM TB_Viatura v LEFT JOIN TB_TipoViatura t ON t.idTipoViatura = v.idTipoViatura "
                   + "WHERE v.ativo = 1 ORDER BY v.EB";
        List<ControlePreventiva> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, Manutencao.PREVENTIVA);
            ps.setString(2, Manutencao.ENCERRADA);
            ps.setString(3, Manutencao.PREVENTIVA);
            ps.setString(4, Manutencao.ENCERRADA);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long data = rs.getLong("dataUltima");
                    Long dataUltima = rs.wasNull() ? null : data;
                    int km = rs.getInt("kmUltima");
                    Integer kmUltima = rs.wasNull() ? null : km;
                    lista.add(new ControlePreventiva(rs.getString("EB"), rs.getString("tipoViatura"),
                            rs.getInt("odometroAtual"), rs.getInt("kmManutencaoPreventiva"), dataUltima, kmUltima));
                }
            }
        }
        return lista;
    }
}
