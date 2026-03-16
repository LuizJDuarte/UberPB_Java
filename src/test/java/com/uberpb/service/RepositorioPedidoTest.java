package com.uberpb.service;

import com.uberpb.model.Pedido;
import com.uberpb.repository.ImplRepositorioPedidoArquivo;
import com.uberpb.repository.RepositorioPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Repositório de Pedidos - RF24")
public class RepositorioPedidoTest {

    private RepositorioPedido repositorio;

    @BeforeEach
    public void setUp() {

        repositorio = ImplRepositorioPedidoArquivo.getInstance();

        // 🔴 MUDANÇA AQUI
        // ANTES ERA ASSIM:
        /*
        repositorio = ImplRepositorioPedidoArquivo.getInstance();
        */

        // ✔ AGORA limpamos os dados antes de cada teste
        ((ImplRepositorioPedidoArquivo) repositorio).limpar();
    }

    @Test
    @DisplayName("RF24: Deve buscar pedidos por entregador")
    public void testBuscarPorEntregador() {

        Pedido pedido1 = new Pedido(
                "cliente1@teste.com",
                "rest1@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");
        pedido1.setEntregadorAlocado("ent@teste.com");

        Pedido pedido2 = new Pedido(
                "cliente2@teste.com",
                "rest2@teste.com",
                new ArrayList<>(),
                75.0,
                "DINHEIRO");

        Pedido pedido3 = new Pedido(
                "cliente3@teste.com",
                "rest3@teste.com",
                new ArrayList<>(),
                100.0,
                "PIX");
        pedido3.setEntregadorAlocado("outro@teste.com");

        repositorio.salvar(pedido1);
        repositorio.salvar(pedido2);
        repositorio.salvar(pedido3);

        List<Pedido> pedidosEntregador = repositorio.buscarPorEntregador("ent@teste.com");

        long countPedido1 = pedidosEntregador.stream()
                .filter(p -> p.getEmailCliente().equals("cliente1@teste.com"))
                .count();

        assertTrue(countPedido1 >= 1);

        long countCliente2ou3 = pedidosEntregador.stream()
                .filter(p -> p.getEmailCliente().equals("cliente2@teste.com") ||
                        p.getEmailCliente().equals("cliente3@teste.com"))
                .count();

        assertEquals(0, countCliente2ou3);
    }

    // restante da classe permanece igual
}