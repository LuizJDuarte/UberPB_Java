package com.uberpb.service;

import com.uberpb.model.CarrinhoCompras;
import com.uberpb.model.ItemCardapio;
import com.uberpb.model.Restaurante;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoCarrinhoTest {

    @Test
    void deveCriarCarrinhoAutomaticamente() {

        ServicoCarrinho servico = new ServicoCarrinho();

        CarrinhoCompras carrinho = servico.obterCarrinho("cliente@email.com");

        assertNotNull(carrinho);
    }

    @Test
    void deveAdicionarItemAoCarrinho() {

        ServicoCarrinho servico = new ServicoCarrinho();

        Restaurante restaurante = mock(Restaurante.class);
        ItemCardapio item = mock(ItemCardapio.class);

        servico.adicionarAoCarrinho(
                "cliente@email.com",
                restaurante,
                item,
                2
        );

        CarrinhoCompras carrinho = servico.obterCarrinho("cliente@email.com");

        assertNotNull(carrinho);
    }

    @Test
    void deveLimparCarrinho() {

        ServicoCarrinho servico = new ServicoCarrinho();

        Restaurante restaurante = mock(Restaurante.class);
        ItemCardapio item = mock(ItemCardapio.class);

        servico.adicionarAoCarrinho(
                "cliente@email.com",
                restaurante,
                item,
                1
        );

        servico.limparCarrinho("cliente@email.com");

        CarrinhoCompras carrinho = servico.obterCarrinho("cliente@email.com");

        assertNotNull(carrinho);
    }

    @Test
    void cadaUsuarioDeveTerCarrinhoSeparado() {

        ServicoCarrinho servico = new ServicoCarrinho();

        CarrinhoCompras carrinho1 = servico.obterCarrinho("user1@email.com");
        CarrinhoCompras carrinho2 = servico.obterCarrinho("user2@email.com");

        assertNotSame(carrinho1, carrinho2);
    }
}