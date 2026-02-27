package com.uberpb.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o modelo Notificacao (RF22)
 */
@DisplayName("Testes do Modelo Notificação - RF22")
public class NotificacaoTest {

    @Test
    @DisplayName("Deve criar notificação corretamente")
    public void testCriarNotificacao() {
        Notificacao notif = new Notificacao(
                "123",
                "usuario@teste.com",
                TipoNotificacao.NOVO_PEDIDO_RESTAURANTE,
                "Novo pedido recebido");

        assertEquals("123", notif.getId());
        assertEquals("usuario@teste.com", notif.getDestinatarioEmail());
        assertEquals(TipoNotificacao.NOVO_PEDIDO_RESTAURANTE, notif.getTipo());
        assertEquals("Novo pedido recebido", notif.getMensagem());
        assertFalse(notif.isLida());
        assertNotNull(notif.getDataHora());
    }

    @Test
    @DisplayName("Deve marcar notificação como lida")
    public void testMarcarComoLida() {
        Notificacao notif = new Notificacao(
                "1",
                "user@test.com",
                TipoNotificacao.SISTEMA,
                "Teste");

        assertFalse(notif.isLida());

        notif.marcarComoLida();

        assertTrue(notif.isLida());
    }

    @Test
    @DisplayName("Deve converter para string de persistência corretamente")
    public void testToStringParaPersistencia() {
        Notificacao notif = new Notificacao(
                "abc123",
                "user@test.com",
                TipoNotificacao.PEDIDO_DISPONIVEL_ENTREGADOR,
                "Mensagem de teste");

        String persistencia = notif.toStringParaPersistencia();

        assertNotNull(persistencia);
        assertTrue(persistencia.contains("abc123"));
        assertTrue(persistencia.contains("user@test.com"));
        assertTrue(persistencia.contains("PEDIDO_DISPONIVEL_ENTREGADOR"));
        assertTrue(persistencia.contains("Mensagem de teste"));
        assertTrue(persistencia.contains("false")); // não lida
    }

    @Test
    @DisplayName("Deve restaurar notificação a partir de string")
    public void testFromString() {
        String linha = "id123,user@test.com,SISTEMA,Teste mensagem,2024-01-01T10:00:00,false";

        Notificacao notif = Notificacao.fromString(linha);

        assertNotNull(notif);
        assertEquals("id123", notif.getId());
        assertEquals("user@test.com", notif.getDestinatarioEmail());
        assertEquals(TipoNotificacao.SISTEMA, notif.getTipo());
        assertEquals("Teste mensagem", notif.getMensagem());
        assertFalse(notif.isLida());
    }

    @Test
    @DisplayName("Deve retornar null para string inválida")
    public void testFromStringInvalida() {
        assertNull(Notificacao.fromString(null));
        assertNull(Notificacao.fromString(""));
        assertNull(Notificacao.fromString("campos,insuficientes"));
    }

    @Test
    @DisplayName("toString deve incluir informações principais")
    public void testToString() {
        Notificacao notif = new Notificacao(
                "1",
                "user@test.com",
                TipoNotificacao.PEDIDO_ACEITO,
                "Pedido aceito pelo entregador");

        String str = notif.toString();

        assertNotNull(str);
        assertTrue(str.contains("NÃO LIDA"));
        assertTrue(str.contains("PEDIDO_ACEITO"));
        assertTrue(str.contains("Pedido aceito pelo entregador"));
    }
}
