package com.uberpb.service;

import java.util.Comparator;
import java.util.List;

import com.uberpb.model.Localizacao;
import com.uberpb.model.Restaurante;

public class ServicoLocalRestaurante {
    public List<Restaurante> listarRestaurantesProximos(String emailPassageiro, List<Restaurante> todosRestaurantes) {
    ServicoLocalizacao servicoLocalizacao = new ServicoLocalizacao();
    
    Localizacao locPassageiro = servicoLocalizacao.obterLocalizacaoAtual(emailPassageiro);

    return todosRestaurantes.stream()
        .filter(r -> servicoLocalizacao.distanciaKm(locPassageiro, r.getLocalizacao()) <= 10.0) // Raio de 10km, por exemplo
        .sorted(Comparator.comparingDouble(r -> servicoLocalizacao.distanciaKm(locPassageiro, r.getLocalizacao())))
        .toList();
}
}
