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

        servico.notificarRestauranteNovoPedido(
                emailRestaurante, emailCliente, total);

        verify(repositorio, times(1)).salvar(any(Notificacao.class));
    }

    @Test
    @DisplayName("Deve enviar notificação para entregador sobre pedido disponível")
    public void testNotificarEntregadorPedidoDisponivel() {

        String emailEntregador = "entregador@teste.com";
        String emailRestaurante = "restaurante@teste.com";
        double valorEntrega = 7.5;

        servico.notificarEntregadorPedidoDisponivel(
                emailEntregador, emailRestaurante, valorEntrega);

        verify(repositorio, times(1)).salvar(any(Notificacao.class));
    }

    @Test
    @DisplayName("Deve buscar notificações não lidas de um usuário")
    public void testBuscarNotificacoesNaoLidas() {

        String email = "usuario@teste.com";

        List<Notificacao> notificacoes = new ArrayList<>();
        notificacoes.add(new Notificacao("1", email, TipoNotificacao.SISTEMA, "Teste"));

        when(repositorio.buscarNaoLidasPorDestinatario(email))
                .thenReturn(notificacoes);

        List<Notificacao> resultado = servico.buscarNotificacoesNaoLidas(email);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        verify(repositorio).buscarNaoLidasPorDestinatario(email);
    }

    @Test
    @DisplayName("Deve marcar notificação como lida")
    public void testMarcarComoLida() {

        String notifId = "123";

        Notificacao notificacao =
                new Notificacao(notifId, "user@test.com", TipoNotificacao.SISTEMA, "Teste");

        when(repositorio.buscarPorId(notifId)).thenReturn(notificacao);

        servico.marcarComoLida(notifId);

        assertTrue(notificacao.isLida());

        verify(repositorio).buscarPorId(notifId);
        verify(repositorio).atualizar(notificacao);
    }

    @Test
    @DisplayName("Deve contar notificações não lidas")
    public void testContarNotificacoesNaoLidas() {

        String email = "usuario@teste.com";

        List<Notificacao> notificacoes = new ArrayList<>();
        notificacoes.add(new Notificacao("1", email, TipoNotificacao.SISTEMA, "Msg1"));
        notificacoes.add(new Notificacao("2", email, TipoNotificacao.SISTEMA, "Msg2"));

        when(repositorio.buscarNaoLidasPorDestinatario(email))
                .thenReturn(notificacoes);

        int count = servico.contarNotificacoesNaoLidas(email);

        assertEquals(2, count);

        verify(repositorio).buscarNaoLidasPorDestinatario(email);
    }
}