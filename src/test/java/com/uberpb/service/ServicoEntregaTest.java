package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioPedido;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para ServicoEntrega (RF22)
 */
@DisplayName("Testes do Serviço de Entrega - RF22")
public class ServicoEntregaTest {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioPedido repositorioPedido;
    private ServicoLocalizacao servicoLocalizacao;
    private ServicoNotificacao servicoNotificacao;
    private ServicoEntrega servicoEntrega;

    @BeforeEach
    public void setUp() {
        repositorioUsuario = mock(RepositorioUsuario.class);
        repositorioPedido = mock(RepositorioPedido.class);
        servicoLocalizacao = mock(ServicoLocalizacao.class);
        servicoNotificacao = mock(ServicoNotificacao.class);

        servicoEntrega = new ServicoEntrega(
                repositorioUsuario,
                repositorioPedido,
                servicoLocalizacao,
                servicoNotificacao);
    }

    @Test
    @DisplayName("Deve buscar entregador mais próximo do restaurante")
    public void testBuscarEntregadorMaisProximo() {
        // Configurar restaurante
        Restaurante restaurante = new Restaurante("rest@teste.com", "hash");
        restaurante.setNomeFantasia("Restaurante Teste");
        restaurante.setLocalizacao(new Localizacao(-7.12, -34.88));

        // Configurar entregadores
        Entregador entregador1 = new Entregador("ent1@teste.com", "hash");
        entregador1.setContaAtiva(true);
        entregador1.setDisponivel(true);

        Entregador entregador2 = new Entregador("ent2@teste.com", "hash");
        entregador2.setContaAtiva(true);
        entregador2.setDisponivel(true);

        List<Usuario> usuarios = Arrays.asList(restaurante, entregador1, entregador2);

        when(repositorioUsuario.buscarPorEmail("rest@teste.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(usuarios);

        String emailEntregador = servicoEntrega.buscarEntregadorMaisProximo("rest@teste.com");

        assertNotNull(emailEntregador);
        assertTrue(emailEntregador.equals("ent1@teste.com") || emailEntregador.equals("ent2@teste.com"));
    }

    @Test
    @DisplayName("Deve retornar null quando não há entregadores disponíveis")
    public void testBuscarEntregadorSemDisponiveis() {
        Restaurante restaurante = new Restaurante("rest@teste.com", "hash");
        restaurante.setLocalizacao(new Localizacao(-7.12, -34.88));

        // Entregador indisponível
        Entregador entregador = new Entregador("ent@teste.com", "hash");
        entregador.setContaAtiva(true);
        entregador.setDisponivel(false); // OFFLINE

        List<Usuario> usuarios = Arrays.asList(restaurante, entregador);

        when(repositorioUsuario.buscarPorEmail("rest@teste.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(usuarios);

        String emailEntregador = servicoEntrega.buscarEntregadorMaisProximo("rest@teste.com");

        assertNull(emailEntregador);
    }

    @Test
    @DisplayName("Deve processar novo pedido com notificações para restaurante e entregador")
    public void testProcessarNovoPedido() {
        // Configurar restaurante
        Restaurante restaurante = new Restaurante("rest@teste.com", "hash");
        restaurante.setNomeFantasia("Pizzaria Teste");
        restaurante.setLocalizacao(new Localizacao(-7.12, -34.88));

        // Configurar entregador
        Entregador entregador = new Entregador("ent@teste.com", "hash");
        entregador.setContaAtiva(true);
        entregador.setDisponivel(true);

        // Configurar pedido
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                100.0,
                "CARTAO");

        List<Usuario> usuarios = Arrays.asList(restaurante, entregador);

        when(repositorioUsuario.buscarPorEmail("rest@teste.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(usuarios);

        boolean resultado = servicoEntrega.processarNovoPedido(pedido);

        assertTrue(resultado);
        assertEquals("ent@teste.com", pedido.getEntregadorAlocado());

        // Verificar que notificações foram enviadas
        verify(servicoNotificacao, times(1)).notificarRestauranteNovoPedido(
                eq("rest@teste.com"), eq("cliente@teste.com"), eq(100.0));
        verify(servicoNotificacao, times(1)).notificarEntregadorPedidoDisponivel(
                eq("ent@teste.com"), anyString(), anyDouble());
    }

    @Test
    @DisplayName("Deve retornar false quando não há entregador disponível ao processar pedido")
    public void testProcessarPedidoSemEntregador() {
        Restaurante restaurante = new Restaurante("rest@teste.com", "hash");
        restaurante.setLocalizacao(new Localizacao(-7.12, -34.88));

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                100.0,
                "CARTAO");

        List<Usuario> usuarios = Arrays.asList(restaurante);

        when(repositorioUsuario.buscarPorEmail("rest@teste.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(usuarios);

        boolean resultado = servicoEntrega.processarNovoPedido(pedido);

        assertFalse(resultado);
        assertNull(pedido.getEntregadorAlocado());

        // Deve notificar restaurante mesmo sem entregador
        verify(servicoNotificacao, times(1)).notificarRestauranteNovoPedido(
                anyString(), anyString(), anyDouble());
    }

    @Test
    @DisplayName("Deve listar apenas entregadores disponíveis e ativos")
    public void testListarEntregadoresDisponiveis() {
        Entregador ent1 = new Entregador("ent1@teste.com", "hash");
        ent1.setContaAtiva(true);
        ent1.setDisponivel(true);

        Entregador ent2 = new Entregador("ent2@teste.com", "hash");
        ent2.setContaAtiva(true);
        ent2.setDisponivel(false); // Offline

        Entregador ent3 = new Entregador("ent3@teste.com", "hash");
        ent3.setContaAtiva(false); // Conta inativa
        ent3.setDisponivel(true);

        List<Usuario> usuarios = Arrays.asList(ent1, ent2, ent3);
        when(repositorioUsuario.buscarTodos()).thenReturn(usuarios);

        List<Entregador> disponiveis = servicoEntrega.listarEntregadoresDisponiveis();

        assertEquals(1, disponiveis.size());
        assertEquals("ent1@teste.com", disponiveis.get(0).getEmail());
    }

    // ========== TESTES RF24 - ACEITAR/RECUSAR PEDIDOS ==========

    @Test
    @DisplayName("RF24: Deve permitir entregador aceitar pedido alocado para ele")
    public void testAceitarPedidoComSucesso() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                100.0,
                "CARTAO");
        pedido.setEntregadorAlocado("ent@teste.com");
        pedido.setStatus("CRIADO");

        boolean resultado = servicoEntrega.aceitarPedido("ent@teste.com", pedido);

        assertTrue(resultado);
        assertEquals("ACEITO", pedido.getStatus());

        // Verifica que o pedido foi atualizado no repositório
        verify(repositorioPedido, times(1)).atualizar(pedido);

        // Verifica que o cliente foi notificado
        verify(servicoNotificacao, times(1)).notificarCliente(
                eq("cliente@teste.com"),
                anyString());
    }

    @Test
    @DisplayName("RF24: Não deve permitir aceitar pedido não alocado para o entregador")
    public void testAceitarPedidoNaoAlocado() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                100.0,
                "CARTAO");
        pedido.setEntregadorAlocado("outro@teste.com");
        pedido.setStatus("CRIADO");

        boolean resultado = servicoEntrega.aceitarPedido("ent@teste.com", pedido);

        assertFalse(resultado);
        assertEquals("CRIADO", pedido.getStatus()); // Status não deve mudar

        // Não deve atualizar repositório
        verify(repositorioPedido, never()).atualizar(any());
    }

    @Test
    @DisplayName("RF24: Não deve permitir aceitar pedido já processado")
    public void testAceitarPedidoJaProcessado() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                100.0,
                "CARTAO");
        pedido.setEntregadorAlocado("ent@teste.com");
        pedido.setStatus("ACEITO"); // Já aceito

        boolean resultado = servicoEntrega.aceitarPedido("ent@teste.com", pedido);

        assertFalse(resultado);

        // Não deve atualizar repositório
        verify(repositorioPedido, never()).atualizar(any());
    }

