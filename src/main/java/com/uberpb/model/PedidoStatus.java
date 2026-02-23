package com.uberpb.model;

public enum PedidoStatus {
    CRIADO("Criado"),
    CONFIRMADO("Confirmado"),
    AGENDADO("Agendado"),
    ACEITO("Aceito"),
    EM_PREPARACAO("Em Preparação"),
    PRONTO("Pronto"),
    EM_ROTA("Em Rota"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    PedidoStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
