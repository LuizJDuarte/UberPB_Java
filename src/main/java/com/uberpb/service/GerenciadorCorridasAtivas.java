package com.uberpb.service;

import com.uberpb.model.CorridaStatus;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Calcula progresso por tempo decorrido; threadless (pull) e determinístico. */
public final class GerenciadorCorridasAtivas {

    public static final class Progresso {
        public final String corridaId;
        public final int percentual;        // 0–100
        public final double distanciaRestanteKm;
        public final int minutosRestantes;
        public final boolean concluida;

        public Progresso(String corridaId, int percentual, double distKm, int min, boolean concl) {
            this.corridaId = corridaId;
            this.percentual = percentual;
            this.distanciaRestanteKm = distKm;
            this.minutosRestantes = min;
            this.concluida = concl;
        }
    }

    private static final class Registro {
        final String id;
        final long inicioMs;
        final long fimMs;
        final double distanciaKm;

        Registro(String id, long inicioMs, long fimMs, double distanciaKm) {
            this.id = id; this.inicioMs = inicioMs; this.fimMs = fimMs; this.distanciaKm = distanciaKm;
        }
    }

    private final Map<String, Registro> ativos = new ConcurrentHashMap<>();

    /** Inicia uma corrida ativa calculando fim por duração em minutos. */
    public void iniciar(String corridaId, int minutos, double distanciaKm) {
        long ini = System.currentTimeMillis();
        long fim = ini + Math.max(1, minutos) * 60_000L;
        ativos.put(corridaId, new Registro(corridaId, ini, fim, Math.max(0.1, distanciaKm)));
    }

    /** Progresso calculado on-demand. */
    public Progresso progresso(String corridaId) {
        Registro r = ativos.get(corridaId);
        if (r == null) return new Progresso(corridaId, 100, 0.0, 0, true);

        long agora = System.currentTimeMillis();
        if (agora >= r.fimMs) {
            return new Progresso(corridaId, 100, 0.0, 0, true);
        }
        double total = r.fimMs - r.inicioMs;
        double decorrido = Math.max(0, agora - r.inicioMs);
        int pct = (int) Math.min(100, Math.max(0, Math.round(100.0 * decorrido / total)));

        // distância e tempo restantes proporcional ao que falta
        double fatorFalta = 1.0 - (pct / 100.0);
        double distRest = Math.max(0.0, r.distanciaKm * fatorFalta);
        int minRest = (int) Math.ceil((r.fimMs - agora) / 60_000.0);

        return new Progresso(corridaId, pct, distRest, minRest, false);
    }

    /** Marca como concluída removendo da lista. */
    public void encerrar(String corridaId) {
        ativos.remove(corridaId);
    }

    public boolean isAtiva(String corridaId) { return ativos.containsKey(corridaId); }

    public boolean isConcluida(String corridaId) {
        Registro r = ativos.get(corridaId);
        return r == null || System.currentTimeMillis() >= r.fimMs;
    }
}
