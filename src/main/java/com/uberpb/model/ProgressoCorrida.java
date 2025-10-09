package com.uberpb.model;

public class ProgressoCorrida {
    private final String corridaId;
    private final double distanciaTotalKm;
    private final int minutosEstimados;

    private volatile double kmPercorridos;
    private volatile int minutosDecorridos;
    private volatile boolean concluida;

    public ProgressoCorrida(String corridaId, double distanciaTotalKm, int minutosEstimados) {
        this.corridaId = corridaId;
        this.distanciaTotalKm = distanciaTotalKm;
        this.minutosEstimados = minutosEstimados;
    }

    public String getCorridaId() { return corridaId; }
    public double getDistanciaTotalKm() { return distanciaTotalKm; }
    public int getMinutosEstimados() { return minutosEstimados; }
    public double getKmPercorridos() { return kmPercorridos; }
    public int getMinutosDecorridos() { return minutosDecorridos; }
    public boolean isConcluida() { return concluida; }

    public void avancar(double km, int minutos) {
        kmPercorridos = Math.min(distanciaTotalKm, kmPercorridos + km);
        minutosDecorridos = Math.min(minutosEstimados, minutosDecorridos + minutos);
        if (kmPercorridos >= distanciaTotalKm || minutosDecorridos >= minutosEstimados) {
            concluida = true;
        }
    }

    public int percentual() {
        if (distanciaTotalKm <= 0) return 0;
        return (int)Math.round((kmPercorridos / distanciaTotalKm) * 100.0);
    }
}
