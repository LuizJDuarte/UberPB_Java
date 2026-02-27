package com.uberpb.test;

import com.uberpb.model.AvaliacaoPassageiro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvaliacaoPassageiroTest {

    @Test
    void testCriacaoEGets() {
        AvaliacaoPassageiro a = new AvaliacaoPassageiro(
                "corrida123",
                "motorista@uberpb.com",
                "passageiro@uberpb.com",
                5,
                "Ótimo passageiro!"
        );

        assertEquals("motorista@uberpb.com", a.getMotoristaEmail());
        assertEquals("passageiro@uberpb.com", a.getPassageiroEmail());
        assertEquals(5, a.getRating());
        assertEquals("Ótimo passageiro!", a.getComentario());
        assertEquals("corrida123", a.getCorridaId());
    }

    @Test
    void testToStringParaPersistencia() {
        AvaliacaoPassageiro a = new AvaliacaoPassageiro(
                "corrida123",
                "motorista@uberpb.com",
                "passageiro@uberpb.com",
                4,
                "Bom passageiro"
        );

        String persistencia = a.toStringParaPersistencia();
        assertTrue(persistencia.startsWith("PASSAGEIRO_TO_MOTORISTA|"));
        assertTrue(persistencia.contains("corrida123"));
        assertTrue(persistencia.contains("passageiro@uberpb.com"));
        assertTrue(persistencia.contains("motorista@uberpb.com"));
        assertTrue(persistencia.contains("4"));
        assertTrue(persistencia.contains("Bom passageiro"));
    }

    @Test
    void testFromStringParaPersistencia() {
        String linha = "PASSAGEIRO_TO_MOTORISTA|id|corrida123|motorista@uberpb.com|passageiro@uberpb.com|5|Excelente|2026-02-21T12:00";
        AvaliacaoPassageiro a = AvaliacaoPassageiro.fromStringParaPersistencia(linha);

        assertEquals("corrida123", a.getCorridaId());
        assertEquals("motorista@uberpb.com", a.getMotoristaEmail());
        assertEquals("passageiro@uberpb.com", a.getPassageiroEmail());
        assertEquals(5, a.getRating());
        assertEquals("Excelente", a.getComentario());
    }

    @Test
    void testToStringAndFromStringConsistency() {
        AvaliacaoPassageiro a1 = new AvaliacaoPassageiro(
                "corridaXYZ",
                "m@u.com",
                "p@u.com",
                3,
                "Ok"
        );

        String persistencia = a1.toStringParaPersistencia();
        AvaliacaoPassageiro a2 = AvaliacaoPassageiro.fromStringParaPersistencia(persistencia);

        assertEquals(a1.getCorridaId(), a2.getCorridaId());
        assertEquals(a1.getMotoristaEmail(), a2.getMotoristaEmail());
        assertEquals(a1.getPassageiroEmail(), a2.getPassageiroEmail());
        assertEquals(a1.getRating(), a2.getRating());
        assertEquals(a1.getComentario(), a2.getComentario());
    }
}