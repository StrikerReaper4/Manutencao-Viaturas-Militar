package br.ufms.mvm.modelo;

// Uma linha do relatorio mensal (RF14). Campos que a manutencao ainda nao tem
// (encerramento, situacao final) ficam null e saem em branco no relatorio.
public class LinhaRelatorioMensal {

    private final int idManutencao;
    private final String eb;
    private final String tipoViatura;
    private final String servico;
    private final String pecas;
    private final long dataInicio;
    private final Long dataEncerramento;
    private final String situacaoFinal;

    public LinhaRelatorioMensal(int idManutencao, String eb, String tipoViatura, String servico, String pecas,
                                long dataInicio, Long dataEncerramento, String situacaoFinal) {
        this.idManutencao = idManutencao;
        this.eb = eb;
        this.tipoViatura = tipoViatura;
        this.servico = servico;
        this.pecas = pecas;
        this.dataInicio = dataInicio;
        this.dataEncerramento = dataEncerramento;
        this.situacaoFinal = situacaoFinal;
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

    public String getServico() {
        return servico;
    }

    public String getPecas() {
        return pecas;
    }

    public long getDataInicio() {
        return dataInicio;
    }

    public Long getDataEncerramento() {
        return dataEncerramento;
    }

    public String getSituacaoFinal() {
        return situacaoFinal;
    }
}
