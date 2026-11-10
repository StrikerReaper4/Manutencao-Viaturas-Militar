package br.ufms.mvm.modelo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Manutencao {

    public static final String PREVENTIVA = "Preventiva";
    public static final String CORRETIVA = "Corretiva";
    public static final String EM_ANDAMENTO = "Em andamento";
    public static final String ENCERRADA = "Encerrada";

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
    private List<ItemManutencao_Material> materiais = new ArrayList<>();
    private ItemManutencao_Servico servicoFaltante;

    // chaves lidas do banco, usadas pra buscar a viatura e a pane
    private String eb;
    private Integer idPane;

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

    // 1.4 do diagrama de comunicacao do encerramento
    public void encerrar(long dataAtual, int odometroSaida, String tipo, String situacaoFinal) {
        this.dataEncerramento = dataAtual;
        this.odometroSaida = odometroSaida;
        this.tipo = tipo;
        this.situacaoFinal = situacaoFinal;
    }

    // 1.6 e 1.7 -> 1.6.1 e 1.7.1
    public void registrarMaterial(Item it, int quantidade, boolean faltante) {
        ItemManutencao_Material imm = new ItemManutencao_Material(it, quantidade, faltante);
        imm.setIdItemMaterial(materiais.size() + 1);
        imm.setManutencao(this);
        materiais.add(imm);
    }

    // 1.8 -> 1.8.1
    public void registrarServicoFaltante(String descricao) {
        ItemManutencao_Servico ims = new ItemManutencao_Servico(descricao, true);
        ims.setIdItemServico(1);
        ims.setManutencao(this);
        this.servicoFaltante = ims;
    }

    // 1.9 -> 1.9.1. Filtro que nao foi previsto na abertura entra como item novo
    public void registrarFiltros(List<Filtros> filtros) {
        for (Filtros f : filtros) {
            ItemManutencao imf = buscarItem(f.getIdFiltro());
            if (imf == null) {
                preverFiltros(List.of(f));
                imf = buscarItem(f.getIdFiltro());
            }
            imf.registrarSubstituicao(f.getEspecificacao(), f.getVolumeOleo());
        }
    }

    private ItemManutencao buscarItem(int idFiltro) {
        for (ItemManutencao imf : itens) {
            if (imf.getFiltro().getIdFiltro() == idFiltro) {
                return imf;
            }
        }
        return null;
    }

    public void carregarItem(ItemManutencao imf) {
        imf.setManutencao(this);
        itens.add(imf);
    }

    public void setChaves(String eb, Integer idPane) {
        this.eb = eb;
        this.idPane = idPane;
    }

    public String getEb() {
        return viatura != null ? viatura.getEb() : eb;
    }

    public Integer getIdPane() {
        return pane != null ? Integer.valueOf(pane.getIdPane()) : idPane;
    }

    public List<ItemManutencao_Material> getMateriais() {
        return materiais;
    }

    public ItemManutencao_Servico getServicoFaltante() {
        return servicoFaltante;
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

    @Override
    public String toString() {
        // aparece assim no combo da tela de encerramento
        String abertura = new SimpleDateFormat("dd/MM/yyyy").format(new Date(dataInicio));
        return "Nº " + idManutencao + " - EB " + getEb() + " - " + tipo + " - aberta em " + abertura;
    }
}
