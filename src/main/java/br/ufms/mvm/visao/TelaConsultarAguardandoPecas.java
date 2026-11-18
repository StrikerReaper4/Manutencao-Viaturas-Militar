package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraConsultarAguardandoPecas;
import br.ufms.mvm.modelo.ViaturaAguardandoPecas;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Tela do caso de uso Consultar viaturas aguardando pecas (RF12)
public class TelaConsultarAguardandoPecas extends TelaConsulta {

    private final ControladoraConsultarAguardandoPecas controladora = new ControladoraConsultarAguardandoPecas();

    public TelaConsultarAguardandoPecas() {
        super("Consultar viaturas aguardando peças",
                "Peças indicadas nas panes ainda não atendidas e o que faltou na última manutenção encerrada.",
                new String[]{"EB da viatura", "Tipo de viatura", "Peças que estão faltando", "Serviço que está faltando"});
        iniciar();
    }

    @Override
    protected List<Object[]> buscarLinhas() throws SQLException {
        List<Object[]> linhas = new ArrayList<>();
        for (ViaturaAguardandoPecas v : controladora.consultar()) {
            linhas.add(new Object[]{v.getEb(), v.getTipoViatura(), String.join("; ", v.getPecas()),
                    String.join("; ", v.getServicos())});
        }
        return linhas;
    }

    @Override
    protected String resumo(int total) {
        return total == 0 ? "Nenhuma viatura aguardando peças." : total + " viatura(s) aguardando peças ou serviço.";
    }
}
