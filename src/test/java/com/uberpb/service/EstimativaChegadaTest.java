package com.uberpb.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EstimativaChegadaTest {

    @Test
    void deveCriarEstimativaComConstrutorPadrao() {

        EstimativaChegada estimativa = new EstimativaChegada();

        assertEquals(0.0, estimativa.getDistanciaKm());
        assertEquals(0, estimativa.getTempoEstimadoMinutos());
    }

    @Test
    void deveCriarEstimativaComValores() {

        EstimativaChegada estimativa = new EstimativaChegada(10.5, 20);

        assertEquals(10.5, estimativa.getDistanciaKm());
        assertEquals(20, estimativa.getTempoEstimadoMinutos());
    }

    @Test
    void naoDevePermitirValoresNegativos() {

        EstimativaChegada estimativa = new EstimativaChegada(-5, -10);

        assertEquals(0.0, estimativa.getDistanciaKm());
        assertEquals(0, estimativa.getTempoEstimadoMinutos());
    }

    @Test
    void deveCalcularTempoPorVelocidadeMedia() {

        EstimativaChegada estimativa = EstimativaChegada.porVelocidadeMedia(30, 60);

        assertEquals(30, estimativa.getDistanciaKm());
        assertEquals(30, estimativa.getTempoEstimadoMinutos());
    }

    @Test
    void deveUsarVelocidadePadraoQuandoVelocidadeInvalida() {

        EstimativaChegada estimativa = EstimativaChegada.porVelocidadeMedia(30, 0);

        assertEquals(30, estimativa.getDistanciaKm());
        assertEquals(60, estimativa.getTempoEstimadoMinutos());
    }

    @Test
    void settersDevemAtualizarValores() {

        EstimativaChegada estimativa = new EstimativaChegada();

        estimativa.setDistanciaKm(15);
        estimativa.setTempoEstimadoMinutos(25);

        assertEquals(15, estimativa.getDistanciaKm());
        assertEquals(25, estimativa.getTempoEstimadoMinutos());
    }

    @Test
    void settersNaoDevemPermitirValoresNegativos() {

        EstimativaChegada estimativa = new EstimativaChegada();

        estimativa.setDistanciaKm(-10);
        estimativa.setTempoEstimadoMinutos(-5);

        assertEquals(0.0, estimativa.getDistanciaKm());
        assertEquals(0, estimativa.getTempoEstimadoMinutos());
    }
}
