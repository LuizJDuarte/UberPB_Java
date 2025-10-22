package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Localizacao;

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

    // ==== cálculo por coordenadas ====
    public EstimativaCorrida calcularPorCoordenadas(Localizacao origem,
                                                    Localizacao destino,
                                                    CategoriaVeiculo cat) {
        double d = distanciaHaversineKm(origem.latitude(), origem.longitude(),
                                        destino.latitude(), destino.longitude());
        return calcular(d, cat);
    }

    // ==== cálculo por endereços (geocodificação offline deterministic) ====
    public EstimativaCorrida calcularPorEnderecos(String origem, String destino, CategoriaVeiculo cat) {
        Localizacao o = geocodeOffline(origem);
        Localizacao d = geocodeOffline(destino);
        double dKm = distanciaHaversineKm(o.latitude(), o.longitude(), d.latitude(), d.longitude());
        return calcular(dKm, cat);
    }

    private EstimativaCorrida calcular(double distanciaKm, CategoriaVeiculo cat) {
        int minutos = (int) Math.max(1, Math.round(distanciaKm * 3)); // ~20km/h
        double fator = switch (cat) {
            case UBERX   -> 1.0;
            case COMFORT -> 1.2;
            case BLACK   -> 1.8;
            case BAG     -> 1.3;
            case XL      -> 1.5;
            default      -> 1.0;
        };
        double preco = Math.max(8.0, distanciaKm * 5.0 * fator);
        return new EstimativaCorrida(distanciaKm, minutos, preco);
    }

    private static double distanciaHaversineKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return Math.max(0.1, R * c);
    }

    private static Localizacao geocodeOffline(String s) {
        int h = (s == null) ? 0 : s.hashCode();
        double lat = -7.12 + ((h & 0x7FFF) / 32767.0) * 0.20;
        double lon = -34.90 + (((h>>>15) & 0x7FFF) / 32767.0) * 0.20;
        return new Localizacao(lat, lon);
    }
}
