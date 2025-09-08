package com.uberpb.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Veículo no formato que o repositório espera:
 *   toStringParaPersistencia() => "modelo|ano|placa|cor|capacidade|portaMalas|cat1;cat2;..."
 *   fromStringParaPersistencia(s) => reconstrói o objeto com a lista de categorias
 */
public class Veiculo {
    private String modelo;
    private int ano;
    private String placa;
    private String cor;
    private int capacidadePassageiros;
    private String tamanhoPortaMalas; // "P", "M", "G" ou "N/A"
    private List<CategoriaVeiculo> categoriasDisponiveis = new ArrayList<>();

    public Veiculo(String modelo, int ano, String placa, String cor,
                   int capacidadePassageiros, String tamanhoPortaMalas) {
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.cor = cor;
        this.capacidadePassageiros = capacidadePassageiros;
        this.tamanhoPortaMalas = tamanhoPortaMalas;
    }

    // Getters/Setters
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public int getCapacidadePassageiros() { return capacidadePassageiros; }
    public void setCapacidadePassageiros(int capacidadePassageiros) { this.capacidadePassageiros = capacidadePassageiros; }

    public String getTamanhoPortaMalas() { return tamanhoPortaMalas; }
    public void setTamanhoPortaMalas(String tamanhoPortaMalas) { this.tamanhoPortaMalas = tamanhoPortaMalas; }

    public List<CategoriaVeiculo> getCategoriasDisponiveis() { return categoriasDisponiveis; }
    public void setCategoriasDisponiveis(List<CategoriaVeiculo> categoriasDisponiveis) {
        this.categoriasDisponiveis = categoriasDisponiveis != null ? categoriasDisponiveis : new ArrayList<>();
    }

    /** Serializa no formato de linha esperado pelo repositório. */
    public String toStringParaPersistencia() {
        StringBuilder sb = new StringBuilder();
        sb.append(nullSafe(modelo)).append("|")
          .append(ano).append("|")
          .append(nullSafe(placa)).append("|")
          .append(nullSafe(cor)).append("|")
          .append(capacidadePassageiros).append("|")
          .append(nullSafe(tamanhoPortaMalas)).append("|");

        if (categoriasDisponiveis != null && !categoriasDisponiveis.isEmpty()) {
            for (int i = 0; i < categoriasDisponiveis.size(); i++) {
                sb.append(categoriasDisponiveis.get(i).getNome());
                if (i < categoriasDisponiveis.size() - 1) sb.append(";");
            }
        }
        return sb.toString();
    }

    /** Reconstrói a partir da string persistida pelo repositório. */
    public static Veiculo fromStringParaPersistencia(String data) {
        if (data == null || data.isBlank() || "null".equalsIgnoreCase(data)) return null;

        String[] p = data.split("\\|", -1); // -1 para manter vazios
        String modelo = p.length > 0 ? p[0] : "";
        int ano = p.length > 1 ? parseInt(p[1], 0) : 0;
        String placa = p.length > 2 ? p[2] : "";
        String cor = p.length > 3 ? p[3] : "";
        int capacidade = p.length > 4 ? parseInt(p[4], 0) : 0;
        String portaMalas = p.length > 5 ? p[5] : "";

        Veiculo v = new Veiculo(modelo, ano, placa, cor, capacidade, portaMalas);

        if (p.length > 6 && p[6] != null && !p[6].isBlank()) {
            String[] cats = p[6].split(";");
            List<CategoriaVeiculo> lista = new ArrayList<>();
            for (String c : cats) {
                CategoriaVeiculo cat = CategoriaVeiculo.fromString(c.trim());
                if (cat != null) lista.add(cat);
            }
            v.setCategoriasDisponiveis(lista);
        }
        return v;
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
    private static String nullSafe(String s) { return s == null ? "" : s; }

    @Override
    public String toString() {
        return "Veículo [Modelo=" + modelo + ", Ano=" + ano + ", Placa=" + placa +
               ", Cor=" + cor + ", Capacidade=" + capacidadePassageiros +
               ", Porta-malas=" + tamanhoPortaMalas + ", Categorias=" + categoriasDisponiveis + "]";
    }
}
