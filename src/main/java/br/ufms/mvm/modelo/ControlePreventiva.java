package br.ufms.mvm.modelo;

// Uma linha da consulta de manutencoes preventivas (RF11). So leitura.
// A regra de "precisa ou nao" fica na controladora; aqui so os dados lidos e o resultado.
public class ControlePreventiva {

    private final String eb;
    private final String tipoViatura;
    private final int odometroAtual;
    private final int kmLimite;
    private final Long dataUltima;
    private final Integer kmUltima;
    private boolean necessita;
    private String motivo;

    public ControlePreventiva(String eb, String tipoViatura, int odometroAtual, int kmLimite,
                              Long dataUltima, Integer kmUltima) {
        this.eb = eb;
        this.tipoViatura = tipoViatura;
        this.odometroAtual = odometroAtual;
        this.kmLimite = kmLimite;
        this.dataUltima = dataUltima;
        this.kmUltima = kmUltima;
    }

    public void sinalizar(boolean necessita, String motivo) {
        this.necessita = necessita;
        this.motivo = motivo;
    }

    // km rodados desde a ultima preventiva (null se nunca teve)
    public Integer getKmPercorridos() {
        return kmUltima == null ? null : odometroAtual - kmUltima;
    }

    public String getEb() {
        return eb;
    }

    public String getTipoViatura() {
        return tipoViatura;
    }

    public int getOdometroAtual() {
        return odometroAtual;
    }

    public int getKmLimite() {
        return kmLimite;
    }

    public Long getDataUltima() {
        return dataUltima;
    }

    public Integer getKmUltima() {
        return kmUltima;
    }

    public boolean isNecessita() {
        return necessita;
    }

    public String getMotivo() {
        return motivo;
    }
}
