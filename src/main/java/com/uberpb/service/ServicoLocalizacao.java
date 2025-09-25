package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServicoLocalizacao {
    
    private final RepositorioCorrida repositorioCorrida;
    private final Map<String, LocalizacaoTempoReal> localizacoesMotoristas = new HashMap<>();
    
    public ServicoLocalizacao(RepositorioCorrida repositorioCorrida) {
        this.repositorioCorrida = repositorioCorrida;
    }
    
    /**
     * Atualiza a localização de um motorista (simulado)
     */
    public void atualizarLocalizacaoMotorista(String motoristaEmail, String corridaId, 
                                             Localizacao localizacao) {
        // Simular cálculo de distância e tempo até o passageiro
        double distanciaKm = Math.random() * 10 + 1; // 1-11 km
        int tempoMinutos = (int) (distanciaKm * 3); // ~3 min/km
        
        LocalizacaoTempoReal loc = new LocalizacaoTempoReal(
            motoristaEmail, corridaId, localizacao, LocalDateTime.now(),
            distanciaKm, tempoMinutos
        );
        
        localizacoesMotoristas.put(corridaId, loc);
    }
    
    /**
     * Obtém a localização atual do motorista para uma corrida
     */
    public Optional<LocalizacaoTempoReal> obterLocalizacaoMotorista(String corridaId) {
        LocalizacaoTempoReal loc = localizacoesMotoristas.get(corridaId);
        
        if (loc == null) {
            // Simular localização se não existir
            Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
            if (corrida != null && corrida.getMotoristaAlocado() != null) {
                Localizacao localizacaoSimulada = new Localizacao(
                    -7.1190 + Math.random() * 0.1, // Latitude João Pessoa ±0.1
                    -34.8450 + Math.random() * 0.1  // Longitude João Pessoa ±0.1
                );
                
                atualizarLocalizacaoMotorista(
                    corrida.getMotoristaAlocado(), 
                    corridaId, 
                    localizacaoSimulada
                );
                
                loc = localizacoesMotoristas.get(corridaId);
            }
        }
        
        return Optional.ofNullable(loc);
    }
    
    /**
     * Simula o movimento do motorista em direção ao passageiro
     */
    public void simularMovimentoMotorista(String corridaId) {
        Optional<LocalizacaoTempoReal> locOpt = obterLocalizacaoMotorista(corridaId);
        locOpt.ifPresent(loc -> {
            // Simular movimento reduzindo a distância
            double novaDistancia = Math.max(0.1, loc.getDistanciaPassageiroKm() - 0.5);
            int novoTempo = (int) (novaDistancia * 3);
            
            LocalizacaoTempoReal novaLoc = new LocalizacaoTempoReal(
                loc.getMotoristaEmail(),
                loc.getCorridaId(),
                loc.getLocalizacao(),
                LocalDateTime.now(),
                novaDistancia,
                novoTempo
            );
            
            localizacoesMotoristas.put(corridaId, novaLoc);
        });
    }
    
    /**
     * MELHORADO: Calcula estimativa de chegada mais precisa
     */
    public EstimativaChegada calcularEstimativaChegada(String corridaId) {
        Optional<LocalizacaoTempoReal> locOpt = obterLocalizacaoMotorista(corridaId);
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        
        if (locOpt.isEmpty() || corrida == null) {
            return new EstimativaChegada(0, 0, "Indisponível");
        }
        
        LocalizacaoTempoReal loc = locOpt.get();
        
        // Fatores que influenciam a estimativa
        double fatorTrafico = calcularFatorTrafico();
        double fatorTempo = calcularFatorTempo();
        
        // Estimativa ajustada
        double tempoAjustado = loc.getTempoEstimadoMinutos() * fatorTrafico * fatorTempo;
        double distanciaAjustada = loc.getDistanciaPassageiroKm();
        
        String precisao = determinarPrecisaoEstimativa(tempoAjustado);
        
        return new EstimativaChegada(
            Math.max(1, (int) tempoAjustado), // Mínimo 1 minuto
            distanciaAjustada,
            precisao
        );
    }
    
    private double calcularFatorTrafico() {
        // Simular condições de tráfego baseadas na hora do dia
        int hora = LocalDateTime.now().getHour();
        if (hora >= 7 && hora <= 9) return 1.3; // Hora do pico manhã
        if (hora >= 17 && hora <= 19) return 1.4; // Hora do pico tarde
        if (hora >= 22 || hora <= 5) return 0.8; // Madrugada - tráfego livre
        return 1.0; // Horário normal
    }
    
    private double calcularFatorTempo() {
        // Simular condições climáticas (em sistema real, integrar com API de clima)
        return 1.0; // Placeholder para expansão futura
    }
    
    private String determinarPrecisaoEstimativa(double tempo) {
        if (tempo <= 5) return "Alta";
        if (tempo <= 15) return "Média";
        return "Baixa";
    }
    
    /**
     * NOVO: Atualização em tempo real com otimização
     */
    public void atualizarLocalizacaoComOtimizacao(String motoristaEmail, String corridaId, 
                                                 Localizacao localizacao, 
                                                 ServicoOtimizacaoRota servicoOtimizacao) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida != null && corrida.getOrigem() != null && corrida.getDestino() != null) {
            // Calcular rota otimizada
            RotaOtimizada rota = servicoOtimizacao.calcularRotaOtimizada(
                localizacao, corrida.getDestino());
            
            // Usar estimativas da rota otimizada
            double distanciaKm = rota.getDistanciaKm();
            int tempoMinutos = (int) Math.ceil(rota.getTempoEstimadoMinutos());
            
            LocalizacaoTempoReal loc = new LocalizacaoTempoReal(
                motoristaEmail, corridaId, localizacao, LocalDateTime.now(),
                distanciaKm, tempoMinutos
            );
            
            localizacoesMotoristas.put(corridaId, loc);
        } else {
            // Fallback para método original
            atualizarLocalizacaoMotorista(motoristaEmail, corridaId, localizacao);
        }
    }
}