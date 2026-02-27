package com.uberpb.model;

public class ItemCarrinho {
    private ItemCardapio item;
    private int quantidade;

    public ItemCarrinho(ItemCardapio item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
    }

    public ItemCardapio getItem() { return item; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getSubtotal() {
        return item.getPreco() * quantidade;
    }

    @Override
    public String toString() {
        return String.format("%dx %s - R$ %.2f (Subtotal: R$ %.2f)", 
            quantidade, item.getNome(), item.getPreco(), getSubtotal());
    }
}