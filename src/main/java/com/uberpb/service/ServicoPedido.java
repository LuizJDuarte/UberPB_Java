package com.uberpb.service;

import java.util.List;

import com.uberpb.model.Pedido;
import com.uberpb.repository.RepositorioPedido;

public class ServicoPedido {

    private RepositorioPedido repositorio;

    public ServicoPedido(RepositorioPedido repositorio) {
        this.repositorio = repositorio;
    }

    public void salvarPedido(Pedido pedido) {
        repositorio.salvar(pedido);
    }

    public List<Pedido> buscarPorCliente(String email) {
        return repositorio.buscarPorCliente(email);
    }

    public List<Pedido> buscarPorRestaurante(String email) {
        return repositorio.buscarPorRestaurante(email);
    }

    public List<Pedido> listarTodos() {
        return repositorio.listarTodos();
    }
}
