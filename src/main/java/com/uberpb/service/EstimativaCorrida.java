package com.uberpb.service;

public class EstimativaCorrida {
    private final double distanciaKm;
    private final int minutos;
    private final double preco;

    public EstimativaCorrida(double distanciaKm, int minutos, double preco) {
        this.distanciaKm = distanciaKm;
        this.minutos = minutos;
        this.preco = preco;
    }

    public double getDistanciaKm() { return distanciaKm; }
    public int getMinutos() { return minutos; }
    public double getPreco() { return preco; }
}