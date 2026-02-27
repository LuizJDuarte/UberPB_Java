package com.uberpb.model;

import java.util.Locale;

public class ItemCardapio {
    private String nome;
    private String descricao;
    private double preco;

    public ItemCardapio(String nome, String descricao, double preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }

    /**
     * Usamos o caractere '#' para separar os campos INTERNOS do item.
     * Isso evita conflito com a vírgula (usada no CSV principal) 
     * e com o ponto-e-vírgula ou ponto.
     */
    public String toStringParaPersistencia() {
        // Removemos qualquer '#' acidental que o usuário digitar para não quebrar o formato
        String nomeLimpo = nome.replace("#", "").replace("|", "");
        String descLimpa = descricao.replace("#", "").replace("|", "");
        return String.format(Locale.US, "%s#%s#%.2f", nomeLimpo, descLimpa, preco);
    }

    public static ItemCardapio fromStringParaPersistencia(String dados) {
        if (dados == null || dados.isBlank()) return null;
        
        // Divide usando o separador interno '#'
        String[] parts = dados.split("#");
        if (parts.length == 3) {
            try {
                return new ItemCardapio(parts[0], parts[1], Double.parseDouble(parts[2]));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("  - %s: %s (R$ %.2f)", nome, descricao, preco);
    }
}
