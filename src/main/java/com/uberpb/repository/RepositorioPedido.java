package com.uberpb.repository;

import com.uberpb.model.Pedido;
import java.util.List;

public interface RepositorioPedido {

    void salvar(Pedido pedido);

    List<Pedido> listarTodos();

    List<Pedido> buscarPorCliente(String email);

    List<Pedido> buscarPorRestaurante(String email);

    /**
     * RF24: Busca pedidos atribuídos a um entregador
     */
    List<Pedido> buscarPorEntregador(String emailEntregador);

    /**
     * RF24: Busca pedidos que ainda não foram aceitos/recusados e têm entregador
     * alocado
     */
    List<Pedido> buscarPedidosDisponiveisParaEntregador(String emailEntregador);

    /**
     * RF24: Atualiza um pedido existente
     */
    void atualizar(Pedido pedido);

    List<Pedido> buscarPedidosDoRestaurante(String email);
}
