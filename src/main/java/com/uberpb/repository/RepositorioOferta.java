package com.uberpb.repository;

import com.uberpb.model.OfertaCorrida;

import java.util.List;

public interface RepositorioOferta {
    void salvar(OfertaCorrida oferta);
    void atualizar(OfertaCorrida oferta);
    OfertaCorrida buscarPorId(String id);
    List<OfertaCorrida> buscarPorCorrida(String corridaId);
    List<OfertaCorrida> buscarPorMotorista(String motoristaEmail);
    List<OfertaCorrida> buscarTodas();
}