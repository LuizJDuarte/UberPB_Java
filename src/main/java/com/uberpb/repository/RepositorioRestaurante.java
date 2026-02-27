package com.uberpb.repository;

import com.uberpb.model.Restaurante; // Certifique-se de ter a model Restaurante
import java.util.List;

public interface RepositorioRestaurante {
    void salvar(Restaurante restaurante);
    List<Restaurante> listarTodos();
    Restaurante buscarPorId(String id);
}