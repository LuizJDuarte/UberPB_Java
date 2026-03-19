package com.uberpb.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapaASCIITest {

    @Test
    void deveExibirTrilhoNoInicio() {
        String resultado = MapaASCII.trilho("A", "B", 0);
        assertTrue(resultado.contains("|o-----------------------------|"));
        assertTrue(resultado.contains("A"));
        assertTrue(resultado.contains("B"));
    }

    @Test
    void deveExibirTrilhoNoMeio() {
        String resultado = MapaASCII.trilho("Origem", "Destino", 50);
        // posição aproximada no meio do trilho de 30 chars
        assertTrue(resultado.contains("|--------------o---------------|"));
    }

    @Test
    void deveExibirTrilhoNoFim() {
        String resultado = MapaASCII.trilho("Origem", "Destino", 100);
        assertTrue(resultado.contains("|-----------------------------o|"));
    }

    @Test
    void deveEncurtarStringsLongas() {
        String origem = "OrigemMuitoLongaQueDeveSerEncurtadaAqui";
        String destino = "DestinoMuitoLongoQueTambemDeveSerEncurtadoAli";
        String resultado = MapaASCII.trilho(origem, destino, 50);

        // verifica se as strings foram encurtadas
        assertTrue(resultado.contains("..."));
        assertTrue(resultado.contains("o")); // trilho ainda tem o carro
    }

    @Test
    void deveTratarParametrosNulos() {
        String resultado = MapaASCII.trilho(null, null, 50);
        assertNotNull(resultado);
        assertTrue(resultado.contains("Origem"));
        assertTrue(resultado.contains("Destino"));
        assertTrue(resultado.contains("o"));
    }
}