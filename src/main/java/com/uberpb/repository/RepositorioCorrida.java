package com.uberpb.repository;

import com.uberpb.model.Corrida;
import java.util.List;

public interface RepositorioCorrida {
    void salvar(Corrida corrida);
    void atualizar(Corrida corrida);
    Corrida buscarPorId(String id);
    List<Corrida> buscarPorPassageiro(String emailPassageiro);
    List<Corrida> buscarTodas();
}
