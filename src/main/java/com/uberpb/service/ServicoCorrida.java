package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

/**
 * Serviço de Corrida — RF04..RF06.
 * Aceita categoria escolhida e calcula estimativa.
 * Mantém wrappers sem categoria para compatibilidade.
 */
public class ServicoCorrida {

    private final RepositorioCorrida repositorioCorrida;
    private final RepositorioUsuario repositorioUsuario;

    public ServicoCorrida(RepositorioCorrida repositorioCorrida, RepositorioUsuario repositorioUsuario) {
        this.repositorioCorrida = repositorioCorrida;
        this.repositorioUsuario = repositorioUsuario;
    }

    // --------- NOVAS ASSINATURAS (com categoria) ---------

    /** RF04 + RF06: por endereços com categoria */
    public Corrida solicitarCorrida(String emailPassageiro, String origemEndereco, String destinoEndereco, CategoriaVeiculo categoria) {
        if (emailPassageiro == null || emailPassageiro.isBlank())
            throw new IllegalArgumentException("Passageiro inválido.");
        if (origemEndereco == null || origemEndereco.isBlank()
                || destinoEndereco == null || destinoEndereco.isBlank())
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");

        Usuario u = repositorioUsuario.buscarPorEmail(emailPassageiro);
        if (!(u instanceof Passageiro))
            throw new IllegalArgumentException("Apenas passageiros podem solicitar corridas.");

        if (origemEndereco.trim().equalsIgnoreCase(destinoEndereco.trim()))
            throw new IllegalArgumentException("Origem e destino não podem ser iguais.");

        Corrida corrida = Corrida.novaComEnderecos(emailPassageiro, origemEndereco.trim(), destinoEndereco.trim(), categoria);
        repositorioCorrida.salvar(corrida);
        return corrida;
    }

    /** RF04 + RF06: por coordenadas com categoria */
    public Corrida solicitarCorrida(String emailPassageiro, Localizacao origem, Localizacao destino, CategoriaVeiculo categoria) {
        if (emailPassageiro == null || emailPassageiro.isBlank())
            throw new IllegalArgumentException("Passageiro inválido.");
        if (origem == null || destino == null)
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");

        Usuario u = repositorioUsuario.buscarPorEmail(emailPassageiro);
        if (!(u instanceof Passageiro))
            throw new IllegalArgumentException("Apenas passageiros podem solicitar corridas.");

        if (Double.compare(origem.latitude(), destino.latitude()) == 0 &&
            Double.compare(origem.longitude(), destino.longitude()) == 0)
            throw new IllegalArgumentException("Origem e destino não podem ser iguais.");

        Corrida corrida = Corrida.novaComCoordenadas(emailPassageiro, origem, destino, categoria);
        repositorioCorrida.salvar(corrida);
        return corrida;
    }

    // --------- Wrappers de compatibilidade (sem categoria) ---------

    public Corrida solicitarCorrida(String emailPassageiro, String origemEndereco, String destinoEndereco) {
        return solicitarCorrida(emailPassageiro, origemEndereco, destinoEndereco, null);
    }

    public Corrida solicitarCorrida(String emailPassageiro, Localizacao origem, Localizacao destino) {
        return solicitarCorrida(emailPassageiro, origem, destino, null);
    }

    // --------- RF05: Estimativa (tempo e preço) ---------

    public EstimativaCorrida estimarPorEnderecos(String origemEndereco, String destinoEndereco, CategoriaVeiculo categoria) {
        double distanciaKm = estimarDistanciaHeuristica(origemEndereco, destinoEndereco);
        return calcularEstimativa(distanciaKm, categoria);
    }

    public EstimativaCorrida estimarPorCoordenadas(Localizacao origem, Localizacao destino, CategoriaVeiculo categoria) {
        double distanciaKm = distanciaGeodesicaAproximada(origem, destino);
        return calcularEstimativa(distanciaKm, categoria);
    }

    // ===== helpers de estimativa =====

    private double estimarDistanciaHeuristica(String o, String d) {
        int h = Math.abs((o + "|" + d).hashCode());
        int delta = (h % 10);     // 0..9
        return 4.0 + delta;       // 4..13 km (heurístico)
    }

    private double distanciaGeodesicaAproximada(Localizacao o, Localizacao d) {
        double dx = o.latitude() - d.latitude();
        double dy = o.longitude() - d.longitude();
        return Math.sqrt(dx*dx + dy*dy) * 111.0; // ~km por grau
    }

    private EstimativaCorrida calcularEstimativa(double distanciaKm, CategoriaVeiculo cat) {
        int minutos = (int) Math.round(distanciaKm * 3.0);

        double base = 3.50, porKm = 1.20, porMin = 0.50; // UberX
        if (cat != null) {
            String nome = cat.name().toUpperCase();
            if (nome.contains("COMFORT")) { base = 4.50; porKm = 1.60; porMin = 0.60; }
            else if (nome.contains("BLACK")) { base = 7.00; porKm = 2.40; porMin = 0.80; }
            else if (nome.contains("BAG"))   { base*=1.10; porKm*=1.10; porMin*=1.10; }
            else if (nome.contains("XL"))    { base*=1.30; porKm*=1.30; porMin*=1.30; }
        }

        double preco = base + porKm * distanciaKm + porMin * minutos;
        preco = Math.round(preco * 100.0) / 100.0;
        return new EstimativaCorrida(distanciaKm, minutos, preco);
    }
}
