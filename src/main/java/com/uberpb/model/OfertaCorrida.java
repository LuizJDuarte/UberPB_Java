package com.uberpb.model;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Representa uma oferta de corrida enviada a um motorista.
 * Persistência simples em pipe:
 *   id|corridaId|motoristaEmail|status
 */
public class OfertaCorrida {
    private final String id;
    private final String corridaId;
    private final String motoristaEmail;
    private OfertaStatus status;

    public OfertaCorrida(String id, String corridaId, String motoristaEmail, OfertaStatus status) {
        this.id = Objects.requireNonNull(id);
        this.corridaId = Objects.requireNonNull(corridaId);
        this.motoristaEmail = Objects.requireNonNull(motoristaEmail);
        this.status = Objects.requireNonNull(status);
    }

    public static OfertaCorrida nova(String corridaId, String motoristaEmail) {
        return new OfertaCorrida(UUID.randomUUID().toString(), corridaId, motoristaEmail, OfertaStatus.PENDENTE);
    }

    public String getId() { return id; }
    public String getCorridaId() { return corridaId; }
    public String getMotoristaEmail() { return motoristaEmail; }
    public OfertaStatus getStatus() { return status; }
    public void setStatus(OfertaStatus status) { this.status = status; }

    public String toStringParaPersistencia() {
        return String.join("|", id, corridaId, motoristaEmail, status.name());
    }

    public static OfertaCorrida fromStringParaPersistencia(String linha) {
        // Duas opções CORRETAS: use Pattern.quote("|")...
        String[] p = linha.split(Pattern.quote("|"), -1);
        // ...ou use split com escape duplo: String[] p = linha.split("\\\\|", -1);
        String id = p[0];
        String corridaId = p[1];
        String motorista = p[2];
        OfertaStatus st = OfertaStatus.valueOf(p[3]);
        return new OfertaCorrida(id, corridaId, motorista, st);
    }
}
