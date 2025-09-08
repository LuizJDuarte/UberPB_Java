package com.uberpb.model;

public enum CategoriaVeiculo {
    UBERX("UberX", "Corrida mais econômica"),
    COMFORT("Comfort", "Carros mais novos e espaçosos"),
    BLACK("Black", "Veículos premium e motoristas de alta avaliação"),
    BAG("Bag", "Veículos com porta-malas maior"),
    XL("XL", "Capacidade para mais passageiros");

    private final String nome;
    private final String descricao;

    CategoriaVeiculo(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }

    /** Converte a partir do nome de exibição (ex.: "UberX"). Retorna null se não reconhecer. */
    public static CategoriaVeiculo fromString(String text) {
        if (text == null) return null;
        for (CategoriaVeiculo c : values()) {
            if (c.nome.equalsIgnoreCase(text)) return c;
        }
        return null;
    }
}
