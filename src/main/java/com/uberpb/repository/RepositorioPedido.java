package com.uberpb.repository;

import com.uberpb.model.Pedido;
import java.util.List;

public interface RepositorioPedido {

    void salvar(Pedido pedido);

    List<Pedido> listarTodos();

    List<Pedido> buscarPorCliente(String email);

    List<Pedido> buscarPorRestaurante(String email);
}
