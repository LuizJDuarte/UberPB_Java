package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Localizacao;
import java.time.LocalDateTime;

public class CalculadoraPrecoCorrida {
    
    private static final double TAXA_BASE = 5.0;
    private static final double PRECO_POR_KM = 2.5;
    private static final double PRECO_POR_MINUTO = 0.5;
    private static final double VELOCIDADE_MEDIA_KMH = 30.0;
    
    // Fatores dinâmicos
    private static final double FATOR_HORA_PICO = 1.3;
    private static final double FATOR_MADRUGADA = 0.8;
    private static final double TAXA_CHUVA = 1.2;
    
    public static double calcularPreco(double distanciaKm, double tempoMinutos, CategoriaVeiculo categoria) {
        double precoBase = calcularPrecoBase(distanciaKm, tempoMinutos);
        double precoComCategoria = precoBase * categoria.getMultiplicadorPreco();
        double precoComFatores = aplicarFatoresExternos(precoComCategoria);
        
        return Math.round(precoComFatores * 100.0) / 100.0; // Arredonda para 2 casas decimais
    }
    
    private static double calcularPrecoBase(double distanciaKm, double tempoMinutos) {
        return TAXA_BASE + (distanciaKm * PRECO_POR_KM) + (tempoMinutos * PRECO_POR_MINUTO);
    }
    
    private static double aplicarFatoresExternos(double preco) {
        double precoAjustado = preco;
        
        // Fator horário
        int hora = LocalDateTime.now().getHour();
        if ((hora >= 7 && hora <= 9) || (hora >= 17 && hora <= 19)) {
            precoAjustado *= FATOR_HORA_PICO;
        } else if (hora >= 22 || hora <= 5) {
            precoAjustado *= FATOR_MADRUGADA;
        }
        
        // Simular condição climática (em sistema real, integrar com API)
        boolean estaChovendo = simularCondicaoClimatica();
        if (estaChovendo) {
            precoAjustado *= TAXA_CHUVA;
        }
        
        return precoAjustado;
    }
    
    private static boolean simularCondicaoClimatica() {
        // Simulação simples - em sistema real, usar API de clima
        return Math.random() > 0.7; // 30% de chance de "estar chovendo"
    }
    
    public static double estimarTempoMinutos(double distanciaKm) {
        return (distanciaKm / VELOCIDADE_MEDIA_KMH) * 60;
    }
    
    public static double estimarDistanciaKm(String origem, String destino) {
        // Simulação melhorada
        int complexidade = Math.abs(origem.hashCode() - destino.hashCode()) % 10;
        return 3.0 + (complexidade * 1.2); // Entre 3 e 15 km
    }
    
    public static double calcularDistanciaKm(Localizacao origem, Localizacao destino) {
        final int R = 6371;
        
        double lat1 = Math.toRadians(origem.latitude());
        double lon1 = Math.toRadians(origem.longitude());
        double lat2 = Math.toRadians(destino.latitude());
        double lon2 = Math.toRadians(destino.longitude());
        
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                  Math.cos(lat1) * Math.cos(lat2) *
                  Math.sin(dLon/2) * Math.sin(dLon/2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return R * c;
    }
    
    /**
     * NOVO: Gerar descrição detalhada do cálculo
     */
    public static String gerarDetalhesPreco(double distanciaKm, double tempoMinutos, CategoriaVeiculo categoria) {
        double taxaBase = TAXA_BASE;
        double custoDistancia = distanciaKm * PRECO_POR_KM;
        double custoTempo = tempoMinutos * PRECO_POR_MINUTO;
        double precoBase = taxaBase + custoDistancia + custoTempo;
        double multiplicador = categoria.getMultiplicadorPreco();
        double precoFinal = precoBase * multiplicador;
        
        return String.format(
            "💵 DETALHAMENTO DO PREÇO:\n" +
            "   📍 Taxa base: R$ %.2f\n" +
            "   🛣️  Distância (%.1f km): R$ %.2f\n" +
            "   ⏱️  Tempo (%d min): R$ %.2f\n" +
            "   🚗 Categoria %s: x%.1f\n" +
            "   💰 TOTAL: R$ %.2f",
            taxaBase, distanciaKm, custoDistancia, (int)tempoMinutos, custoTempo,
            categoria.getNome(), multiplicador, precoFinal
        );
    }
}