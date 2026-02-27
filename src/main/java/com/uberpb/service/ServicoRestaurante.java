package com.uberpb.service;


import com.uberpb.model.ItemCardapio;
import com.uberpb.model.Restaurante;
import com.uberpb.repository.RepositorioRestaurante;
import java.util.List;


/**
 * Serviço para gerenciar a lógica de negócio de restaurantes e cardápios.
 */
public class ServicoRestaurante {


    private RepositorioRestaurante repositorioRestaurante;


    public ServicoRestaurante(RepositorioRestaurante repositorioRestaurante) {
        this.repositorioRestaurante = repositorioRestaurante;
    }


    public Restaurante buscarPorEmail(String email) {
        return repositorioRestaurante.buscarPorId(email);
    }


    public List<Restaurante> listarTodos() {
        return repositorioRestaurante.listarTodos();
    }


    public void adicionarItemAoCardapio(String emailRestaurante, String nome, String descricao, double preco) {
        Restaurante r = buscarPorEmail(emailRestaurante);
        if (r != null) {
            r.adicionarItemCardapio(new ItemCardapio(nome, descricao, preco));
            repositorioRestaurante.salvar(r);
        }
    }


    public void atualizarInfoEntrega(String emailRestaurante, double taxa, int tempo) {
        Restaurante r = buscarPorEmail(emailRestaurante);
        if (r != null) {
            r.setTaxaEntrega(taxa);
            r.setTempoEstimadoEntregaMinutos(tempo);
            repositorioRestaurante.salvar(r);
        }
    }
}
