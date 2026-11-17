package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.RegistroSituacao;
import br.ufms.mvm.modelo.RegistroSituacao.Tipo;
import br.ufms.mvm.persistencia.Conexao;
import br.ufms.mvm.persistencia.ManutencaoDAO;
import br.ufms.mvm.persistencia.SituacaoRegistroDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

// Caso de uso Atualizar situacao de registro (RF04), privativo do Operador.
// Inativar nao apaga: o registro some das listas de novos lancamentos (elas so mostram ativos)
// mas continua nas manutencoes e relatorios antigos.
public class ControladoraAtualizarSituacao {

    private final SituacaoRegistroDAO situacaoDAO = new SituacaoRegistroDAO();
    private final ManutencaoDAO manutencaoDAO = new ManutencaoDAO();

    public ControladoraAtualizarSituacao() {
        if (!Sessao.isOperador()) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    public List<RegistroSituacao> listarRegistros(Tipo tipo) throws SQLException {
        return situacaoDAO.listar(tipo);
    }

    // inverte a situacao do registro e grava data + usuario responsavel; devolve a nova situacao
    public boolean alterarSituacao(RegistroSituacao r, long dataAtual) throws SQLException {
        boolean novoAtivo = !r.isAtivo();
        int eu = Sessao.getUsuarioLogado().getIdUsuario();
        if (!novoAtivo) {
            if (r.getTipo() == Tipo.USUARIO && r.getChave().equals(String.valueOf(eu))) {
                throw new IllegalArgumentException(MensagensEdicao.PROPRIO_USUARIO);
            }
            if (r.getTipo() == Tipo.VIATURA && manutencaoDAO.buscarEmAndamento(r.getChave()) != null) {
                throw new IllegalArgumentException(MensagensEdicao.VIATURA_EM_MANUTENCAO);
            }
        }
        Connection con = Conexao.getConexao();
        con.setAutoCommit(false);
        try {
            situacaoDAO.alterarSituacao(r.getTipo(), r.getChave(), novoAtivo, dataAtual, eu);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
        return novoAtivo;
    }
}
