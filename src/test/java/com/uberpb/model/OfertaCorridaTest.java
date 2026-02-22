package com.uberpb.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfertaCorridaTest {

    @Test
    void deveCriarNovaOfertaComStatusPendente() {
        OfertaCorrida oferta = OfertaCorrida.nova("corrida123", "motorista@test.com");

        assertNotNull(oferta.getId());
        assertEquals("corrida123", oferta.getCorridaId());
        assertEquals("motorista@test.com", oferta.getMotoristaEmail());
        assertEquals(OfertaStatus.PENDENTE, oferta.getStatus());
    }

    @Test
    void deveAlterarStatusCorretamente() {
        OfertaCorrida oferta = OfertaCorrida.nova("corrida123", "motorista@test.com");

        oferta.setStatus(OfertaStatus.ACEITA);

        assertEquals(OfertaStatus.ACEITA, oferta.getStatus());
    }

    @Test
    void deveGerarStringDePersistenciaCorreta() {
        OfertaCorrida oferta = new OfertaCorrida(
                "id1",
                "corrida123",
                "motorista@test.com",
                OfertaStatus.RECUSADA
        );

        String esperado = "id1|corrida123|motorista@test.com|RECUSADA";

        assertEquals(esperado, oferta.toStringParaPersistencia());
    }

    @Test
    void deveReconstruirObjetoAPartirDaString() {
        String linha = "id1|corrida123|motorista@test.com|ACEITA";

        OfertaCorrida oferta = OfertaCorrida.fromStringParaPersistencia(linha);

        assertEquals("id1", oferta.getId());
        assertEquals("corrida123", oferta.getCorridaId());
        assertEquals("motorista@test.com", oferta.getMotoristaEmail());
        assertEquals(OfertaStatus.ACEITA, oferta.getStatus());
    }

    @Test
    void deveLancarExcecaoSeParametrosNulos() {
        assertThrows(NullPointerException.class, () ->
                new OfertaCorrida(null, "corrida", "motorista", OfertaStatus.PENDENTE)
        );

        assertThrows(NullPointerException.class, () ->
                new OfertaCorrida("id", null, "motorista", OfertaStatus.PENDENTE)
        );

        assertThrows(NullPointerException.class, () ->
                new OfertaCorrida("id", "corrida", null, OfertaStatus.PENDENTE)
        );

        assertThrows(NullPointerException.class, () ->
                new OfertaCorrida("id", "corrida", "motorista", null)
        );
    }
}