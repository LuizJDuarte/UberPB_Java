package com.uberpb.model;

public enum CategoriaVeiculo {
    UBERX("UberX", "Corrida mais econômica", 1.0),
    COMFORT("Comfort", "Carros mais novos e espaçosos", 1.3),
    BLACK("Black", "Veículos premium e motoristas de alta avaliação", 2.0),
    BAG("Bag", "Veículos com porta-malas maior", 1.5),
    XL("XL", "Capacidade para mais passageiros", 1.8);

    private final String nome;
    private final String descricao;
    private final double multiplicadorPreco;

    CategoriaVeiculo(String nome, String descricao, double multiplicadorPreco) {
        this.nome = nome;
        this.descricao = descricao;
        this.multiplicadorPreco = multiplicadorPreco;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getMultiplicadorPreco() { return multiplicadorPreco; }

    /** Converte a partir do nome de exibição (ex.: "UberX"). Retorna null se não reconhecer. */
    public static CategoriaVeiculo fromString(String text) {
        if (text == null) return null;
        for (CategoriaVeiculo c : values()) {
            if (c.nome.equalsIgnoreCase(text)) return c;
        }
        return null;
    }
}