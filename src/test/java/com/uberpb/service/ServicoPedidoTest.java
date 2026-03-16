package com.uberpb.service;

import com.uberpb.model.Pedido;
import com.uberpb.repository.RepositorioPedido;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes da classe ServicoPedido")
public class ServicoPedidoTest {

    private RepositorioPedido repositorio;
    private ServicoPedido servico;

    @BeforeEach
    void setUp() {
        repositorio = mock(RepositorioPedido.class);
        servico = new ServicoPedido(repositorio);
    }

    @Test
    @DisplayName("Deve salvar pedido")
    void deveSalvarPedido() {

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO"
        );

        servico.salvarPedido(pedido);

        verify(repositorio, times(1)).salvar(pedido);
    }

    @Test
    @DisplayName("Deve buscar pedidos por cliente")
    void deveBuscarPorCliente() {

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "PIX"
        );

        when(repositorio.buscarPorCliente("cliente@teste.com"))
                .thenReturn(Arrays.asList(pedido));

        List<Pedido> resultado = servico.buscarPorCliente("cliente@teste.com");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(repositorio, times(1)).buscarPorCliente("cliente@teste.com");
    }

    @Test
    @DisplayName("Deve buscar pedidos por restaurante")
    void deveBuscarPorRestaurante() {

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                70.0,
                "DINHEIRO"
        );

        when(repositorio.buscarPorRestaurante("rest@teste.com"))
                .thenReturn(Arrays.asList(pedido));

        List<Pedido> resultado = servico.buscarPorRestaurante("rest@teste.com");

        assertEquals(1, resultado.size());

        verify(repositorio, times(1)).buscarPorRestaurante("rest@teste.com");
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    void deveListarTodos() {

        Pedido p1 = new Pedido(
                "cliente1@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                30.0,
                "PIX"
        );

        Pedido p2 = new Pedido(
                "cliente2@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                80.0,
                "CARTAO"
        );

        when(repositorio.listarTodos())
                .thenReturn(Arrays.asList(p1, p2));

        List<Pedido> resultado = servico.listarTodos();

        assertEquals(2, resultado.size());

        verify(repositorio, times(1)).listarTodos();
    }

    @Test
    @DisplayName("RF24: Deve buscar pedidos por entregador")
    void deveBuscarPorEntregador() {

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                45.0,
                "PIX"
        );

        pedido.setEntregadorAlocado("ent@teste.com");

        when(repositorio.buscarPorEntregador("ent@teste.com"))
                .thenReturn(Arrays.asList(pedido));

        List<Pedido> resultado = servico.buscarPorEntregador("ent@teste.com");

        assertEquals(1, resultado.size());
        assertEquals("ent@teste.com", resultado.get(0).getEntregadorAlocado());

        verify(repositorio, times(1)).buscarPorEntregador("ent@teste.com");
    }

    @Test
    @DisplayName("RF24: Deve buscar pedidos disponíveis para entregador")
    void deveBuscarPedidosDisponiveisParaEntregador() {

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                60.0,
                "PIX"
        );

        pedido.setStatus("CRIADO");

        when(repositorio.buscarPedidosDisponiveisParaEntregador("ent@teste.com"))
                .thenReturn(Arrays.asList(pedido));

        List<Pedido> resultado =
                servico.buscarPedidosDisponiveisParaEntregador("ent@teste.com");

        assertEquals(1, resultado.size());
        assertEquals("CRIADO", resultado.get(0).getStatus());

        verify(repositorio, times(1))
                .buscarPedidosDisponiveisParaEntregador("ent@teste.com");
    }

    @Test
    @DisplayName("RF24: Deve atualizar pedido")
    void deveAtualizarPedido() {

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                90.0,
                "CARTAO"
        );

        servico.atualizarPedido(pedido);

        verify(repositorio, times(1)).atualizar(pedido);
    }
}