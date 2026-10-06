package br.ufms.mvm.negocio;

import java.util.ArrayList;
import java.util.List;

public class Manutencao {

    public static final String PREVENTIVA = "Preventiva";
    public static final String CORRETIVA = "Corretiva";
    public static final String EM_ANDAMENTO = "Em andamento";

    private int idManutencao;
    private String tipo;
    private long dataInicio;
    private Long dataEncerramento;
    private int odometroEntrada;
    private Integer odometroSaida;
    private String situacao;
    private String situacaoFinal;

    private Viatura viatura;
    private Pane pane;
    private List<Mecanico> mecanicos = new ArrayList<>();
    private List<ItemManutencao> itens = new ArrayList<>();

    // mensagem 1.3 <<create>> do diagrama de comunicacao
    public Manutencao(String tipo, long dataAtual, int odometro) {
        this.tipo = tipo;
        this.dataInicio = dataAtual;
        this.odometroEntrada = odometro;
    }

    public void associarViatura(Viatura v) {
        this.viatura = v;
    }

    public void associarPane(Pane p) {
        this.pane = p;
    }

    public void adicionarMecanico(Mecanico me) {
        mecanicos.add(me);
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    // 1.13 -> 1.13.1: um ItemManutencao para cada filtro previsto
    public List<ItemManutencao> preverFiltros(List<Filtros> filtros) {
        for (Filtros f : filtros) {
            ItemManutencao imf = new ItemManutencao(f);
            imf.setIdItem(itens.size() + 1);
            imf.setManutencao(this);
            itens.add(imf);
        }
        return itens;
    }

    public int getIdManutencao() {
        return idManutencao;
    }

    public void setIdManutencao(int idManutencao) {
        this.idManutencao = idManutencao;
    }

    public String getTipo() {
        return tipo;
    }

    public long getDataInicio() {
        return dataInicio;
    }

    public Long getDataEncerramento() {
        return dataEncerramento;
    }

    public int getOdometroEntrada() {
        return odometroEntrada;
    }

    public Integer getOdometroSaida() {
        return odometroSaida;
    }

    public String getSituacao() {
        return situacao;
    }

    public String getSituacaoFinal() {
        return situacaoFinal;
    }

    public Viatura getViatura() {
        return viatura;
    }

    public Pane getPane() {
        return pane;
    }

    public List<Mecanico> getMecanicos() {
        return mecanicos;
    }

    public List<ItemManutencao> getItens() {
        return itens;
    }
}
