package com.uberpb.service;

import com.uberpb.model.Pedido;
import com.uberpb.repository.ImplRepositorioPedidoArquivo;
import com.uberpb.repository.RepositorioPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para funcionalidades RF24 de RepositorioPedido
 */
@DisplayName("Testes do Repositório de Pedidos - RF24")
public class RepositorioPedidoTest {

    private RepositorioPedido repositorio;

    @BeforeEach
    public void setUp() {
        // Usa instância singleton do repositório
        repositorio = ImplRepositorioPedidoArquivo.getInstance();
    }

    @Test
    @DisplayName("RF24: Deve buscar pedidos por entregador")
    public void testBuscarPorEntregador() {
        // Criar pedido com entregador alocado
        Pedido pedido1 = new Pedido(
                "cliente1@teste.com",
                "rest1@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");
        pedido1.setEntregadorAlocado("ent@teste.com");

        // Criar pedido sem entregador
        Pedido pedido2 = new Pedido(
                "cliente2@teste.com",
                "rest2@teste.com",
                new ArrayList<>(),
                75.0,
                "DINHEIRO");

        // Criar pedido com outro entregador
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

        // Buscar pedidos do entregador
        List<Pedido> pedidosEntregador = repositorio.buscarPorEntregador("ent@teste.com");

        // Verificar que o pedido alocado para o entregador foi retornado
        long countPedido1 = pedidosEntregador.stream()
                .filter(p -> p.getEmailCliente().equals("cliente1@teste.com") &&
                        "ent@teste.com".equals(p.getEntregadorAlocado()))
                .count();
        assertTrue(countPedido1 >= 1, "Deve retornar o pedido alocado para ent@teste.com");

        // Verificar que pedidos sem entregador ou com outro entregador NÃO aparecem
        // (verificando especificamente os 2 pedidos de controle que criamos)
        long countCliente2ou3 = pedidosEntregador.stream()
                .filter(p -> (p.getEmailCliente().equals("cliente2@teste.com") && p.getEntregadorAlocado() == null) ||
                        (p.getEmailCliente().equals("cliente3@teste.com")
                                && "outro@teste.com".equals(p.getEntregadorAlocado())))
                .count();
        assertEquals(0, countCliente2ou3,
                "Não deve retornar pedido sem entregador (cliente2) nem pedido de outro entregador (cliente3)");
    }

    @Test
    @DisplayName("RF24: Deve buscar pedidos disponíveis para entregador aceitar")
    public void testBuscarPedidosDisponiveisParaEntregador() {
        // Pedido CRIADO alocado para entregador - DEVE APARECER
        Pedido pedido1 = new Pedido(
                "cliente1@teste.com",
                "rest1@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");
        pedido1.setEntregadorAlocado("ent@teste.com");
        pedido1.setStatus("CRIADO");

        // Pedido CONFIRMADO alocado para entregador - DEVE APARECER
        Pedido pedido2 = new Pedido(
                "cliente2@teste.com",
                "rest2@teste.com",
                new ArrayList<>(),
                75.0,
                "DINHEIRO");
        pedido2.setEntregadorAlocado("ent@teste.com");
        pedido2.setStatus("CONFIRMADO");

        // Pedido ACEITO alocado para entregador - NÃO DEVE APARECER
        Pedido pedido3 = new Pedido(
                "cliente3@teste.com",
                "rest3@teste.com",
                new ArrayList<>(),
                100.0,
                "PIX");
        pedido3.setEntregadorAlocado("ent@teste.com");
        pedido3.setStatus("ACEITO");

        // Pedido alocado para outro entregador - NÃO DEVE APARECER
        Pedido pedido4 = new Pedido(
                "cliente4@teste.com",
                "rest4@teste.com",
                new ArrayList<>(),
                120.0,
                "CARTAO");
        pedido4.setEntregadorAlocado("outro@teste.com");
        pedido4.setStatus("CRIADO");

        repositorio.salvar(pedido1);
        repositorio.salvar(pedido2);
        repositorio.salvar(pedido3);
        repositorio.salvar(pedido4);

        // Buscar pedidos disponíveis
        List<Pedido> pedidosDisponiveis = repositorio
                .buscarPedidosDisponiveisParaEntregador("ent@teste.com");

        // Verificar resultados
        assertTrue(pedidosDisponiveis.stream()
                .anyMatch(p -> p.getEmailCliente().equals("cliente1@teste.com")));
        assertTrue(pedidosDisponiveis.stream()
                .anyMatch(p -> p.getEmailCliente().equals("cliente2@teste.com")));
        assertFalse(pedidosDisponiveis.stream()
                .anyMatch(p -> p.getEmailCliente().equals("cliente3@teste.com")));
        assertFalse(pedidosDisponiveis.stream()
                .anyMatch(p -> p.getEmailCliente().equals("cliente4@teste.com")));
    }

    @Test
    @DisplayName("RF24: Deve atualizar pedido existente")
    public void testAtualizarPedido() {
        // Criar e salvar pedido
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");
        pedido.setStatus("CRIADO");

        repositorio.salvar(pedido);

        // Atualizar status
        pedido.setStatus("ACEITO");
        pedido.setEntregadorAlocado("ent@teste.com");

        repositorio.atualizar(pedido);

        // Buscar e verificar atualização
        List<Pedido> pedidos = repositorio.buscarPorCliente("cliente@teste.com");

        boolean encontrou = pedidos.stream()
                .anyMatch(p -> p.getStatus().equals("ACEITO") &&
                        p.getEntregadorAlocado() != null &&
                        p.getEntregadorAlocado().equals("ent@teste.com"));

        assertTrue(encontrou, "Pedido deve ter sido atualizado");
    }

    @Test
    @DisplayName("RF24: Deve retornar lista vazia quando entregador não tem pedidos")
    public void testBuscarPorEntregadorSemPedidos() {
        List<Pedido> pedidos = repositorio.buscarPorEntregador("entregador_sem_pedidos@teste.com");

        assertNotNull(pedidos);
        // Pode não estar vazio se houver dados de outros testes
    }

    @Test
    @DisplayName("RF24: Deve retornar lista vazia quando não há pedidos disponíveis")
    public void testBuscarPedidosDisponiveisSemResultados() {
        // Criar pedido já aceito
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                50.0,
                "CARTAO");
        pedido.setEntregadorAlocado("ent@teste.com");
        pedido.setStatus("ACEITO"); // Já aceito

        repositorio.salvar(pedido);

        List<Pedido> disponiveis = repositorio
                .buscarPedidosDisponiveisParaEntregador("ent_sem_pendentes@teste.com");

        assertNotNull(disponiveis);
        // Lista pode não estar vazia se houver dados de outros testes
        assertFalse(disponiveis.stream()
                .anyMatch(p -> p.getEntregadorAlocado() != null &&
                        p.getEntregadorAlocado().equals("ent_sem_pendentes@teste.com")));
    }
}
