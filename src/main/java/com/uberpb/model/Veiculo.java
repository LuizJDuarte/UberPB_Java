package com.uberpb.model;

import java.util.ArrayList;
import java.util.List;

public class Veiculo {
    private String modelo;
    private int ano;
    private String placa;
    private String cor;
    private int capacidadePassageiros;
    private String tamanhoPortaMalas; // "P", "M", "G" ou "N/A"
    private List<CategoriaVeiculo> categoriasDisponiveis;

    public Veiculo(String modelo, int ano, String placa, String cor, int capacidadePassageiros, String tamanhoPortaMalas) {
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.cor = cor;
        this.capacidadePassageiros = capacidadePassageiros;
        this.tamanhoPortaMalas = tamanhoPortaMalas;
        this.categoriasDisponiveis = new ArrayList<>();
    }

    // Getters
    public String getModelo() {
        return modelo;
    }

    public int getAno() {
        return ano;
    }

    public String getPlaca() {
        return placa;
    }

    public String getCor() {
        return cor;
    }

    public int getCapacidadePassageiros() {
        return capacidadePassageiros;
    }

    public String getTamanhoPortaMalas() {
        return tamanhoPortaMalas;
    }

    public List<CategoriaVeiculo> getCategoriasDisponiveis() {
        return categoriasDisponiveis;
    }

    // Setter para categorias disponíveis, pois serão determinadas pela validação
    public void setCategoriasDisponiveis(List<CategoriaVeiculo> categoriasDisponiveis) {
        this.categoriasDisponiveis = categoriasDisponiveis;
    }

    public String toStringParaPersistencia() {
        StringBuilder sb = new StringBuilder();
        sb.append(modelo).append("|")
          .append(ano).append("|")
          .append(placa).append("|")
          .append(cor).append("|")
          .append(capacidadePassageiros).append("|")
          .append(tamanhoPortaMalas);

        if (!categoriasDisponiveis.isEmpty()) {
            sb.append("|");
            for (int i = 0; i < categoriasDisponiveis.size(); i++) {
                sb.append(categoriasDisponiveis.get(i).getNome());
                if (i < categoriasDisponiveis.size() - 1) {
                    sb.append(";");
                }
            }
        }
        return sb.toString();
    }

    public static Veiculo fromStringParaPersistencia(String veiculoData) {
        if (veiculoData == null || veiculoData.isEmpty()) {
            return null;
        }
        String[] parts = veiculoData.split("\\|");
        if (parts.length < 6) { // Pelo menos os 6 primeiros atributos
            return null;
        }

        String modelo = parts[0];
        int ano = Integer.parseInt(parts[1]);
        String placa = parts[2];
        String cor = parts[3];
        int capacidadePassageiros = Integer.parseInt(parts[4]);
        String tamanhoPortaMalas = parts[5];

        Veiculo veiculo = new Veiculo(modelo, ano, placa, cor, capacidadePassageiros, tamanhoPortaMalas);

        if (parts.length > 6) { // Se houver categorias
            String[] categoriasStr = parts[6].split(";");
            List<CategoriaVeiculo> categorias = new ArrayList<>();
            for (String catStr : categoriasStr) {
                CategoriaVeiculo cat = CategoriaVeiculo.fromString(catStr);
                if (cat != null) {
                    categorias.add(cat);
                }
            }
            veiculo.setCategoriasDisponiveis(categorias);
        }
        return veiculo;
    }

    @Override
    public String toString() {
        return "Veículo [Modelo=" + modelo + ", Ano=" + ano + ", Placa=" + placa +
               ", Cor=" + cor + ", Capacidade=" + capacidadePassageiros +
               ", Porta-malas=" + tamanhoPortaMalas + ", Categorias=" + categoriasDisponiveis + "]";
    }
}