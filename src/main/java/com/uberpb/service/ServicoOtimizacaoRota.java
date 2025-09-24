package com.uberpb.service;

import com.uberpb.model.Localizacao;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para otimização de rotas considerando tráfego, distância e tempo
 */
public class ServicoOtimizacaoRota {
    
    private static final double VELOCIDADE_LIVRE = 50.0; // km/h
    private static final double VELOCIDADE_CONGESTIONADA = 15.0; // km/h
    private static final double FATOR_OTIMIZACAO = 0.85; // 15% de melhoria na rota otimizada
    
    public RotaOtimizada calcularRotaOtimizada(Localizacao origem, Localizacao destino) {
        // Simulação de cálculo de rota - em sistema real integraria com API como Google Maps
        double distanciaDireta = calcularDistancia(origem, destino);
        
        // Simular diferentes rotas possíveis
        List<Rota> rotasPossiveis = simularRotasAlternativas(origem, destino, distanciaDireta);
        
        // Encontrar a melhor rota
        Rota melhorRota = encontrarMelhorRota(rotasPossiveis);
        
        // Aplicar otimização
        double distanciaOtimizada = melhorRota.getDistancia() * FATOR_OTIMIZACAO;
        double tempoOtimizado = melhorRota.getTempoEstimado() * FATOR_OTIMIZACAO;
        
        return new RotaOtimizada(
            origem, 
            destino, 
            distanciaOtimizada, 
            tempoOtimizado, 
            melhorRota.getPontos(),
            calcularEconomiaTempo(melhorRota.getTempoEstimado(), tempoOtimizado)
        );
    }
    
    public RotaOtimizada calcularRotaParaCorrida(String corridaId) {
        // Implementar busca da corrida e cálculo da rota otimizada
        // Integrar com repositório de corridas
        return null; // Placeholder
    }
    
    private List<Rota> simularRotasAlternativas(Localizacao origem, Localizacao destino, double distanciaBase) {
        List<Rota> rotas = new ArrayList<>();
        
        // Rota 1: Mais rápida (menos tráfego)
        rotas.add(new Rota(
            distanciaBase * 1.1, // 10% mais longa mas mais rápida
            (distanciaBase * 1.1 / VELOCIDADE_LIVRE) * 60,
            gerarPontosRota(origem, destino, 3)
        ));
        
        // Rota 2: Mais curta (mas com tráfego)
        rotas.add(new Rota(
            distanciaBase * 0.95, // 5% mais curta
            (distanciaBase * 0.95 / VELOCIDADE_CONGESTIONADA) * 60,
            gerarPontosRota(origem, destino, 5)
        ));
        
        // Rota 3: Balanceada
        rotas.add(new Rota(
            distanciaBase,
            (distanciaBase / ((VELOCIDADE_LIVRE + VELOCIDADE_CONGESTIONADA) / 2)) * 60,
            gerarPontosRota(origem, destino, 4)
        ));
        
        return rotas;
    }
    
    private Rota encontrarMelhorRota(List<Rota> rotas) {
        return rotas.stream()
                .min((r1, r2) -> Double.compare(
                    r1.getTempoEstimado() * 0.7 + r1.getDistancia() * 0.3, // Ponderar tempo 70%, distância 30%
                    r2.getTempoEstimado() * 0.7 + r2.getDistancia() * 0.3
                ))
                .orElse(rotas.get(0));
    }
    
    private double calcularDistancia(Localizacao origem, Localizacao destino) {
        // Usar mesma lógica do ServicoCorrida ou melhorar
        double dx = origem.latitude() - destino.latitude();
        double dy = origem.longitude() - destino.longitude();
        return Math.sqrt(dx*dx + dy*dy) * 111.0;
    }
    
    private List<Localizacao> gerarPontosRota(Localizacao origem, Localizacao destino, int pontos) {
        List<Localizacao> pontosRota = new ArrayList<>();
        pontosRota.add(origem);
        
        for (int i = 1; i < pontos - 1; i++) {
            double frac = (double) i / pontos;
            double lat = origem.latitude() + (destino.latitude() - origem.latitude()) * frac;
            double lon = origem.longitude() + (destino.longitude() - origem.longitude()) * frac;
            pontosRota.add(new Localizacao(lat, lon));
        }
        
        pontosRota.add(destino);
        return pontosRota;
    }
    
    private double calcularEconomiaTempo(double tempoOriginal, double tempoOtimizado) {
        return ((tempoOriginal - tempoOtimizado) / tempoOriginal) * 100;
    }
}