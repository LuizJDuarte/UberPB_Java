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

    /**
     * RF24: Busca pedidos atribuídos a um entregador
     */
    public List<Pedido> buscarPorEntregador(String emailEntregador) {
        return repositorio.buscarPorEntregador(emailEntregador);
    }

    /**
     * RF24: Busca pedidos disponíveis para aceitação de um entregador
     */
    public List<Pedido> buscarPedidosDisponiveisParaEntregador(String emailEntregador) {
        return repositorio.buscarPedidosDisponiveisParaEntregador(emailEntregador);
    }

    /**
     * RF24: Atualiza o status de um pedido
     */
    public void atualizarPedido(Pedido pedido) {
        repositorio.atualizar(pedido);
    }
}
