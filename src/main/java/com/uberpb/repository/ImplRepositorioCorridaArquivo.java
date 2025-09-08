package com.uberpb.repository;

import com.uberpb.model.Corrida;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImplRepositorioCorridaArquivo extends BaseRepositorioArquivo implements RepositorioCorrida {

    private static final String ARQUIVO = "corridas.txt";
    private final Path caminho = prepararArquivoEmData(ARQUIVO);
    private final List<Corrida> cache = new ArrayList<>();

    public ImplRepositorioCorridaArquivo() {
        carregar();
    }

    @Override
    public synchronized void salvar(Corrida corrida) {
        cache.add(corrida);
        gravar();
    }

    @Override
    public synchronized void atualizar(Corrida corrida) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(corrida.getId())) {
                cache.set(i, corrida);
                gravar();
                return;
            }
        }
        throw new IllegalArgumentException("Corrida não encontrada: " + corrida.getId());
    }

    @Override
    public synchronized Corrida buscarPorId(String id) {
        return cache.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public synchronized List<Corrida> buscarPorPassageiro(String emailPassageiro) {
        List<Corrida> l = new ArrayList<>();
        for (Corrida c : cache) if (c.getEmailPassageiro().equalsIgnoreCase(emailPassageiro)) l.add(c);
        return l;
    }

    @Override
    public synchronized List<Corrida> buscarTodas() {
        return new ArrayList<>(cache);
    }

    // ===== IO =====

    private void carregar() {
        lerLinhas(caminho, linha -> cache.add(Corrida.fromStringGenerico(linha)));
    }

    private void gravar() {
        gravarAtomico(caminho, cache, Corrida::toStringParaPersistencia);
    }
}
