package com.uberpb.service;

public class EstimativaChegada {
    private final int tempoEstimadoMinutos;
    private final double distanciaKm;
    private final String precisao;
    
    public EstimativaChegada(int tempoEstimadoMinutos, double distanciaKm, String precisao) {
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.distanciaKm = distanciaKm;
        this.precisao = precisao;
    }
    
    // Getters
    public int getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }
    public double getDistanciaKm() { return distanciaKm; }
    public String getPrecisao() { return precisao; }
}