package com.uberpb.model;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoCompras {
    private Restaurante restaurante;
    private List<ItemCarrinho> itens;

    public CarrinhoCompras() {
        this.itens = new ArrayList<>();
    }

    public Restaurante getRestaurante() { return restaurante; }
    public List<ItemCarrinho> getItens() { return itens; }

    public void adicionarItem(Restaurante novoRestaurante, ItemCardapio item, int qtd) {
        // Se mudar de restaurante, limpa o carrinho anterior 
        if (this.restaurante != null && !this.restaurante.getEmail().equals(novoRestaurante.getEmail())) {
            this.itens.clear();
        }
        this.restaurante = novoRestaurante;

        // Se o item já existe, aumenta a quantidade
        for (ItemCarrinho ic : itens) {
            if (ic.getItem().getNome().equals(item.getNome())) {
                ic.setQuantidade(ic.getQuantidade() + qtd);
                return;
            }
        }
        
        itens.add(new ItemCarrinho(item, qtd));
    }

    public void removerItemPorIndice(int indice) {
        if (indice >= 0 && indice < itens.size()) {
            itens.remove(indice);
        }
    }

    public void limpar() {
        itens.clear();
        restaurante = null;
    }

    public double getTotalItens() {
        return itens.stream().mapToDouble(ItemCarrinho::getSubtotal).sum();
    }

    public double getTotalGeral() {
        if (restaurante == null) return 0;
        return getTotalItens() + restaurante.getTaxaEntrega();
    }

    public boolean isEmpty() {
        return itens.isEmpty();
    }
}