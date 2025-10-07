package com.uberpb.model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Avaliacao {
    protected final String id;
    protected final String corridaId;
    protected final int rating; // 1-5 estrelas
    protected final String comentario;
    protected final LocalDateTime dataAvaliacao;
    
    public Avaliacao(String corridaId, int rating, String comentario) {
        this.id = UUID.randomUUID().toString();
        this.corridaId = corridaId;
        this.rating = Math.max(1, Math.min(5, rating)); // Garantir entre 1-5
        this.comentario = comentario != null ? comentario : "";
        this.dataAvaliacao = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getCorridaId() { return corridaId; }
    public int getRating() { return rating; }
    public String getComentario() { return comentario; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    
    public abstract String toStringParaPersistencia();
}