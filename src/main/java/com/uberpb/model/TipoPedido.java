package com.uberpb.model;

public enum TipoPedido {
    IMEDIATO("Imediato"),
    AGENDADO("Agendado");

    private final String descricao;

    TipoPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
