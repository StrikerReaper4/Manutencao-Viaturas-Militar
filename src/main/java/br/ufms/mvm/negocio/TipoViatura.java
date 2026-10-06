package br.ufms.mvm.negocio;

public class TipoViatura {

    private int idTipoViatura;
    private String nomeTipo;

    public TipoViatura(int idTipoViatura, String nomeTipo) {
        this.idTipoViatura = idTipoViatura;
        this.nomeTipo = nomeTipo;
    }

    public int getIdTipoViatura() {
        return idTipoViatura;
    }

    public String getNomeTipo() {
        return nomeTipo;
    }
}
