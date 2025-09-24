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
}