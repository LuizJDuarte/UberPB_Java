package com.uberpb.repository;

import com.uberpb.model.Corrida;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImplRepositorioCorridaArquivo extends BaseRepositorioArquivo implements RepositorioCorrida {

    private static final String ARQUIVO = "corridas.txt";
    private final Path caminho = prepararArquivoEmData(ARQUIVO);
    private final List<Corrida> cache = new ArrayList<>();

    private static final ImplRepositorioCorridaArquivo INSTANCE = new ImplRepositorioCorridaArquivo();

    private ImplRepositorioCorridaArquivo() {
        carregar();
    }

    public static ImplRepositorioCorridaArquivo getInstance() {
        return INSTANCE;
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
    public synchronized void remover(String id) {
        cache.removeIf(c -> c.getId().equals(id));
        gravar();
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

    @Override
    public void limpar() {
        cache.clear();
        gravar();
    }

    @Override
    public synchronized Corrida buscarCorridaAtivaPorPassageiro(String email) {
        return cache.stream()
            .filter(c -> c.getEmailPassageiro().equalsIgnoreCase(email))
            .filter(c -> {
                var status = c.getStatus();
                return status == com.uberpb.model.CorridaStatus.SOLICITADA ||
                       status == com.uberpb.model.CorridaStatus.ACEITA ||
                       status == com.uberpb.model.CorridaStatus.EM_ANDAMENTO;
            })
            .findFirst()
            .orElse(null);
    }

    // ===== IO =====

    private void carregar() {
        lerLinhas(caminho, linha -> cache.add(Corrida.fromStringGenerico(linha)));
    }

    private void gravar() {
        gravarAtomico(caminho, cache, Corrida::toStringParaPersistencia);
    }

}
