package com.uberpb.repository;

import com.uberpb.model.Pedido;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImplRepositorioPedidoArquivo extends BaseRepositorioArquivo implements RepositorioPedido {

    private static final String ARQUIVO = "pedidos.txt";
    private final Path caminho = prepararArquivoEmData(ARQUIVO);
    private final List<Pedido> cache = new ArrayList<>();

    private static final ImplRepositorioPedidoArquivo INSTANCE = new ImplRepositorioPedidoArquivo();

    private ImplRepositorioPedidoArquivo() {
        carregar();
    }

    public static ImplRepositorioPedidoArquivo getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized void salvar(Pedido pedido) {
        cache.add(pedido);
        gravar();
    }

    @Override
    public synchronized List<Pedido> listarTodos() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized List<Pedido> buscarPorCliente(String email) {
        return cache.stream()
                .filter(p -> p.getEmailCliente().equalsIgnoreCase(email))
                .toList();
    }

    @Override
    public synchronized List<Pedido> buscarPorRestaurante(String email) {
        return cache.stream()
                .filter(p -> p.getEmailRestaurante().equalsIgnoreCase(email))
                .toList();
    }

    private void carregar() {
        lerLinhas(caminho, linha -> {
            Pedido p = Pedido.fromString(linha);
            if (p != null) cache.add(p);
        });
    }

    private void gravar() {
        gravarAtomico(caminho, cache, Pedido::toStringParaPersistencia);
    }
}
