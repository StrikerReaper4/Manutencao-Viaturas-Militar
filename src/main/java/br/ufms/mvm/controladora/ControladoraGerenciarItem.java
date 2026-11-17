package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.Item;
import br.ufms.mvm.persistencia.ItemDAO;

import java.sql.SQLException;
import java.util.List;

// Caso de uso Gerenciar item (RF03), Operador e Mecanico
public class ControladoraGerenciarItem {

    private final ItemDAO itemDAO = new ItemDAO();

    public ControladoraGerenciarItem() {
        if (Sessao.getUsuarioLogado() == null) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    public List<Item> listarItens() throws SQLException {
        return itemDAO.listarTodos();
    }

    // idItem null inclui, senao altera
    public void gravarItem(Integer idItem, String nome, String unidade, String quantidade) throws SQLException {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Nome do item"));
        }
        if (unidade == null || unidade.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Unidade de medida"));
        }
        if (quantidade == null || quantidade.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Quantidade"));
        }
        int qtd = ControladoraGerenciarViatura.numero(quantidade, "Quantidade");

        if (idItem == null) {
            itemDAO.inserir(new Item(0, nome.trim(), unidade.trim(), qtd, true));
        } else {
            Item existente = itemDAO.buscarPorID(idItem);
            if (existente == null) {
                throw new IllegalArgumentException(MensagensCadastro.SELECIONE);
            }
            itemDAO.atualizar(new Item(idItem, nome.trim(), unidade.trim(), qtd, existente.isAtivo()));
        }
    }

    // RNF12 e RNF13
    public void removerItem(int idItem) throws SQLException {
        if (itemDAO.possuiVinculo(idItem)) {
            throw new IllegalArgumentException(MensagensCadastro.ITEM_COM_VINCULO);
        }
        itemDAO.remover(idItem);
    }
}
