package com.uberpb.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PedidoTest {

    @Test
    public void testConstrutorEGetters() {
        List<ItemCarrinho> itens = new ArrayList<>();
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "restaurante@teste.com",
                itens,
                50.0,
                "CARTAO"
        );

        assertEquals("cliente@teste.com", pedido.getEmailCliente());
        assertEquals("restaurante@teste.com", pedido.getEmailRestaurante());
        assertEquals(itens, pedido.getItens());
        assertEquals(50.0, pedido.getTotal());
        assertEquals("CARTAO", pedido.getFormaPagamento());
        assertEquals("CRIADO", pedido.getStatus());
    }

    @Test
    public void testSetStatus() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "restaurante@teste.com",
                new ArrayList<>(),
                30.0,
                "PIX"
        );

        pedido.setStatus("ENTREGUE");

        assertEquals("ENTREGUE", pedido.getStatus());
    }

    @Test
    public void testToStringParaPersistencia() {
        ItemCardapio item = new ItemCardapio("Pizza", "Calabresa", 40.0);
        ItemCarrinho carrinho = new ItemCarrinho(item, 2);

        List<ItemCarrinho> itens = new ArrayList<>();
        itens.add(carrinho);

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "restaurante@teste.com",
                itens,
                80.0,
                "CARTAO"
        );

        String resultado = pedido.toStringParaPersistencia();

        assertTrue(resultado.contains("cliente@teste.com"));
        assertTrue(resultado.contains("restaurante@teste.com"));
        assertTrue(resultado.contains("Pizza:2;"));
        assertTrue(resultado.contains("80.0"));
        assertTrue(resultado.contains("CARTAO"));
        assertTrue(resultado.contains("CRIADO"));
    }

    @Test
    public void testFromStringValido() {
        String linha = "cliente@teste.com,restaurante@teste.com,ITEMS,100.0,PIX,ENTREGUE";

        Pedido pedido = Pedido.fromString(linha);

        assertNotNull(pedido);
        assertEquals("cliente@teste.com", pedido.getEmailCliente());
        assertEquals("restaurante@teste.com", pedido.getEmailRestaurante());
        assertEquals(100.0, pedido.getTotal());
        assertEquals("PIX", pedido.getFormaPagamento());
        assertEquals("ENTREGUE", pedido.getStatus());
    }

    @Test
    public void testFromStringFormatoInvalido() {
        String linha = "invalido";

        Pedido pedido = Pedido.fromString(linha);

        assertNull(pedido);
    }

    @Test
    public void testFromStringNumeroInvalido() {
        String linha = "cliente@teste.com,restaurante@teste.com,ITEMS,abc,PIX,ENTREGUE";

        Pedido pedido = Pedido.fromString(linha);

        assertNull(pedido);
    }
}