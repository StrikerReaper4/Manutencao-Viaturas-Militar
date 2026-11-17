package br.ufms.mvm.modelo;

// Uma linha da consulta de manutencoes em andamento (RF10). So leitura.
public class ManutencaoEmAndamento {

    private final int idManutencao;
    private final String eb;
    private final String tipoViatura;
    private final String mecanicos;
    private final String servico;
    private final long dataInicio;

    public ManutencaoEmAndamento(int idManutencao, String eb, String tipoViatura, String mecanicos,
                                 String servico, long dataInicio) {
        this.idManutencao = idManutencao;
        this.eb = eb;
        this.tipoViatura = tipoViatura;
        this.mecanicos = mecanicos;
        this.servico = servico;
        this.dataInicio = dataInicio;
    }

    public int getIdManutencao() {
        return idManutencao;
    }

    public String getEb() {
        return eb;
    }

    public String getTipoViatura() {
        return tipoViatura;
    }

    public String getMecanicos() {
        return mecanicos;
    }

    public String getServico() {
        return servico;
    }

    public long getDataInicio() {
        return dataInicio;
    }
}
