package com.uberpb.test;

import com.uberpb.model.ProgressoCorrida;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgressoCorridaTest {

    @Test
    void testConstrutorComTodosParametros() {
        ProgressoCorrida p = new ProgressoCorrida("123", 120, 10.0, 30);
        assertEquals("123", p.getCorridaId());
        assertEquals(120, p.getDuracaoSegundos());
        assertEquals(10.0, p.getDistanciaKm());
        assertEquals(30, p.getSegundosDecorridos());
    }

    @Test
    void testConstrutorSemSegundosDecorridos() {
        ProgressoCorrida p = new ProgressoCorrida("123", 120, 10.0);
        assertEquals(0, p.getSegundosDecorridos());
    }

    @Test
    void testValoresInvalidosConstrutor() {
        ProgressoCorrida p = new ProgressoCorrida("123", -50, -5.0, -10);
        assertEquals(1, p.getDuracaoSegundos()); // minimo 1
        assertEquals(0.01, p.getDistanciaKm()); // minimo 0.01
        assertEquals(0, p.getSegundosDecorridos());
    }

    @Test
    void testSetSegundosDecorridos() {
        ProgressoCorrida p = new ProgressoCorrida("123", 100, 10.0);
        p.setSegundosDecorridos(50);
        assertEquals(50, p.getSegundosDecorridos());

        // menor que 0
        p.setSegundosDecorridos(-10);
        assertEquals(0, p.getSegundosDecorridos());

        // maior que duracao
        p.setSegundosDecorridos(200);
        assertEquals(100, p.getSegundosDecorridos());
    }

    @Test
    void testPercentual() {
        ProgressoCorrida p = new ProgressoCorrida("123", 100, 10.0, 50);
        assertEquals(50, p.getPercentual());

        p.setSegundosDecorridos(0);
        assertEquals(0, p.getPercentual());

        p.setSegundosDecorridos(100);
        assertEquals(100, p.getPercentual());
    }

    @Test
    void testMinutosRestantes() {
        ProgressoCorrida p = new ProgressoCorrida("123", 125, 10.0, 65);
        assertEquals(1, p.getMinutosRestantes()); // 60 segundos restantes = 1 minuto
    }

    @Test
    void testDistanciaRestanteKm() {
        ProgressoCorrida p = new ProgressoCorrida("123", 100, 10.0, 30);
        double restante = p.getDistanciaRestanteKm();
        assertEquals(7.0, restante, 0.01); // 30% do tempo decorrido, 70% restante
    }

    @Test
    void testIsConcluida() {
        ProgressoCorrida p = new ProgressoCorrida("123", 100, 10.0, 100);
        assertTrue(p.isConcluida());

        p.setSegundosDecorridos(99);
        assertFalse(p.isConcluida());
    }
}