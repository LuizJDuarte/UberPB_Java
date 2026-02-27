package com.uberpb.model;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CarrinhoComprasTest {

    private Restaurante criarRestaurante(String email, double taxa) {
        Restaurante r = new Restaurante(email, "123");
        r.setTaxaEntrega(taxa);
        return r;
    }

   private ItemCardapio criarItem(String nome, double preco) {
    return new ItemCardapio(nome, "Descricao padrão", preco);
}

    @Test
    void deveAdicionarItemAoCarrinho() {
        Restaurante restaurante = new Restaurante("rest@test.com", "hash");
        restaurante.setTaxaEntrega(5.0);

        ItemCardapio item = new ItemCardapio(
                "Hamburguer",
                "Hamburguer artesanal",
                20.0);

        CarrinhoCompras carrinho = new CarrinhoCompras();
        carrinho.adicionarItem(restaurante, item, 2);

        assertEquals(1, carrinho.getItens().size());
        assertEquals(40.0, carrinho.getTotalItens());
        assertEquals(45.0, carrinho.getTotalGeral());
    }

    @Test
    void deveSomarQuantidadeSeItemJaExistir() {

        CarrinhoCompras carrinho = new CarrinhoCompras();
        Restaurante r = criarRestaurante("r1@email.com", 5.0);
        ItemCardapio item = criarItem("Pizza", 30.0);

        carrinho.adicionarItem(r, item, 1);
        carrinho.adicionarItem(r, item, 2);

        assertEquals(1, carrinho.getItens().size());
        assertEquals(3, carrinho.getItens().get(0).getQuantidade());
    }

    @Test
    void deveLimparCarrinhoSeMudarRestaurante() {

        CarrinhoCompras carrinho = new CarrinhoCompras();
        Restaurante r1 = criarRestaurante("r1@email.com", 5.0);
        Restaurante r2 = criarRestaurante("r2@email.com", 7.0);

        ItemCardapio item = criarItem("Pizza", 30.0);

        carrinho.adicionarItem(r1, item, 1);
        carrinho.adicionarItem(r2, item, 1);

        assertEquals(r2, carrinho.getRestaurante());
        assertEquals(1, carrinho.getItens().size());
    }

    @Test
    void deveRemoverItemPorIndiceValido() {

        CarrinhoCompras carrinho = new CarrinhoCompras();
        Restaurante r = criarRestaurante("r1@email.com", 5.0);
        ItemCardapio item = criarItem("Pizza", 30.0);

        carrinho.adicionarItem(r, item, 1);
        carrinho.removerItemPorIndice(0);

        assertTrue(carrinho.getItens().isEmpty());
    }

    @Test
    void naoDeveRemoverIndiceInvalido() {

        CarrinhoCompras carrinho = new CarrinhoCompras();
        carrinho.removerItemPorIndice(5); // não deve quebrar

        assertTrue(carrinho.getItens().isEmpty());
    }

    @Test
    void deveCalcularTotalGeralComTaxa() {

        CarrinhoCompras carrinho = new CarrinhoCompras();
        Restaurante r = criarRestaurante("r1@email.com", 10.0);
        ItemCardapio item = criarItem("Pizza", 20.0);

        carrinho.adicionarItem(r, item, 2); // 40

        assertEquals(50.0, carrinho.getTotalGeral());
    }

    @Test
    void totalGeralDeveSerZeroSemRestaurante() {

        CarrinhoCompras carrinho = new CarrinhoCompras();

        assertEquals(0, carrinho.getTotalGeral());
    }

    @Test
    void deveLimparCarrinhoComMetodoLimpar() {

        CarrinhoCompras carrinho = new CarrinhoCompras();
        Restaurante r = criarRestaurante("r1@email.com", 5.0);
        ItemCardapio item = criarItem("Pizza", 30.0);

        carrinho.adicionarItem(r, item, 1);
        carrinho.limpar();

        assertTrue(carrinho.isEmpty());
        assertNull(carrinho.getRestaurante());
    }
}