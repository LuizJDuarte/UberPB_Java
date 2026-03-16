package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Localizacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EstimativaCorridaTest {

    private EstimativaCorrida estimativa;

    @BeforeEach
    void setUp() {
        estimativa = new EstimativaCorrida(0, 0, 0);
    }

    @Test
    void deveCalcularEstimativaPorCoordenadas() {

        Localizacao origem = new Localizacao(-7.23072, -35.8817);
        Localizacao destino = new Localizacao(-7.25000, -35.9000);

        EstimativaCorrida resultado = estimativa.calcularPorCoordenadas(
                origem, destino, CategoriaVeiculo.UBERX);

        assertNotNull(resultado);
        assertTrue(resultado.getDistanciaKm() > 0);
        assertTrue(resultado.getMinutos() >= 5);
        assertTrue(resultado.getPreco() >= 8.0);
    }

    @Test
    void deveCalcularEstimativaPorEnderecos() {

        EstimativaCorrida resultado = estimativa.calcularPorEnderecos(
                "Rua A", "Rua B", CategoriaVeiculo.UBERX);

        assertNotNull(resultado);
        assertTrue(resultado.getDistanciaKm() > 0);
        assertTrue(resultado.getPreco() >= 8.0);
    }

    @Test
    void deveRetornarValoresPadraoQuandoEnderecoNulo() {

        EstimativaCorrida resultado = estimativa.calcularPorEnderecos(
                null, null, CategoriaVeiculo.UBERX);

        assertEquals(5.0, resultado.getDistanciaKm());
        assertEquals(10, resultado.getMinutos());
        assertEquals(25.0, resultado.getPreco());
    }

    @Test
    void precoDeveSerMaiorParaCategoriaBlack() {

        // distância maior para garantir que o fator da categoria influencie
        Localizacao origem = new Localizacao(-7.23, -35.88);
        Localizacao destino = new Localizacao(-7.30, -35.95);

        EstimativaCorrida uberX = estimativa.calcularPorCoordenadas(
                origem, destino, CategoriaVeiculo.UBERX);

        EstimativaCorrida black = estimativa.calcularPorCoordenadas(
                origem, destino, CategoriaVeiculo.BLACK);

        assertTrue(black.getPreco() > uberX.getPreco());
    }

    @Test
    void distanciaMinimaDeveSerMaiorOuIgualAUmKm() {

        Localizacao origem = new Localizacao(-7.23, -35.88);
        Localizacao destino = new Localizacao(-7.2301, -35.8801);

        EstimativaCorrida resultado = estimativa.calcularPorCoordenadas(
                origem, destino, CategoriaVeiculo.UBERX);

        assertTrue(resultado.getDistanciaKm() >= 1.0);
    }
}
