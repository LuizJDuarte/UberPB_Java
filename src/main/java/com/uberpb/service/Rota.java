package com.uberpb.service;

import com.uberpb.model.Localizacao;
import java.util.List;

public class Rota {
    private final double distancia;
    private final double tempoEstimado;
    private final List<Localizacao> pontos;
    
    public Rota(double distancia, double tempoEstimado, List<Localizacao> pontos) {
        this.distancia = distancia;
        this.tempoEstimado = tempoEstimado;
        this.pontos = pontos;
    }
    
    // Getters
    public double getDistancia() { return distancia; }
    public double getTempoEstimado() { return tempoEstimado; }
    public List<Localizacao> getPontos() { return pontos; }
}