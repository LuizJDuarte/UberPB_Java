package com.uberpb.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemCarrinhoTest {

    @Test
    public void testConstrutorEGetters() {
        ItemCardapio item = new ItemCardapio("Pizza", "Calabresa", 30.0);
        ItemCarrinho carrinho = new ItemCarrinho(item, 2);

        assertEquals(item, carrinho.getItem());
        assertEquals(2, carrinho.getQuantidade());
    }

    @Test
    public void testSetQuantidade() {
        ItemCardapio item = new ItemCardapio("Hamburguer", "Artesanal", 25.0);
        ItemCarrinho carrinho = new ItemCarrinho(item, 1);

        carrinho.setQuantidade(3);

        assertEquals(3, carrinho.getQuantidade());
    }

    @Test
    public void testGetSubtotal() {
        ItemCardapio item = new ItemCardapio("Suco", "Laranja", 7.5);
        ItemCarrinho carrinho = new ItemCarrinho(item, 4);

        double subtotal = carrinho.getSubtotal();

        assertEquals(30.0, subtotal);
    }

    @Test
    public void testToString() {
        ItemCardapio item = new ItemCardapio("Refrigerante", "Lata", 6.0);
        ItemCarrinho carrinho = new ItemCarrinho(item, 3);

        String esperado = String.format(
                "%dx %s - R$ %.2f (Subtotal: R$ %.2f)",
                3, "Refrigerante", 6.0, 18.0);

        assertEquals(esperado, carrinho.toString());
    }
}