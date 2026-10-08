package br.ufms.mvm.modelo;

public class Pane {

    public static final String REGISTRADA = "Registrada";
    public static final String EM_ATENDIMENTO = "Em atendimento";

    private int idPane;
    private String descricao;
    private int prioridade;
    private Long dataLimite;
    private String missao;
    private String situacao;
    private String eb;

    public Pane(int idPane, String descricao, int prioridade, Long dataLimite,
                String missao, String situacao, String eb) {
        this.idPane = idPane;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.dataLimite = dataLimite;
        this.missao = missao;
        this.situacao = situacao;
        this.eb = eb;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public int getIdPane() {
        return idPane;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public Long getDataLimite() {
        return dataLimite;
    }

    public String getMissao() {
        return missao;
    }

    public String getSituacao() {
        return situacao;
    }

    public String getEb() {
        return eb;
    }

    @Override
    public String toString() {
        return "#" + idPane + " - " + descricao + " (prioridade " + prioridade + ")";
    }
}
