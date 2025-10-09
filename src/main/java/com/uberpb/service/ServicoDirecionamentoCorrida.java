package com.uberpb.service;

import com.uberpb.model.Corrida;
import com.uberpb.model.Motorista;
import com.uberpb.model.Veiculo;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Decide a quem direcionar a corrida (RF09: mais próximo dentro da categoria).
 */
public class ServicoDirecionamentoCorrida {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioCorrida repositorioCorrida;

    public ServicoDirecionamentoCorrida(RepositorioUsuario ru, RepositorioCorrida rc) {
        this.repositorioUsuario = Objects.requireNonNull(ru);
        this.repositorioCorrida = Objects.requireNonNull(rc);
    }

    /** Filtra motoristas ativos que atendem a categoria da corrida. */
    public List<Motorista> filtrarCandidatos(Corrida corrida) {
        return repositorioUsuario.buscarTodos().stream()
                .filter(u -> u instanceof Motorista)
                .map(u -> (Motorista)u)
                .filter(Motorista::isContaAtiva)
                .filter(m -> atendeCategoria(m.getVeiculo(), corrida))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean atendeCategoria(Veiculo v, Corrida c) {
        if (v == null || v.getCategoriasDisponiveis() == null) return false;
        if (c.getCategoriaEscolhida() == null) return true; // fallback: qualquer
        return v.getCategoriasDisponiveis().contains(c.getCategoriaEscolhida());
    }

    /** Escolhe o mais próximo da ORIGEM da corrida. */
    public String escolherMotoristaMaisProximo(Corrida corrida, List<Motorista> candidatos, ServicoLocalizacao sl) {
        var origem = sl.geocodificar(
                corrida.getOrigem() != null ? corrida.getOrigem().toString() : corrida.getOrigemEndereco());

        return candidatos.stream()
                .map(m -> new ParMotoristaDist(m.getEmail(),
                        sl.distanciaKm(sl.obterLocalizacaoAtual(m.getEmail()), origem)))
                .min(Comparator.comparingDouble(p -> p.dist))
                .map(p -> p.email)
                .orElse(null);
    }

    private static class ParMotoristaDist {
        final String email; final double dist;
        ParMotoristaDist(String email, double dist) { this.email = email; this.dist = dist; }
    }
}
