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

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public static CategoriaVeiculo fromString(String text) {
        for (CategoriaVeiculo b : CategoriaVeiculo.values()) {
            if (b.nome.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null; // Ou lançar uma exceção IllegalArgumentException
    }
}