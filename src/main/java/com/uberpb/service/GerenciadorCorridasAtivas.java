package com.uberpb.service;

import com.uberpb.model.ProgressoCorrida;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controla o avanço “em tempo real” das corridas usando threads.
 */
public class GerenciadorCorridasAtivas {

    private final Map<String, ProgressoCorrida> emAndamento = new ConcurrentHashMap<>();

    public void iniciar(String corridaId, double distanciaKm, int minutos) {
        ProgressoCorrida p = new ProgressoCorrida(corridaId, distanciaKm, minutos);
        emAndamento.put(corridaId, p);

        Thread t = new Thread(() -> {
            // Simulação: avança a cada ~1s, proporcional à estimativa total
            int steps = Math.max(1, minutos);
            double kmPorStep = distanciaKm / steps;
            for (int i = 0; i < steps; i++) {
                if (p.isConcluida()) break;
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                p.avancar(kmPorStep, 1);
            }
            p.avancar(0, 0); // força marca de concluída
        }, "Corrida-" + corridaId);

        t.setDaemon(true);
        t.start();
    }

    public ProgressoCorrida progresso(String corridaId) {
        return emAndamento.get(corridaId);
    }

    public void encerrar(String corridaId) {
        emAndamento.remove(corridaId);
    }
}
