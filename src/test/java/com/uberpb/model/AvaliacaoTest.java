package com.uberpb.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AvaliacaoTest {

    // Classe concreta fake só para teste
    static class AvaliacaoFake extends Avaliacao {

        public AvaliacaoFake(String corridaId, int rating, String comentario) {
            super(corridaId, rating, comentario);
        }

        @Override
        public String toStringParaPersistencia() {
            return "persistencia";
        }
    }

    @Test
    void deveCriarAvaliacaoCorretamente() {
        Avaliacao avaliacao = new AvaliacaoFake("corrida123", 4, "Muito bom");

        assertNotNull(avaliacao.getId());
        assertEquals("corrida123", avaliacao.getCorridaId());
        assertEquals(4, avaliacao.getRating());
        assertEquals("Muito bom", avaliacao.getComentario());
        assertNotNull(avaliacao.getDataAvaliacao());
    }

    @Test
    void deveLimitarRatingParaMinimoUm() {
        Avaliacao avaliacao = new AvaliacaoFake("corrida123", 0, "Teste");

        assertEquals(1, avaliacao.getRating());
    }

    @Test
    void deveLimitarRatingParaMaximoCinco() {
        Avaliacao avaliacao = new AvaliacaoFake("corrida123", 10, "Teste");

        assertEquals(5, avaliacao.getRating());
    }

    @Test
    void deveSubstituirComentarioNullPorVazio() {
        Avaliacao avaliacao = new AvaliacaoFake("corrida123", 3, null);

        assertEquals("", avaliacao.getComentario());
    }

    @Test
    void dataDeveSerProximaDoMomentoAtual() {
        LocalDateTime antes = LocalDateTime.now();

        Avaliacao avaliacao = new AvaliacaoFake("corrida123", 3, "Teste");

        LocalDateTime depois = LocalDateTime.now();

        assertTrue(
            !avaliacao.getDataAvaliacao().isBefore(antes) &&
            !avaliacao.getDataAvaliacao().isAfter(depois)
        );
    }
}