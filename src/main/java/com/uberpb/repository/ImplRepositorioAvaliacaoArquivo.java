package com.uberpb.repository;

import com.uberpb.model.Avaliacao;
import com.uberpb.model.AvaliacaoMotorista;
import com.uberpb.model.AvaliacaoPassageiro;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImplRepositorioAvaliacaoArquivo extends BaseRepositorioArquivo implements RepositorioAvaliacao {

    private static final String ARQUIVO = "avaliacoes.txt";
    private final Path caminho = prepararArquivoEmData(ARQUIVO);
    private final List<Avaliacao> cache = new ArrayList<>();

    public ImplRepositorioAvaliacaoArquivo() {
        carregar();
    }

    @Override
    public synchronized void salvar(Avaliacao avaliacao) {
        cache.add(avaliacao);
        gravar();
    }

    @Override
    public synchronized List<Avaliacao> buscarPorMotorista(String motoristaEmail) {
        List<Avaliacao> resultado = new ArrayList<>();
        for (Avaliacao av : cache) {
            if (av instanceof AvaliacaoPassageiro ap && ap.getMotoristaEmail().equalsIgnoreCase(motoristaEmail)) {
                resultado.add(ap);
            }
        }
        return resultado;
    }

    @Override
    public synchronized List<Avaliacao> buscarPorPassageiro(String passageiroEmail) {
        List<Avaliacao> resultado = new ArrayList<>();
        for (Avaliacao av : cache) {
            if (av instanceof AvaliacaoMotorista am && am.getPassageiroEmail().equalsIgnoreCase(passageiroEmail)) {
                resultado.add(am);
            }
        }
        return resultado;
    }

    @Override
    public synchronized List<Avaliacao> buscarPorCorrida(String corridaId) {
        List<Avaliacao> resultado = new ArrayList<>();
        for (Avaliacao av : cache) {
            if (av.getCorridaId().equals(corridaId)) {
                resultado.add(av);
            }
        }
        return resultado;
    }

    @Override
    public synchronized List<Avaliacao> buscarTodas() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized boolean corridaFoiAvaliada(String corridaId) {
        return cache.stream().anyMatch(av -> av.getCorridaId().equals(corridaId));
    }

    // ===== IO =====

    private void carregar() {
        lerLinhas(caminho, this::parseLinhaAvaliacao);
    }

    private void gravar() {
        gravarAtomico(caminho, cache, Avaliacao::toStringParaPersistencia);
    }

    private void parseLinhaAvaliacao(String linha) {
        if (linha.startsWith("PASSAGEIRO_TO_MOTORISTA")) {
            cache.add(AvaliacaoPassageiro.fromStringParaPersistencia(linha));
        } else if (linha.startsWith("MOTORISTA_TO_PASSAGEIRO")) {
            cache.add(AvaliacaoMotorista.fromStringParaPersistencia(linha));
        }
    }
}