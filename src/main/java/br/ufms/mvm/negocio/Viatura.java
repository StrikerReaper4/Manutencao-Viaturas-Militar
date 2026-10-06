package br.ufms.mvm.negocio;

public class Viatura {

    public static final String DISPONIVEL = "Disponível";
    public static final String INDISPONIVEL = "Indisponível";
    public static final String DISPONIVEL_RESTRICAO = "Disponível com restrição";

    private String eb;
    private String placa;
    private int kmManutencaoPreventiva;
    private int odometroAtual;
    private String situacao;
    private boolean ativo;
    private Modelo modelo;
    private TipoViatura tipoViatura;

    public Viatura(String eb, String placa, int kmManutencaoPreventiva, int odometroAtual,
                   String situacao, boolean ativo, Modelo modelo, TipoViatura tipoViatura) {
        this.eb = eb;
        this.placa = placa;
        this.kmManutencaoPreventiva = kmManutencaoPreventiva;
        this.odometroAtual = odometroAtual;
        this.situacao = situacao;
        this.ativo = ativo;
        this.modelo = modelo;
        this.tipoViatura = tipoViatura;
    }

    public int getOdometro() {
        return odometroAtual;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getEb() {
        return eb;
    }

    public String getPlaca() {
        return placa;
    }

    public int getKmManutencaoPreventiva() {
        return kmManutencaoPreventiva;
    }

    public String getSituacao() {
        return situacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public TipoViatura getTipoViatura() {
        return tipoViatura;
    }

    @Override
    public String toString() {
        // aparece assim no combo da tela
        String nomeModelo = modelo != null ? modelo.getNomeModelo() : "";
        return eb + " - " + nomeModelo;
    }
}
