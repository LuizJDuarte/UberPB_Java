package com.uberpb.repository;

import com.uberpb.model.Notificacao;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação em arquivo do RepositorioNotificacao (RF22)
 */
public class ImplRepositorioNotificacaoArquivo extends BaseRepositorioArquivo implements RepositorioNotificacao {

    private static final String ARQUIVO = "notificacoes.txt";
    private final Path caminho = prepararArquivoEmData(ARQUIVO);
    private final List<Notificacao> cache = new ArrayList<>();

    private static final ImplRepositorioNotificacaoArquivo INSTANCE = new ImplRepositorioNotificacaoArquivo();

    private ImplRepositorioNotificacaoArquivo() {
        carregar();
    }

    public static ImplRepositorioNotificacaoArquivo getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void salvar(Notificacao notificacao) {
        cache.add(notificacao);
        gravar();
    }

    @Override
    public synchronized List<Notificacao> buscarPorDestinatario(String email) {
        return cache.stream()
                .filter(n -> n.getDestinatarioEmail().equalsIgnoreCase(email))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<Notificacao> buscarNaoLidasPorDestinatario(String email) {
        return cache.stream()
                .filter(n -> n.getDestinatarioEmail().equalsIgnoreCase(email))
                .filter(n -> !n.isLida())
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<Notificacao> listarTodas() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized Notificacao buscarPorId(String id) {
        return cache.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public synchronized void atualizar(Notificacao notificacao) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(notificacao.getId())) {
                cache.set(i, notificacao);
                gravar();
                return;
            }
        }
    }

    private void carregar() {
        lerLinhas(caminho, linha -> {
            Notificacao n = Notificacao.fromString(linha);
            if (n != null)
                cache.add(n);
        });
    }

    private void gravar() {
        gravarAtomico(caminho, cache, Notificacao::toStringParaPersistencia);
    }
}
