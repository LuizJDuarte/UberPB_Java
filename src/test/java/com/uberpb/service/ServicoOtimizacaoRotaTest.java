package com.uberpb.service;

import com.uberpb.model.Localizacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicoOtimizacaoRotaTest {

    @Test
    void deveCalcularRotaOtimizada() {
        ServicoOtimizacaoRota servico = new ServicoOtimizacaoRota();

        Localizacao origem = new Localizacao(0, 0);
        Localizacao destino = new Localizacao(1, 1);

        RotaOtimizada rota = servico.calcularRotaOtimizada(origem, destino);

        assertNotNull(rota);
        assertEquals(origem, rota.getOrigem());
        assertEquals(destino, rota.getDestino());

        assertTrue(rota.getDistanciaKm() > 0);
        assertTrue(rota.getTempoEstimadoMinutos() > 0);
        assertNotNull(rota.getPontosRota());
        assertFalse(rota.getPontosRota().isEmpty());
    }

    @Test
    void deveGerarPontosNaRota() {
        ServicoOtimizacaoRota servico = new ServicoOtimizacaoRota();

        Localizacao origem = new Localizacao(0, 0);
        Localizacao destino = new Localizacao(10, 10);

        RotaOtimizada rota = servico.calcularRotaOtimizada(origem, destino);

        assertNotNull(rota.getPontosRota());
        assertTrue(rota.getPontosRota().size() >= 2);

        assertEquals(origem, rota.getPontosRota().get(0));
        assertEquals(destino, rota.getPontosRota()
                .get(rota.getPontosRota().size() - 1));
    }

    @Test
    void deveCalcularEconomiaTempo() {
        ServicoOtimizacaoRota servico = new ServicoOtimizacaoRota();

        Localizacao origem = new Localizacao(0, 0);
        Localizacao destino = new Localizacao(2, 2);

        RotaOtimizada rota = servico.calcularRotaOtimizada(origem, destino);

        assertTrue(rota.getEconomiaTempoPercentual() >= 0);
    }

    @Test
    void deveRetornarNullParaCorrida() {
        ServicoOtimizacaoRota servico = new ServicoOtimizacaoRota();

        RotaOtimizada rota = servico.calcularRotaParaCorrida("123");

        assertNull(rota);
    }

    @Test
    void deveVariarDistanciaComEntradasDiferentes() {
        ServicoOtimizacaoRota servico = new ServicoOtimizacaoRota();

        Localizacao origem1 = new Localizacao(0, 0);
        Localizacao destino1 = new Localizacao(1, 1);

        Localizacao origem2 = new Localizacao(0, 0);
        Localizacao destino2 = new Localizacao(5, 5);

        RotaOtimizada rota1 = servico.calcularRotaOtimizada(origem1, destino1);
        RotaOtimizada rota2 = servico.calcularRotaOtimizada(origem2, destino2);

        assertTrue(rota2.getDistanciaKm() > rota1.getDistanciaKm());
    }
}