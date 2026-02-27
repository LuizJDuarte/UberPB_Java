package com.uberpb.service;

/** Estimativa simplificada de chegada (distância e minutos). */
public class EstimativaChegada {

    private double distanciaKm;
    private int tempoEstimadoMinutos;

    public EstimativaChegada() {
        // valores neutros
        this(0.0, 0);
    }

    public EstimativaChegada(double distanciaKm, int tempoMin) {
        this.distanciaKm = Math.max(0.0, distanciaKm);
        this.tempoEstimadoMinutos = Math.max(0, tempoMin);
    }

    /** Conveniência: calcula minutos assumindo velocidade média (km/h). */
    public static EstimativaChegada porVelocidadeMedia(double distanciaKm, double kmPorHora) {
        if (kmPorHora <= 0) kmPorHora = 30; // padrão simples
        int min = (int)Math.ceil((distanciaKm / kmPorHora) * 60.0);
        return new EstimativaChegada(distanciaKm, min);
    }

    public double getDistanciaKm() { return distanciaKm; }
    public int getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }

    public void setDistanciaKm(double d) { this.distanciaKm = Math.max(0.0, d); }
    public void setTempoEstimadoMinutos(int m) { this.tempoEstimadoMinutos = Math.max(0, m); }
}
