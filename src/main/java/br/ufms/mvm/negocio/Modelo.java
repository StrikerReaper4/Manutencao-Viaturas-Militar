package br.ufms.mvm.negocio;

public class Modelo {

    private int idModelo;
    private String nomeModelo;

    public Modelo(int idModelo, String nomeModelo) {
        this.idModelo = idModelo;
        this.nomeModelo = nomeModelo;
    }

    public int getIdModelo() {
        return idModelo;
    }

    public String getNomeModelo() {
        return nomeModelo;
    }
}
