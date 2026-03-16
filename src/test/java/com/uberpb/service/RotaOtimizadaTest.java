package com.uberpb.service;

import com.uberpb.model.Localizacao;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RotaOtimizadaTest {

    @Test
    void deveCriarRotaOtimizadaComValoresCorretos() {

        Localizacao origem = new Localizacao(-7.23, -35.88);
        Localizacao destino = new Localizacao(-7.25, -35.90);

        List<Localizacao> pontos = List.of(
                origem,
                new Localizacao(-7.24, -35.89),
                destino
        );

        RotaOtimizada rota = new RotaOtimizada(
                origem,
                destino,
                12.5,
                18.0,
                pontos,
                15.0
        );

        assertEquals(origem, rota.getOrigem());
        assertEquals(destino, rota.getDestino());
        assertEquals(12.5, rota.getDistanciaKm());
        assertEquals(18.0, rota.getTempoEstimadoMinutos());
        assertEquals(pontos, rota.getPontosRota());
        assertEquals(15.0, rota.getEconomiaTempoPercentual());
    }

    @Test
    void devePermitirListaVaziaDePontos() {

        Localizacao origem = new Localizacao(-7.20, -35.80);
        Localizacao destino = new Localizacao(-7.30, -35.90);

        RotaOtimizada rota = new RotaOtimizada(
                origem,
                destino,
                5.0,
                10.0,
                List.of(),
                0
        );

        assertTrue(rota.getPontosRota().isEmpty());
    }
}