package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Localizacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CalculadoraPrecoCorridaTest {

    @Test
    void deveCalcularPreco() {

        CategoriaVeiculo categoria = mock(CategoriaVeiculo.class);
        when(categoria.getMultiplicadorPreco()).thenReturn(1.0);

        double preco = CalculadoraPrecoCorrida.calcularPreco(10, 20, categoria);

        assertTrue(preco > 0);
    }

    @Test
    void deveEstimarTempoMinutos() {

        double tempo = CalculadoraPrecoCorrida.estimarTempoMinutos(30);

        // 30km / 30kmh * 60 = 60 minutos
        assertEquals(60, tempo, 0.1);
    }

    @Test
    void deveEstimarDistanciaKm() {

        double distancia = CalculadoraPrecoCorrida.estimarDistanciaKm("Rua A", "Rua B");

        assertTrue(distancia >= 3.0);
        assertTrue(distancia <= 15.0);
    }

    @Test
    void deveCalcularDistanciaKmEntreLocalizacoes() {

        Localizacao origem = new Localizacao(-7.22, -35.88);
        Localizacao destino = new Localizacao(-7.25, -35.90);

        double distancia = CalculadoraPrecoCorrida.calcularDistanciaKm(origem, destino);

        assertTrue(distancia > 0);
    }

    @Test
    void deveGerarDetalhesPreco() {

        CategoriaVeiculo categoria = mock(CategoriaVeiculo.class);

        when(categoria.getMultiplicadorPreco()).thenReturn(1.5);
        when(categoria.getNome()).thenReturn("Luxo");

        String detalhes = CalculadoraPrecoCorrida.gerarDetalhesPreco(
                10,
                20,
                categoria
        );

        assertTrue(detalhes.contains("DETALHAMENTO DO PREÇO"));
        assertTrue(detalhes.contains("Luxo"));
        assertTrue(detalhes.contains("TOTAL"));
    }

    @Test
    void deveCalcularDistanciaZeroParaMesmoPonto() {

        Localizacao origem = new Localizacao(-7.22, -35.88);
        Localizacao destino = new Localizacao(-7.22, -35.88);

        double distancia = CalculadoraPrecoCorrida.calcularDistanciaKm(origem, destino);

        assertEquals(0, distancia, 0.001);
    }

    @Test
    void precoDeveSerArredondadoParaDuasCasas() {

        CategoriaVeiculo categoria = mock(CategoriaVeiculo.class);
        when(categoria.getMultiplicadorPreco()).thenReturn(1.0);

        double preco = CalculadoraPrecoCorrida.calcularPreco(3.333, 7.777, categoria);

        String precoString = String.valueOf(preco);

        assertTrue(precoString.contains("."));
    }
}
