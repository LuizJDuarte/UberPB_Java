package com.uberpb.test;

import com.uberpb.model.AvaliacaoMotorista;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AvaliacaoMotoristaTest {

    @Test
    void testCriacaoEGets() {
        AvaliacaoMotorista a = new AvaliacaoMotorista(
                "corrida123",
                "passageiro@uberpb.com",
                "motorista@uberpb.com",
                5,
                "Ótimo motorista!"
        );

        assertEquals("passageiro@uberpb.com", a.getPassageiroEmail());
        assertEquals("motorista@uberpb.com", a.getMotoristaEmail());
        assertEquals(5, a.getRating());
        assertEquals("Ótimo motorista!", a.getComentario());
        assertEquals("corrida123", a.getCorridaId());
    }

    @Test
    void testToStringParaPersistencia() {
        AvaliacaoMotorista a = new AvaliacaoMotorista(
                "corrida123",
                "passageiro@uberpb.com",
                "motorista@uberpb.com",
                4,
                "Bom motorista"
        );

        String persistencia = a.toStringParaPersistencia();
        assertTrue(persistencia.startsWith("MOTORISTA_TO_PASSAGEIRO|"));
        assertTrue(persistencia.contains("corrida123"));
        assertTrue(persistencia.contains("passageiro@uberpb.com"));
        assertTrue(persistencia.contains("motorista@uberpb.com"));
        assertTrue(persistencia.contains("4"));
        assertTrue(persistencia.contains("Bom motorista"));
    }

    @Test
    void testFromStringParaPersistencia() {
        String linha = "MOTORISTA_TO_PASSAGEIRO|id|corrida123|passageiro@uberpb.com|motorista@uberpb.com|5|Excelente|2026-02-21T12:00";
        AvaliacaoMotorista a = AvaliacaoMotorista.fromStringParaPersistencia(linha);

        assertEquals("corrida123", a.getCorridaId());
        assertEquals("passageiro@uberpb.com", a.getPassageiroEmail());
        assertEquals("motorista@uberpb.com", a.getMotoristaEmail());
        assertEquals(5, a.getRating());
        assertEquals("Excelente", a.getComentario());
    }

    @Test
    void testToStringAndFromStringConsistency() {
        AvaliacaoMotorista a1 = new AvaliacaoMotorista(
                "corridaXYZ",
                "p@u.com",
                "m@u.com",
                3,
                "Ok"
        );

        String persistencia = a1.toStringParaPersistencia();
        AvaliacaoMotorista a2 = AvaliacaoMotorista.fromStringParaPersistencia(persistencia);

        assertEquals(a1.getCorridaId(), a2.getCorridaId());
        assertEquals(a1.getPassageiroEmail(), a2.getPassageiroEmail());
        assertEquals(a1.getMotoristaEmail(), a2.getMotoristaEmail());
        assertEquals(a1.getRating(), a2.getRating());
        assertEquals(a1.getComentario(), a2.getComentario());
    }
}