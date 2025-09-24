package com.uberpb.model;

import java.time.LocalDateTime;

public class LocalizacaoTempoReal {
    private final String motoristaEmail;
    private final String corridaId;
    private final Localizacao localizacao;
    private final LocalDateTime timestamp;
    private final double distanciaPassageiroKm;
    private final int tempoEstimadoMinutos;
    
    public LocalizacaoTempoReal(String motoristaEmail, String corridaId, 
                               Localizacao localizacao, LocalDateTime timestamp,
                               double distanciaPassageiroKm, int tempoEstimadoMinutos) {
        this.motoristaEmail = motoristaEmail;
        this.corridaId = corridaId;
        this.localizacao = localizacao;
        this.timestamp = timestamp;
        this.distanciaPassageiroKm = distanciaPassageiroKm;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
    }
    
    // Getters
    public String getMotoristaEmail() { return motoristaEmail; }
    public String getCorridaId() { return corridaId; }
    public Localizacao getLocalizacao() { return localizacao; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getDistanciaPassageiroKm() { return distanciaPassageiroKm; }
    public int getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }
}