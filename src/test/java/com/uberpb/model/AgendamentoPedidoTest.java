package com.uberpb.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AgendamentoPedidoTest {

    @Test
    void deveCriarERecuperarData() {
        LocalDateTime data = LocalDateTime.now().plusHours(1);
        AgendamentoPedido ag = new AgendamentoPedido(data);

        assertEquals(data, ag.getDataHoraAgendamento());
    }

    @Test
    void deveAlterarData() {
        AgendamentoPedido ag = new AgendamentoPedido(null);

        LocalDateTime novaData = LocalDateTime.now().plusDays(1);
        ag.setDataHoraAgendamento(novaData);

        assertEquals(novaData, ag.getDataHoraAgendamento());
    }

    @Test
    void deveSerInvalidoQuandoDataNull() {
        AgendamentoPedido ag = new AgendamentoPedido(null);

        assertFalse(ag.isValido());
        assertEquals("Data e hora não podem estar vazias.", ag.getErroValidacao());
    }

    @Test
    void deveSerInvalidoQuandoDataNoPassado() {
        LocalDateTime passado = LocalDateTime.now().minusHours(1);
        AgendamentoPedido ag = new AgendamentoPedido(passado);

        assertFalse(ag.isValido());
        assertEquals("A data e hora devem ser no futuro.", ag.getErroValidacao());
    }

    @Test
    void deveSerValidoQuandoDataNoFuturo() {
        LocalDateTime futuro = LocalDateTime.now().plusHours(2);
        AgendamentoPedido ag = new AgendamentoPedido(futuro);

        assertTrue(ag.isValido());
        assertNull(ag.getErroValidacao());
    }

    @Test
    void deveFormatarParaPersistencia() {
        LocalDateTime data = LocalDateTime.of(2026, 3, 18, 15, 30);
        AgendamentoPedido ag = new AgendamentoPedido(data);

        String resultado = ag.formatarParaPersistencia();

        assertEquals("18/03/2026 15:30", resultado);
    }

    @Test
    void deveCriarAPartirDeStringValida() {
        String dataStr = "18/03/2026 15:30";

        AgendamentoPedido ag = AgendamentoPedido.fromString(dataStr);

        assertNotNull(ag);
        assertEquals(18, ag.getDataHoraAgendamento().getDayOfMonth());
        assertEquals(3, ag.getDataHoraAgendamento().getMonthValue());
        assertEquals(2026, ag.getDataHoraAgendamento().getYear());
    }

    @Test
    void deveRetornarNullParaStringInvalida() {
        AgendamentoPedido ag = AgendamentoPedido.fromString("data invalida");

        assertNull(ag);
    }

    @Test
    void deveRetornarNullParaStringVazia() {
        AgendamentoPedido ag = AgendamentoPedido.fromString(" ");

        assertNull(ag);
    }

    @Test
    void deveRetornarNullParaStringNull() {
        AgendamentoPedido ag = AgendamentoPedido.fromString(null);

        assertNull(ag);
    }

    @Test
    void deveGerarToStringCorretamente() {
        LocalDateTime data = LocalDateTime.of(2026, 3, 18, 15, 30);
        AgendamentoPedido ag = new AgendamentoPedido(data);

        String texto = ag.toString();

        assertTrue(texto.contains("Agendado para"));
        assertTrue(texto.contains("18/03/2026 15:30"));
    }
}