package br.ufms.mvm.modelo;

// Uma linha da tela Atualizar situacao de registro (RF04): qualquer viatura, item ou usuario,
// com a situacao atual e quem fez a ultima alteracao
public class RegistroSituacao {

    public enum Tipo {
        VIATURA("Viatura"), ITEM("Item"), USUARIO("Usuário");

        private final String nome;

        Tipo(String nome) {
            this.nome = nome;
        }

        @Override
        public String toString() {
            return nome;
        }
    }

    private final Tipo tipo;
    private final String chave;
    private final String descricao;
    private final boolean ativo;
    private final Long dataUltimaAlteracao;
    private final String usuarioUltimaAlteracao;

    public RegistroSituacao(Tipo tipo, String chave, String descricao, boolean ativo,
                            Long dataUltimaAlteracao, String usuarioUltimaAlteracao) {
        this.tipo = tipo;
        this.chave = chave;
        this.descricao = descricao;
        this.ativo = ativo;
        this.dataUltimaAlteracao = dataUltimaAlteracao;
        this.usuarioUltimaAlteracao = usuarioUltimaAlteracao;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getChave() {
        return chave;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Long getDataUltimaAlteracao() {
        return dataUltimaAlteracao;
    }

    public String getUsuarioUltimaAlteracao() {
        return usuarioUltimaAlteracao;
    }

    @Override
    public String toString() {
        return tipo.toString().toLowerCase() + " " + descricao;
    }
}
