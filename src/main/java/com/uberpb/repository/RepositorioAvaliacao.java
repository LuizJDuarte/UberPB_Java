package com.uberpb.repository;

import com.uberpb.model.Avaliacao;
import java.util.List;

public interface RepositorioAvaliacao {
    void salvar(Avaliacao avaliacao);
    List<Avaliacao> buscarPorMotorista(String motoristaEmail);
    List<Avaliacao> buscarPorPassageiro(String passageiroEmail);
    List<Avaliacao> buscarPorCorrida(String corridaId);
    List<Avaliacao> buscarTodas();
    boolean corridaFoiAvaliada(String corridaId);
    void limpar();
}