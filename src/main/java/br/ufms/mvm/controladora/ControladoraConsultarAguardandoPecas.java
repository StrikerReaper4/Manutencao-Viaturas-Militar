package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.ViaturaAguardandoPecas;
import br.ufms.mvm.persistencia.RelatorioDAO;

import java.sql.SQLException;
import java.util.List;

// Caso de uso Consultar viaturas aguardando pecas (RF12), privativo do Operador.
// Mostra o que esta faltando: pecas indicadas nas panes ainda nao atendidas e os materiais
// e servicos que faltaram na ultima manutencao encerrada de cada viatura.
public class ControladoraConsultarAguardandoPecas {

    private final RelatorioDAO relatorioDAO = new RelatorioDAO();

    public ControladoraConsultarAguardandoPecas() {
        if (!Sessao.isOperador()) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    public List<ViaturaAguardandoPecas> consultar() throws SQLException {
        return relatorioDAO.listarAguardandoPecas();
    }
}
