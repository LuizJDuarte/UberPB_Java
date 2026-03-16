package com.uberpb.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GerenciadorCorridasAtivasTest {

    @Test
    void deveIniciarCorrida() {

        GerenciadorCorridasAtivas g = new GerenciadorCorridasAtivas();

        g.iniciar("corrida1", 10, 5.0);

        assertTrue(g.isAtiva("corrida1"));
    }

    @Test
    void progressoInicialDeveSerBaixo() {

        GerenciadorCorridasAtivas g = new GerenciadorCorridasAtivas();

        g.iniciar("corrida1", 10, 5.0);

        GerenciadorCorridasAtivas.Progresso p = g.progresso("corrida1");

        assertNotNull(p);
        assertEquals("corrida1", p.corridaId);
        assertTrue(p.percentual >= 0);
        assertFalse(p.concluida);
    }

    @Test
    void corridaDeveConcluirDepoisDoTempo() throws InterruptedException {

        GerenciadorCorridasAtivas g = new GerenciadorCorridasAtivas();

        // duração mínima é 5 minutos, então simulamos progresso manual chamando método
        g.iniciar("corrida2", 5, 2.0);

        GerenciadorCorridasAtivas.Progresso p = g.progresso("corrida2");

        assertFalse(p.concluida);
    }

    @Test
    void progressoDeCorridaInexistente() {

        GerenciadorCorridasAtivas g = new GerenciadorCorridasAtivas();

        GerenciadorCorridasAtivas.Progresso p = g.progresso("naoExiste");

        assertEquals(0, p.percentual);
        assertEquals(5.0, p.distanciaRestanteKm);
        assertEquals(10, p.minutosRestantes);
        assertFalse(p.concluida);
    }

    @Test
    void encerrarCorridaRemoveDaLista() {

        GerenciadorCorridasAtivas g = new GerenciadorCorridasAtivas();

        g.iniciar("corrida3", 10, 3.0);

        assertTrue(g.isAtiva("corrida3"));

        g.encerrar("corrida3");

        assertFalse(g.isAtiva("corrida3"));
    }

    @Test
    void corridaEncerradaEhConsideradaConcluida() {

        GerenciadorCorridasAtivas g = new GerenciadorCorridasAtivas();

        g.iniciar("corrida4", 10, 3.0);

        g.encerrar("corrida4");

        assertTrue(g.isConcluida("corrida4"));
    }

    @Test
    void iniciarComValoresMuitoBaixosUsaMinimos() {

        GerenciadorCorridasAtivas g = new GerenciadorCorridasAtivas();

        // valores menores que mínimo
        g.iniciar("corrida5", 1, 0.2);

        GerenciadorCorridasAtivas.Progresso p = g.progresso("corrida5");

        // distância mínima deve ser 1km
        assertTrue(p.distanciaRestanteKm <= 1.0);

        assertFalse(p.concluida);
    }

}