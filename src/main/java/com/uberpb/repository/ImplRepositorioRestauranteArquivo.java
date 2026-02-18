package com.uberpb.repository;


import com.uberpb.model.Restaurante;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementação do repositório de restaurantes que persiste dados em arquivo.
 */
public class ImplRepositorioRestauranteArquivo extends BaseRepositorioArquivo implements RepositorioRestaurante {


    private static final String ARQUIVO = "restaurantes.txt";
    private final Path caminho = prepararArquivoEmData(ARQUIVO);
    private final List<Restaurante> cache = new ArrayList<>();


    private static final ImplRepositorioRestauranteArquivo INSTANCE = new ImplRepositorioRestauranteArquivo();


    private ImplRepositorioRestauranteArquivo() {
        carregar();
    }


    public static ImplRepositorioRestauranteArquivo getInstance() {
        return INSTANCE;
    }


    @Override
    public synchronized void salvar(Restaurante restaurante) {
        // Remove duplicata e salva (Upsert)
        cache.removeIf(r -> r.getEmail().equalsIgnoreCase(restaurante.getEmail()));
        cache.add(restaurante);
        gravar();
    }


    @Override
    public synchronized List<Restaurante> listarTodos() {
        return new ArrayList<>(cache);
    }


    @Override
    public synchronized Restaurante buscarPorId(String id) {
        return cache.stream()
            .filter(r -> r.getEmail().equalsIgnoreCase(id))
            .findFirst()
            .orElse(null);
    }


    private void carregar() {
        lerLinhas(caminho, linha -> {
            Restaurante r = Restaurante.fromString(linha);
            if (r != null) cache.add(r);
        });
    }


    private void gravar() {
        gravarAtomico(caminho, cache, Restaurante::toStringParaPersistencia);
    }
}