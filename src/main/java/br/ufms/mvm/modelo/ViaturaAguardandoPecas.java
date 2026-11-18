package br.ufms.mvm.modelo;

import java.util.ArrayList;
import java.util.List;

// Uma linha da consulta de viaturas aguardando pecas (RF12). So leitura.
// Junta numa linha so tudo o que falta pra viatura: pecas e servicos.
public class ViaturaAguardandoPecas {

    private final String eb;
    private final String tipoViatura;
    private final List<String> pecas = new ArrayList<>();
    private final List<String> servicos = new ArrayList<>();

    public ViaturaAguardandoPecas(String eb, String tipoViatura) {
        this.eb = eb;
        this.tipoViatura = tipoViatura;
    }

    public void adicionarPeca(String peca) {
        if (!pecas.contains(peca)) {
            pecas.add(peca);
        }
    }

    public void adicionarServico(String servico) {
        if (!servicos.contains(servico)) {
            servicos.add(servico);
        }
    }

    public String getEb() {
        return eb;
    }

    public String getTipoViatura() {
        return tipoViatura;
    }

    public List<String> getPecas() {
        return pecas;
    }

    public List<String> getServicos() {
        return servicos;
    }
}
