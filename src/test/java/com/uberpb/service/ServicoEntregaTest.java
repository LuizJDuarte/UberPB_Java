package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioPedido;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para ServicoEntrega (RF22)
 * Versão com cobertura ampliada
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

        Restaurante restaurante = new Restaurante("rest@teste.com", "hash");
        restaurante.setNomeFantasia("Restaurante Teste");
        restaurante.setLocalizacao(new Localizacao(-7.12, -34.88));

        Entregador entregador1 = new Entregador("ent1@teste.com", "hash");
        entregador1.setContaAtiva(true);
        entregador1.setDisponivel(true);

        Entregador entregador2 = new Entregador("ent2@teste.com", "hash");
        entregador2.setContaAtiva(true);
        entregador2.setDisponivel(true);

        List<Usuario> usuarios = Arrays.asList(restaurante, entregador1, entregador2);

        when(repositorioUsuario.buscarPorEmail("rest@teste.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(usuarios);

        when(servicoLocalizacao.obterLocalizacaoAtual("ent1@teste.com"))
                .thenReturn(new Localizacao(-7.13, -34.89));
        when(servicoLocalizacao.obterLocalizacaoAtual("ent2@teste.com"))
                .thenReturn(new Localizacao(-7.14, -34.90));

        when(servicoLocalizacao.distanciaKm(any(), any())).thenReturn(1.0, 2.0);

        String emailEntregador = servicoEntrega.buscarEntregadorMaisProximo("rest@teste.com");

        assertEquals("ent1@teste.com", emailEntregador);
    }

    @Test
    @DisplayName("Deve processar novo pedido apenas notificando restaurante")
    public void testProcessarNovoPedido() {

        Pedido pedido = new Pedido("cliente@teste.com", "rest@teste.com", new ArrayList<>(), 100.0, "CARTAO");

        boolean resultado = servicoEntrega.processarNovoPedido(pedido);

        assertTrue(resultado);
        assertNull(pedido.getEntregadorAlocado());

        verify(servicoNotificacao, times(1))
                .notificarRestauranteNovoPedido(anyString(), anyString(), anyDouble());

        verify(servicoNotificacao, never())
                .notificarEntregadorPedidoDisponivel(anyString(), anyString(), anyDouble());
    }

    @Test
    @DisplayName("RF24: Deve permitir entregador aceitar pedido alocado para ele")
    public void testAceitarPedidoComSucesso() {

        Pedido pedido = new Pedido("c@t.com", "r@t.com", new ArrayList<>(), 100.0, "CARTAO");
        pedido.setEntregadorAlocado("ent@teste.com");
        pedido.setStatus("CONFIRMADO");

        boolean resultado = servicoEntrega.aceitarPedido("ent@teste.com", pedido);

        assertTrue(resultado);

        verify(repositorioPedido, times(1)).atualizar(pedido);

        verify(servicoNotificacao, times(1))
                .notificarCliente(eq("c@t.com"), anyString());
    }

    @Test
    @DisplayName("RF24: Deve permitir entregador recusar pedido e buscar outro")
    public void testRecusarPedidoComNovoEntregador() {

        Restaurante restaurante = new Restaurante("r@t.com", "hash");
        restaurante.setLocalizacao(new Localizacao(0, 0));

        Entregador e1 = new Entregador("e1@t.com", "hash");
        e1.setContaAtiva(true);
        e1.setDisponivel(true);

        Entregador e2 = new Entregador("e2@t.com", "hash");
        e2.setContaAtiva(true);
        e2.setDisponivel(true);

        Pedido pedido = new Pedido("c@t.com", "r@t.com", new ArrayList<>(), 100.0, "CARTAO");
        pedido.setEntregadorAlocado("e1@t.com");
        pedido.setStatus("CONFIRMADO");

        when(repositorioUsuario.buscarPorEmail("r@t.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(Arrays.asList(restaurante, e1, e2));

        when(servicoLocalizacao.obterLocalizacaoAtual(anyString())).thenReturn(new Localizacao(0,0));
        when(servicoLocalizacao.distanciaKm(any(), any())).thenReturn(10.0, 1.0);

        boolean resultado = servicoEntrega.recusarPedido("e1@t.com", pedido);

        assertTrue(resultado);
        assertEquals("e2@t.com", pedido.getEntregadorAlocado());

        verify(repositorioPedido, atLeastOnce()).atualizar(pedido);

        verify(servicoNotificacao, times(1))
                .notificarEntregadorPedidoDisponivel(eq("e2@t.com"), anyString(), anyDouble());
    }

    @Test
    @DisplayName("RF24: Deve permitir recusar pedido mesmo sem outro entregador")
    public void testRecusarPedidoSemNovoEntregador() {

        Restaurante restaurante = new Restaurante("r@t.com", "hash");

        Pedido pedido = new Pedido("c@t.com", "r@t.com", new ArrayList<>(), 100.0, "CARTAO");
        pedido.setEntregadorAlocado("e1@t.com");
        pedido.setStatus("CONFIRMADO");

        when(repositorioUsuario.buscarPorEmail("r@t.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(Arrays.asList(restaurante));

        boolean resultado = servicoEntrega.recusarPedido("e1@t.com", pedido);

        assertTrue(resultado);
        assertNull(pedido.getEntregadorAlocado());

        verify(repositorioPedido, atLeastOnce()).atualizar(pedido);
    }

    // ---------- NOVOS TESTES PARA COBERTURA ----------

    @Test
    @DisplayName("Deve retornar null se o usuário não for restaurante")
    public void testBuscarEntregadorRestauranteInvalido() {

        Usuario usuario = new Entregador("ent@teste.com", "hash");

        when(repositorioUsuario.buscarPorEmail("email@teste.com")).thenReturn(usuario);

        String resultado = servicoEntrega.buscarEntregadorMaisProximo("email@teste.com");

        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve retornar null quando não há entregadores disponíveis")
    public void testBuscarEntregadorSemDisponiveis() {

        Restaurante restaurante = new Restaurante("rest@teste.com", "hash");
        restaurante.setLocalizacao(new Localizacao(0,0));

        when(repositorioUsuario.buscarPorEmail("rest@teste.com")).thenReturn(restaurante);
        when(repositorioUsuario.buscarTodos()).thenReturn(Arrays.asList(restaurante));

        String resultado = servicoEntrega.buscarEntregadorMaisProximo("rest@teste.com");

        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve listar entregadores disponíveis")
    public void testListarEntregadoresDisponiveis() {

        Entregador e1 = new Entregador("e1@teste.com", "hash");
        e1.setDisponivel(true);
        e1.setContaAtiva(true);

        Entregador e2 = new Entregador("e2@teste.com", "hash");
        e2.setDisponivel(false);
        e2.setContaAtiva(true);

        when(repositorioUsuario.buscarTodos()).thenReturn(Arrays.asList(e1, e2));

        List<Entregador> resultado = servicoEntrega.listarEntregadoresDisponiveis();

        assertEquals(1, resultado.size());
        assertEquals("e1@teste.com", resultado.get(0).getEmail());
    }

    @Test
    @DisplayName("Aceitar pedido deve falhar se não estiver alocado ao entregador")
    public void testAceitarPedidoNaoAlocado() {

        Pedido pedido = new Pedido("c@t.com", "r@t.com", new ArrayList<>(), 100.0, "CARTAO");
        pedido.setEntregadorAlocado("outro@teste.com");
        pedido.setStatus("CONFIRMADO");

        boolean resultado = servicoEntrega.aceitarPedido("ent@teste.com", pedido);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Aceitar pedido deve falhar se status inválido")
    public void testAceitarPedidoStatusInvalido() {

        Pedido pedido = new Pedido("c@t.com", "r@t.com", new ArrayList<>(), 100.0, "CARTAO");
        pedido.setEntregadorAlocado("ent@teste.com");
        pedido.setStatus("CRIADO");

        boolean resultado = servicoEntrega.aceitarPedido("ent@teste.com", pedido);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Recusar pedido deve falhar se não estiver alocado")
    public void testRecusarPedidoNaoAlocado() {

        Pedido pedido = new Pedido("c@t.com", "r@t.com", new ArrayList<>(), 100.0, "CARTAO");
        pedido.setEntregadorAlocado("outro@teste.com");
        pedido.setStatus("CONFIRMADO");

        boolean resultado = servicoEntrega.recusarPedido("ent@teste.com", pedido);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Recusar pedido deve falhar se status inválido")
    public void testRecusarPedidoStatusInvalido() {

        Pedido pedido = new Pedido("c@t.com", "r@t.com", new ArrayList<>(), 100.0, "CARTAO");
        pedido.setEntregadorAlocado("ent@teste.com");
        pedido.setStatus("CRIADO");

        boolean resultado = servicoEntrega.recusarPedido("ent@teste.com", pedido);

        assertFalse(resultado);
    }
}