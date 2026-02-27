package com.uberpb.service;

import com.uberpb.model.Localizacao;
import java.util.List;

public class RotaOtimizada {
    private final Localizacao origem;
    private final Localizacao destino;
    private final double distanciaKm;
    private final double tempoEstimadoMinutos;
    private final List<Localizacao> pontosRota;
    private final double economiaTempoPercentual;
    
    public RotaOtimizada(Localizacao origem, Localizacao destino, double distanciaKm, 
                        double tempoEstimadoMinutos, List<Localizacao> pontosRota, 
                        double economiaTempoPercentual) {
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.pontosRota = pontosRota;
        this.economiaTempoPercentual = economiaTempoPercentual;
    }
    
    // Getters
    public Localizacao getOrigem() { return origem; }
    public Localizacao getDestino() { return destino; }
    public double getDistanciaKm() { return distanciaKm; }
    public double getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }
    public List<Localizacao> getPontosRota() { return pontosRota; }
    public double getEconomiaTempoPercentual() { return economiaTempoPercentual; }
}