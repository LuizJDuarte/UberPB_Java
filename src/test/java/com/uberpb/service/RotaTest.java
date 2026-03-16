package com.uberpb.service;

import com.uberpb.model.Localizacao;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RotaTest {

    @Test
    void deveCriarRotaComValoresCorretos() {

        Localizacao p1 = new Localizacao(-7.23, -35.88);
        Localizacao p2 = new Localizacao(-7.24, -35.89);

        List<Localizacao> pontos = List.of(p1, p2);

        Rota rota = new Rota(10.5, 15.0, pontos);

        assertEquals(10.5, rota.getDistancia());
        assertEquals(15.0, rota.getTempoEstimado());
        assertEquals(pontos, rota.getPontos());
    }

    @Test
    void deveRetornarListaDePontos() {

        Localizacao p1 = new Localizacao(-7.10, -35.20);
        Localizacao p2 = new Localizacao(-7.11, -35.21);
        Localizacao p3 = new Localizacao(-7.12, -35.22);

        List<Localizacao> pontos = List.of(p1, p2, p3);

        Rota rota = new Rota(5.0, 8.0, pontos);

        assertEquals(3, rota.getPontos().size());
        assertEquals(p1, rota.getPontos().get(0));
    }

    @Test
    void devePermitirListaVaziaDePontos() {

        List<Localizacao> pontos = List.of();

        Rota rota = new Rota(2.0, 3.0, pontos);

        assertTrue(rota.getPontos().isEmpty());
    }

    @Test
    void devePermitirDistanciaETempoZero() {

        List<Localizacao> pontos = List.of();

        Rota rota = new Rota(0.0, 0.0, pontos);

        assertEquals(0.0, rota.getDistancia());
        assertEquals(0.0, rota.getTempoEstimado());
    }
}