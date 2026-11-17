package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraGerenciarItem;
import br.ufms.mvm.controladora.MensagensCadastro;
import br.ufms.mvm.modelo.Item;

import javax.swing.JTextField;
import java.sql.SQLException;
import java.util.List;

public class TelaGerenciarItem extends TelaCadastro<Item> {

    private final ControladoraGerenciarItem controladora = new ControladoraGerenciarItem();

    private final JTextField txtNome = new JTextField();
    private final JTextField txtUnidade = new JTextField();
    private final JTextField txtQuantidade = new JTextField();

    public TelaGerenciarItem() {
        super("Gerenciar item",
                "Inclusão, alteração e remoção de peças e materiais. Os campos assinalados com (*) são de preenchimento obrigatório.",
                "Dados do item",
                new String[]{"Código", "Nome do item", "Unidade de medida", "Quantidade", "Ativo"},
                true);
        Estilo.empilhar(formulario, Estilo.linha(
                Estilo.campo("Nome do item", true, txtNome),
                Estilo.campo("Unidade de medida (ex.: un, L, jogo)", true, txtUnidade),
                Estilo.campo("Quantidade", true, txtQuantidade)));
        iniciar();
    }

    @Override
    protected List<Item> listar() throws SQLException {
        return controladora.listarItens();
    }

    @Override
    protected Object[] linha(Item it) {
        return new Object[]{it.getIdItem(), it.getNome(), it.getUnidadeMedida(), it.getQuantidadeEstoque(),
                it.isAtivo() ? "Sim" : "Não"};
    }

    @Override
    protected void preencherFormulario(Item it) {
        txtNome.setText(it.getNome());
        txtUnidade.setText(it.getUnidadeMedida());
        txtQuantidade.setText(String.valueOf(it.getQuantidadeEstoque()));
    }

    @Override
    protected void limparFormulario() {
        txtNome.setText("");
        txtUnidade.setText("");
        txtQuantidade.setText("");
    }

    @Override
    protected String gravar() throws SQLException {
        controladora.gravarItem(selecionado == null ? null : selecionado.getIdItem(),
                txtNome.getText(), txtUnidade.getText(), txtQuantidade.getText());
        return MensagensCadastro.gravado("Item " + txtNome.getText().trim());
    }

    @Override
    protected String excluir(Item it) throws SQLException {
        controladora.removerItem(it.getIdItem());
        return MensagensCadastro.removido("Item " + it.getNome());
    }

    @Override
    protected String descrever(Item it) {
        return "o item " + it.getNome();
    }
}