    @Test
    @DisplayName("RF24: Deve permitir entregador recusar pedido e alocar novo entregador")
    public void testRecusarPedidoComNovoEntregador() {
        // Configurar restaurante
        Restaurante restaurante = new Restaurante("rest@teste.com", "hash");
        restaurante.setLocalizacao(new Localizacao(-7.12, -34.88));

        // Primeiro entregador (que vai recusar)
        Entregador entregador1 = new Entregador("ent1@teste.com", "hash");
        entregador1.setContaAtiva(true);
        entregador1.setDisponivel(true);

        // Segundo entregador (que será alocado)
        Entregador entregador2 = new Entregador("ent2@teste.com", "hash");
        entregador2.setContaAtiva(true);
        entregador2.setDisponivel(true);

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                100.0,
                "CARTAO");
        pedido.setEntregadorAlocado("ent1@teste.com");
        pedido.setStatus("CRIADO");

        List<Usuario> usuarios = Arrays.asList(restaurante, entregador1, entregador2);

        when(repositorioUsuario.buscarPorEmail("rest@teste.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(usuarios);

        // Configurar localizações para cenários de busca de entregador
        Localizacao locEnt1 = new Localizacao(-7.15, -34.90); // Mais longe
        Localizacao locEnt2 = new Localizacao(-7.11, -34.87); // Mais perto
        Localizacao locRest = new Localizacao(-7.12, -34.88);

        // Mocks para localização
        when(servicoLocalizacao.obterLocalizacaoAtual("ent1@teste.com")).thenReturn(locEnt1);
        when(servicoLocalizacao.obterLocalizacaoAtual("ent2@teste.com")).thenReturn(locEnt2);
        when(servicoLocalizacao.distanciaKm(locEnt1, locRest)).thenReturn(5.0);
        when(servicoLocalizacao.distanciaKm(locEnt2, locRest)).thenReturn(2.0);

        boolean resultado = servicoEntrega.recusarPedido("ent1@teste.com", pedido);

        assertTrue(resultado);

        // Verifica que novo entregador foi alocado
        assertEquals("ent2@teste.com", pedido.getEntregadorAlocado());
        assertEquals("CRIADO", pedido.getStatus()); // Volta para CRIADO para novo entregador

        // Verifica que o repositório foi atualizado duas vezes (recusa + nova alocação)
        verify(repositorioPedido, atLeast(2)).atualizar(pedido);

        // Verifica que novo entregador foi notificado
        verify(servicoNotificacao, times(1)).notificarEntregadorPedidoDisponivel(
                eq("ent2@teste.com"),
                anyString(),
                anyDouble());
    }

    @Test
    @DisplayName("RF24: Deve permitir recusar pedido mesmo sem novo entregador disponível")
    public void testRecusarPedidoSemNovoEntregador() {
        Restaurante restaurante = new Restaurante("rest@teste.com", "hash");
        restaurante.setLocalizacao(new Localizacao(-7.12, -34.88));

        Entregador entregador = new Entregador("ent@teste.com", "hash");
        entregador.setContaAtiva(true);
        entregador.setDisponivel(true);

        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                100.0,
                "CARTAO");
        pedido.setEntregadorAlocado("ent@teste.com");
        pedido.setStatus("CRIADO");

        List<Usuario> usuarios = Arrays.asList(restaurante, entregador);

        when(repositorioUsuario.buscarPorEmail("rest@teste.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(usuarios);

        boolean resultado = servicoEntrega.recusarPedido("ent@teste.com", pedido);

        assertTrue(resultado);
        assertEquals("RECUSADO", pedido.getStatus());
        assertNull(pedido.getEntregadorAlocado());

        // Verifica que foi atualizado no repositório
        verify(repositorioPedido, atLeast(1)).atualizar(pedido);
    }

    @Test
    @DisplayName("RF24: Não deve permitir recusar pedido não alocado para o entregador")
    public void testRecusarPedidoNaoAlocado() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                100.0,
                "CARTAO");
        pedido.setEntregadorAlocado("outro@teste.com");
        pedido.setStatus("CRIADO");

        boolean resultado = servicoEntrega.recusarPedido("ent@teste.com", pedido);

        assertFalse(resultado);
        assertEquals("CRIADO", pedido.getStatus());
        assertEquals("outro@teste.com", pedido.getEntregadorAlocado());

        // Não deve atualizar repositório
        verify(repositorioPedido, never()).atualizar(any());
    }

    @Test
    @DisplayName("RF24: Não deve permitir recusar pedido já processado")
    public void testRecusarPedidoJaProcessado() {
        Pedido pedido = new Pedido(
                "cliente@teste.com",
                "rest@teste.com",
                new ArrayList<>(),
                100.0,
                "CARTAO");
        pedido.setEntregadorAlocado("ent@teste.com");
        pedido.setStatus("EM_ROTA"); // Já em rota

        boolean resultado = servicoEntrega.recusarPedido("ent@teste.com", pedido);

        assertFalse(resultado);
        assertEquals("EM_ROTA", pedido.getStatus());

        // Não deve atualizar repositório
        verify(repositorioPedido, never()).atualizar(any());
    }
}
