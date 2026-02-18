package com.uberpb.service;

import com.uberpb.model.CarrinhoCompras;
import com.uberpb.model.ItemCardapio;
import com.uberpb.model.Restaurante;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço que mantém os carrinhos de compra dos usuários em memória durante a execução.
 */
public class ServicoCarrinho {
    // Mapa de Email do Usuário -> Carrinho de Compras
    private Map<String, CarrinhoCompras> carrinhos = new HashMap<>();

    public CarrinhoCompras obterCarrinho(String emailUsuario) {
        return carrinhos.computeIfAbsent(emailUsuario, k -> new CarrinhoCompras());
    }

    public void adicionarAoCarrinho(String emailUsuario, Restaurante restaurante, ItemCardapio item, int quantidade) {
        CarrinhoCompras carrinho = obterCarrinho(emailUsuario);
        carrinho.adicionarItem(restaurante, item, quantidade);
    }

    public void limparCarrinho(String emailUsuario) {
        CarrinhoCompras carrinho = carrinhos.get(emailUsuario);
        if (carrinho != null) {
            carrinho.limpar();
        }
    }
}