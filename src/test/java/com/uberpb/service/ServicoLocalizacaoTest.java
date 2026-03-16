package com.uberpb.service;

import com.uberpb.model.Localizacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoLocalizacaoTest {

    private final ServicoLocalizacao servico = new ServicoLocalizacao();

    @Test
    void deveGeocodificarEndereco() {

        Localizacao loc = servico.geocodificar("Rua A, 123");

        assertNotNull(loc);
        assertTrue(loc.latitude() >= -90 && loc.latitude() <= 90);
        assertTrue(loc.longitude() >= -180 && loc.longitude() <= 180);
    }

    @Test
    void geocodificarEnderecoNullRetornaZero() {

        Localizacao loc = servico.geocodificar(null);

        assertEquals(0, loc.latitude());
        assertEquals(0, loc.longitude());
    }

    @Test
    void geocodificarEnderecoVazioRetornaZero() {

        Localizacao loc = servico.geocodificar("");

        assertEquals(0, loc.latitude());
        assertEquals(0, loc.longitude());
    }

    @Test
    void deveCalcularDistanciaEntreDoisPontos() {

        Localizacao a = new Localizacao(10, 10);
        Localizacao b = new Localizacao(11, 11);

        double distancia = servico.distanciaKm(a, b);

        assertTrue(distancia > 0);
    }

    @Test
    void distanciaComLocalizacaoNullRetornaZero() {

        Localizacao a = new Localizacao(10, 10);

        double distancia = servico.distanciaKm(a, null);

        assertEquals(0, distancia);
    }

    @Test
    void distanciaMesmoPontoDeveSerZero() {

        Localizacao a = new Localizacao(10, 10);

        double distancia = servico.distanciaKm(a, a);

        assertEquals(0, distancia);
    }

    @Test
    void deveObterLocalizacaoAtualUsuario() {

        Localizacao loc = servico.obterLocalizacaoAtual("user@email.com");

        assertNotNull(loc);
        assertTrue(loc.latitude() >= -45 && loc.latitude() <= 45);
        assertTrue(loc.longitude() >= -90 && loc.longitude() <= 90);
    }

    @Test
    void obterLocalizacaoAtualEmailNull() {

        Localizacao loc = servico.obterLocalizacaoAtual(null);

        assertEquals(0, loc.latitude());
        assertEquals(0, loc.longitude());
    }
}
