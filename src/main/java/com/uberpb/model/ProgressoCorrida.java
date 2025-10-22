package com.uberpb.model;

/** Estado de acompanhamento de uma corrida. */
public class ProgressoCorrida {
    private final String corridaId;
    private final int duracaoSegundos;
    private final double distanciaKm;
    private int segundosDecorridos;

    public ProgressoCorrida(String corridaId, int duracaoSegundos, double distanciaKm, int segundosDecorridos) {
        this.corridaId = corridaId;
        this.duracaoSegundos = Math.max(1, duracaoSegundos);
        this.distanciaKm = Math.max(0.01, distanciaKm);
        this.segundosDecorridos = Math.max(0, Math.min(duracaoSegundos, segundosDecorridos));
    }

    public ProgressoCorrida(String corridaId, int duracaoSegundos, double distanciaKm) {
        this(corridaId, duracaoSegundos, distanciaKm, 0);
    }

    public String getCorridaId() { return corridaId; }
    public int getDuracaoSegundos() { return duracaoSegundos; }
    public double getDistanciaKm() { return distanciaKm; }
    public int getSegundosDecorridos() { return segundosDecorridos; }
    public void setSegundosDecorridos(int s) {
        this.segundosDecorridos = Math.max(0, Math.min(duracaoSegundos, s));
    }

    // === usados pelo CLI/serviço ===
    public int getPercentual() {
        return (int) Math.round((100.0 * segundosDecorridos) / duracaoSegundos);
    }
    public int getMinutosRestantes() {
        int rest = duracaoSegundos - segundosDecorridos;
        return (int) Math.ceil(rest / 60.0);
    }
    public double getDistanciaRestanteKm() {
        double perc = (double) segundosDecorridos / duracaoSegundos;
        return Math.max(0.0, distanciaKm * (1.0 - perc));
    }
    public boolean isConcluida() {
        return segundosDecorridos >= duracaoSegundos;
    }
}
