package com.uberpb.test;

import com.uberpb.model.Localizacao;
import com.uberpb.model.LocalizacaoTempoReal;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalizacaoTempoRealTest {

    @Test
    void testCriacaoELerCampos() {
        Localizacao loc = new Localizacao(12.34, 56.78);
        LocalDateTime agora = LocalDateTime.now();

        LocalizacaoTempoReal ltr = new LocalizacaoTempoReal(
                "motorista@uberpb.com",
                "corrida123",
                loc,
                agora,
                3.5,
                10
        );

        // Testando getters
        assertEquals("motorista@uberpb.com", ltr.getMotoristaEmail());
        assertEquals("corrida123", ltr.getCorridaId());
        assertEquals(loc, ltr.getLocalizacao());
        assertEquals(agora, ltr.getTimestamp());
        assertEquals(3.5, ltr.getDistanciaPassageiroKm());
        assertEquals(10, ltr.getTempoEstimadoMinutos());
    }

    @Test
    void testCamposNaoNulos() {
        Localizacao loc = new Localizacao(0.0, 0.0);
        LocalDateTime timestamp = LocalDateTime.now();

        LocalizacaoTempoReal ltr = new LocalizacaoTempoReal(
                "a@b.com",
                "id",
                loc,
                timestamp,
                0.0,
                0
        );

        assertNotNull(ltr.getMotoristaEmail());
        assertNotNull(ltr.getCorridaId());
        assertNotNull(ltr.getLocalizacao());
        assertNotNull(ltr.getTimestamp());
    }
}