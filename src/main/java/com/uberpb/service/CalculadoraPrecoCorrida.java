package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.Localizacao;

public class CalculadoraPrecoCorrida {
    
    private static final double TAXA_BASE = 5.0;
    private static final double PRECO_POR_KM = 2.5;
    private static final double PRECO_POR_MINUTO = 0.5;
    private static final double VELOCIDADE_MEDIA_KMH = 30.0; // Velocidade média em área urbana
    
    public static double calcularPreco(double distanciaKm, double tempoMinutos, CategoriaVeiculo categoria) {
        double precoBase = TAXA_BASE + (distanciaKm * PRECO_POR_KM) + (tempoMinutos * PRECO_POR_MINUTO);
        
        // Aplicar multiplicadores conforme categoria
        return precoBase * categoria.getMultiplicadorPreco();
    }
    
    public static double estimarTempoMinutos(double distanciaKm) {
        // Considerando velocidade média de 30 km/h em área urbana
        return (distanciaKm / VELOCIDADE_MEDIA_KMH) * 60; // Converter para minutos
    }
    
    /**
     * Método simplificado para estimar distância entre dois endereços
     * Em um sistema real, isso seria integrado com API de geolocalização
     */
    public static double estimarDistanciaKm(String origem, String destino) {
        // Simulação simples baseada no comprimento dos endereços
        // Em um sistema real, usaríamos coordenadas geográficas
        int diff = Math.abs(origem.length() - destino.length());
        return 5.0 + (diff * 0.5); // Entre 5 e 15 km
    }
    
    /**
     * Método para cálculo com coordenadas geográficas (futuro)
     */
    public static double calcularDistanciaKm(Localizacao origem, Localizacao destino) {
        // Fórmula de Haversine para calcular distância entre coordenadas
        final int R = 6371; // Raio da Terra em km
        
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
}