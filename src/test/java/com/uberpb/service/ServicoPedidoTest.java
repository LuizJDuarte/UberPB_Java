package com.uberpb.service;

import com.uberpb.model.Pedido;
import com.uberpb.repository.RepositorioPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para ServicoPedido - RF24
 */
@DisplayName("Testes do Serviço de Pedidos - RF24")
public class ServicoPedidoTest {

    private RepositorioPedido repositorio;
    private ServicoPedido servico;

    @BeforeEach
    public void setUp() {
        repositorio = mock(RepositorioPedido.class);
        servico = new ServicoPedido(repositorio);
    }

    @Test
    @DisplayName("RF24: Deve buscar pedidos por entregador")
    public void testBuscarPorEntregador() {
        Pedido pedido1 = new Pedido(
                "cliente1@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");
        pedido1.setEntregadorAlocado("ent@teste.com");

        Pedido pedido2 = new Pedido(
                "cliente2@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                75.0,
                "DINHEIRO");
        pedido2.setEntregadorAlocado("ent@teste.com");

        List<Pedido> pedidosEsperados = Arrays.asList(pedido1, pedido2);

        when(repositorio.buscarPorEntregador("ent@teste.com")).thenReturn(pedidosEsperados);

        List<Pedido> resultado = servico.buscarPorEntregador("ent@teste.com");

        assertEquals(2, resultado.size());
        verify(repositorio, times(1)).buscarPorEntregador("ent@teste.com");
    }

    @Test
    @DisplayName("RF24: Deve buscar pedidos disponíveis para entregador")
    public void testBuscarPedidosDisponiveisParaEntregador() {
        Pedido pedido1 = new Pedido(
                "cliente1@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");
        pedido1.setEntregadorAlocado("ent@teste.com");
        pedido1.setStatus("CRIADO");

        List<Pedido> pedidosDisponiveis = Arrays.asList(pedido1);

        when(repositorio.buscarPedidosDisponiveisParaEntregador("ent@teste.com"))
                .thenReturn(pedidosDisponiveis);

        List<Pedido> resultado = servico.buscarPedidosDisponiveisParaEntregador("ent@teste.com");

        assertEquals(1, resultado.size());
        assertEquals("CRIADO", resultado.get(0).getStatus());
        verify(repositorio, times(1)).buscarPedidosDisponiveisParaEntregador("ent@teste.com");
    }

    @Test
    @DisplayName("RF24: Deve atualizar pedido")
    public void testAtualizarPedido() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");
        pedido.setStatus("CRIADO");

        servico.atualizarPedido(pedido);

        verify(repositorio, times(1)).atualizar(pedido);
    }

    @Test
    @DisplayName("Deve salvar pedido")
    public void testSalvarPedido() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");

        servico.salvarPedido(pedido);

        verify(repositorio, times(1)).salvar(pedido);
    }

    @Test
    @DisplayName("Deve buscar pedidos por cliente")
    public void testBuscarPorCliente() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");

        when(repositorio.buscarPorCliente("cliente@teste.com"))
                .thenReturn(Arrays.asList(pedido));

        List<Pedido> resultado = servico.buscarPorCliente("cliente@teste.com");

        assertEquals(1, resultado.size());
        verify(repositorio, times(1)).buscarPorCliente("cliente@teste.com");
    }

    @Test
    @DisplayName("Deve buscar pedidos por restaurante")
    public void testBuscarPorRestaurante() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");

        when(repositorio.buscarPorRestaurante("rest@teste.com"))
                .thenReturn(Arrays.asList(pedido));

        List<Pedido> resultado = servico.buscarPorRestaurante("rest@teste.com");

        assertEquals(1, resultado.size());
        verify(repositorio, times(1)).buscarPorRestaurante("rest@teste.com");
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    public void testListarTodos() {
        Pedido pedido1 = new Pedido(
                "cliente1@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");

        Pedido pedido2 = new Pedido(
                "cliente2@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                75.0,
                "DINHEIRO");

        when(repositorio.listarTodos()).thenReturn(Arrays.asList(pedido1, pedido2));

        List<Pedido> resultado = servico.listarTodos();

        assertEquals(2, resultado.size());
        verify(repositorio, times(1)).listarTodos();
    }
}
