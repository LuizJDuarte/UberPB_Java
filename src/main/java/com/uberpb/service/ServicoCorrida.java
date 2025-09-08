package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

public class ServicoCorrida {

    private final RepositorioCorrida repositorioCorrida;
    private final RepositorioUsuario repositorioUsuario;

    public ServicoCorrida(RepositorioCorrida repositorioCorrida, RepositorioUsuario repositorioUsuario) {
        this.repositorioCorrida = repositorioCorrida;
        this.repositorioUsuario = repositorioUsuario;
    }

    /** RF04 por endereço (texto). */
    public Corrida solicitarCorrida(String emailPassageiro, String origemEndereco, String destinoEndereco) {
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

        Corrida corrida = Corrida.novaComEnderecos(emailPassageiro, origemEndereco.trim(), destinoEndereco.trim());
        repositorioCorrida.salvar(corrida);
        return corrida;
    }

    /** Mantido para futuros usos: por coordenadas. */
    public Corrida solicitarCorrida(String emailPassageiro, Localizacao origem, Localizacao destino) {
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

        Corrida corrida = Corrida.novaComCoordenadas(emailPassageiro, origem, destino);
        repositorioCorrida.salvar(corrida);
        return corrida;
    }
}
