package com.uberpb.repository;

import com.uberpb.model.OfertaCorrida;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImplRepositorioOfertaArquivo extends BaseRepositorioArquivo implements RepositorioOferta {

    private static final String ARQUIVO = "ofertas.txt";
    private final Path caminho = prepararArquivoEmData(ARQUIVO);
    private final List<OfertaCorrida> cache = new ArrayList<>();

    public ImplRepositorioOfertaArquivo() { carregar(); }

    @Override
    public synchronized void salvar(OfertaCorrida oferta) {
        cache.add(oferta);
        gravar();
    }

    @Override
    public synchronized void atualizar(OfertaCorrida oferta) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(oferta.getId())) {
                cache.set(i, oferta);
                gravar();
                return;
            }
        }
        throw new IllegalArgumentException("Oferta não encontrada: " + oferta.getId());
    }

    @Override
    public synchronized OfertaCorrida buscarPorId(String id) {
        return cache.stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public synchronized List<OfertaCorrida> buscarPorCorrida(String corridaId) {
        List<OfertaCorrida> out = new ArrayList<>();
        for (OfertaCorrida o : cache) if (o.getCorridaId().equalsIgnoreCase(corridaId)) out.add(o);
        return out;
    }

    @Override
    public synchronized List<OfertaCorrida> buscarPorMotorista(String motoristaEmail) {
        List<OfertaCorrida> out = new ArrayList<>();
        for (OfertaCorrida o : cache) if (o.getMotoristaEmail().equalsIgnoreCase(motoristaEmail)) out.add(o);
        return out;
    }

    @Override
    public synchronized List<OfertaCorrida> buscarTodas() { return new ArrayList<>(cache); }

    private void carregar() { lerLinhas(caminho, linha -> cache.add(OfertaCorrida.fromStringParaPersistencia(linha))); }

    private void gravar() { gravarAtomico(caminho, cache, OfertaCorrida::toStringParaPersistencia); }
}