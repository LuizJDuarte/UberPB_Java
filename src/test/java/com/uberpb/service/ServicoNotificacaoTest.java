package com.uberpb.service;

import com.uberpb.model.Notificacao;
import com.uberpb.model.TipoNotificacao;
import com.uberpb.repository.RepositorioNotificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para ServicoNotificacao (RF22)
 */
@DisplayName("Testes do Serviço de Notificações - RF22")
public class ServicoNotificacaoTest {

    private RepositorioNotificacao repositorio;
    private ServicoNotificacao servico;

    @BeforeEach
    public void setUp() {
        repositorio = mock(RepositorioNotificacao.class);
        servico = new ServicoNotificacao(repositorio);
    }

    @Test
    @DisplayName("Deve enviar notificação para restaurante sobre novo pedido")
    public void testNotificarRestauranteNovoPedido() {
        String emailRestaurante = "restaurante@teste.com";
        String emailCliente = "cliente@teste.com";
        double total = 50.0;

        Notificacao notificacao = servico.notificarRestauranteNovoPedido(
                emailRestaurante, emailCliente, total);

        assertNotNull(notificacao);
        assertEquals(emailRestaurante, notificacao.getDestinatarioEmail());
        assertEquals(TipoNotificacao.NOVO_PEDIDO_RESTAURANTE, notificacao.getTipo());
        assertTrue(notificacao.getMensagem().contains("Novo pedido"));
        assertTrue(notificacao.getMensagem().contains(emailCliente));
        verify(repositorio, times(1)).salvar(any(Notificacao.class));
    }

    @Test
    @DisplayName("Deve enviar notificação para entregador sobre pedido disponível")
    public void testNotificarEntregadorPedidoDisponivel() {
        String emailEntregador = "entregador@teste.com";
        String emailRestaurante = "restaurante@teste.com";
        double valorEntrega = 7.5;

        Notificacao notificacao = servico.notificarEntregadorPedidoDisponivel(
                emailEntregador, emailRestaurante, valorEntrega);

        assertNotNull(notificacao);
        assertEquals(emailEntregador, notificacao.getDestinatarioEmail());
        assertEquals(TipoNotificacao.PEDIDO_DISPONIVEL_ENTREGADOR, notificacao.getTipo());
        assertTrue(notificacao.getMensagem().contains("Pedido disponível"));
        assertTrue(notificacao.getMensagem().contains(emailRestaurante));
        verify(repositorio, times(1)).salvar(any(Notificacao.class));
    }

    @Test
    @DisplayName("Deve buscar notificações não lidas de um usuário")
    public void testBuscarNotificacoesNaoLidas() {
        String email = "usuario@teste.com";
        List<Notificacao> notificacoesNaoLidas = new ArrayList<>();
        notificacoesNaoLidas.add(new Notificacao("1", email, TipoNotificacao.SISTEMA, "Teste"));

        when(repositorio.buscarNaoLidasPorDestinatario(email)).thenReturn(notificacoesNaoLidas);

        List<Notificacao> resultado = servico.buscarNotificacoesNaoLidas(email);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repositorio, times(1)).buscarNaoLidasPorDestinatario(email);
    }

    @Test
    @DisplayName("Deve marcar notificação como lida")
    public void testMarcarComoLida() {
        String notifId = "123";
        Notificacao notificacao = new Notificacao(notifId, "user@test.com",
                TipoNotificacao.SISTEMA, "Teste");

        when(repositorio.buscarPorId(notifId)).thenReturn(notificacao);

        servico.marcarComoLida(notifId);

        assertTrue(notificacao.isLida());
        verify(repositorio, times(1)).buscarPorId(notifId);
        verify(repositorio, times(1)).atualizar(notificacao);
    }

    @Test
    @DisplayName("Deve contar notificações não lidas corretamente")
    public void testContarNotificacoesNaoLidas() {
        String email = "usuario@teste.com";
        List<Notificacao> notificacoes = new ArrayList<>();
        notificacoes.add(new Notificacao("1", email, TipoNotificacao.SISTEMA, "Msg1"));
        notificacoes.add(new Notificacao("2", email, TipoNotificacao.SISTEMA, "Msg2"));

        when(repositorio.buscarNaoLidasPorDestinatario(email)).thenReturn(notificacoes);

        int count = servico.contarNotificacoesNaoLidas(email);

        assertEquals(2, count);
        verify(repositorio, times(1)).buscarNaoLidasPorDestinatario(email);
    }
}
