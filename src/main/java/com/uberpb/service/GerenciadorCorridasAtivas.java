package com.uberpb.service;

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
        final long duracaoMs; // Duração total em milissegundos
        final double distanciaKm;

        Registro(String id, long inicioMs, long duracaoMs, double distanciaKm) {
            this.id = id; 
            this.inicioMs = inicioMs; 
            this.duracaoMs = duracaoMs;
            this.distanciaKm = distanciaKm;
        }
    }

    private final Map<String, Registro> ativos = new ConcurrentHashMap<>();

    /** Inicia uma corrida ativa calculando fim por duração em minutos. */
    public void iniciar(String corridaId, int minutosDuracao, double distanciaKm) {
        // CORREÇÃO: Garantir valores mínimos realistas
        int minutos = Math.max(5, minutosDuracao); // Mínimo 5 minutos
        double distancia = Math.max(1.0, distanciaKm); // Mínimo 1km
        
        long inicio = System.currentTimeMillis();
        long duracaoMs = minutos * 60_000L; // Converter minutos para ms
        
        ativos.put(corridaId, new Registro(corridaId, inicio, duracaoMs, distancia));
        System.out.println("🚗 Corrida " + corridaId.substring(0, 8) + " iniciada: " + 
                          minutos + " min, " + distancia + " km");
    }

    /** Progresso calculado on-demand. */
    public Progresso progresso(String corridaId) {
        Registro r = ativos.get(corridaId);
        
        // CORREÇÃO: Se não encontrou registro, criar um padrão
        if (r == null) {
            System.out.println("⚠️  Corrida não encontrada no gerenciador: " + corridaId);
            return new Progresso(corridaId, 0, 5.0, 10, false); // Valores padrão
        }

        long agora = System.currentTimeMillis();
        long decorridoMs = Math.max(0, agora - r.inicioMs);
        
        // CORREÇÃO: Prevenir divisão por zero
        if (r.duracaoMs <= 0) {
            return new Progresso(corridaId, 100, 0.0, 0, true);
        }

        double percentual = (double) decorridoMs / r.duracaoMs * 100.0;
        percentual = Math.min(100.0, Math.max(0.0, percentual));
        
        boolean concluida = decorridoMs >= r.duracaoMs;
        
        // CORREÇÃO: Cálculo correto de distância e tempo restantes
        double distanciaRestante = concluida ? 0.0 : r.distanciaKm * (1.0 - (percentual / 100.0));
        long msRestantes = Math.max(0, r.duracaoMs - decorridoMs);
        int minutosRestantes = (int) Math.ceil(msRestantes / 60000.0);

        Progresso progresso = new Progresso(
            corridaId, 
            (int) Math.round(percentual),
            Math.round(distanciaRestante * 10.0) / 10.0, // 1 casa decimal
            minutosRestantes,
            concluida
        );
        
        return progresso;
    }

    /** Marca como concluída removendo da lista. */
    public void encerrar(String corridaId) {
        Registro removido = ativos.remove(corridaId);
        if (removido != null) {
            System.out.println("✅ Corrida " + corridaId.substring(0, 8) + " encerrada no gerenciador");
        }
    }

    public boolean isAtiva(String corridaId) { 
        return ativos.containsKey(corridaId); 
    }

    public boolean isConcluida(String corridaId) {
        Registro r = ativos.get(corridaId);
        if (r == null) return true;
        return System.currentTimeMillis() >= (r.inicioMs + r.duracaoMs);
    }
}