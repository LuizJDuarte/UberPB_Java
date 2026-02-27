package com.uberpb.service;

import com.uberpb.model.Localizacao;

/**
 * Serviço de localização/“geocoding” local, sem dependências externas.
 * - geocodificar(String) => mapeia texto para coordenadas estáveis (hash).
 * - distanciaKm(Localizacao, Localizacao) => distância euclidiana aproximada.
 * - obterLocalizacaoAtual(String) => posição “mockada” do usuário.
 */
public class ServicoLocalizacao {

    public ServicoLocalizacao() { }

    public Localizacao geocodificar(String endereco) {
        if (endereco == null || endereco.isBlank()) return new Localizacao(0,0);
        int h = Math.abs(endereco.hashCode());
        double lat = (h % 18000) / 100.0 - 90.0;      // [-90, 90)
        double lon = ((h / 18000) % 36000) / 100.0 - 180.0; // [-180, 180)
        return new Localizacao(lat, lon);
    }

    /** Distância “flat” em km (boa o suficiente para ordenação). */
    public double distanciaKm(Localizacao a, Localizacao b) {
        if (a == null || b == null) return 0;
        double dx = a.latitude() - b.latitude();
        double dy = a.longitude() - b.longitude();
        // 1 grau ~ 111 km, aproximação
        return Math.sqrt(dx*dx + dy*dy) * 111.0;
    }

    /** Posição aproximada do usuário (mock estável por email). */
    public Localizacao obterLocalizacaoAtual(String email) {
        if (email == null) return new Localizacao(0,0);
        int h = Math.abs(email.hashCode());
        double lat = (h % 9000) / 100.0 - 45.0;
        double lon = ((h / 9000) % 18000) / 100.0 - 90.0;
        return new Localizacao(lat, lon);
    }
}
