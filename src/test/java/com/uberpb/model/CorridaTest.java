package com.uberpb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CorridaTest {

    @Test
    public void testToStringParaPersistencia() {
        Localizacao origem = new Localizacao(1.0, 2.0);
        Localizacao destino = new Localizacao(3.0, 4.0);
        Corrida corridaOriginal = Corrida.novaComCoordenadas("passageiro@teste.com", origem, destino, CategoriaVeiculo.BLACK, MetodoPagamento.CARTAO);
        corridaOriginal.setMotoristaAlocado("motorista@teste.com");

        String persistencia = corridaOriginal.toStringParaPersistencia();
        Corrida corridaRestaurada = Corrida.fromStringGenerico(persistencia);

        assertNotNull(corridaRestaurada);
        assertEquals(corridaOriginal.getId(), corridaRestaurada.getId());
        assertEquals(corridaOriginal.getEmailPassageiro(), corridaRestaurada.getEmailPassageiro());
        assertEquals(corridaOriginal.getStatus(), corridaRestaurada.getStatus());
        assertEquals(corridaOriginal.getOrigem().latitude(), corridaRestaurada.getOrigem().latitude(), 0.001);
        assertEquals(corridaOriginal.getOrigem().longitude(), corridaRestaurada.getOrigem().longitude(), 0.001);
        assertEquals(corridaOriginal.getDestino().latitude(), corridaRestaurada.getDestino().latitude(), 0.001);
        assertEquals(corridaOriginal.getDestino().longitude(), corridaRestaurada.getDestino().longitude(), 0.001);
        assertEquals(corridaOriginal.getCategoriaEscolhida(), corridaRestaurada.getCategoriaEscolhida());
        assertEquals(corridaOriginal.getMetodoPagamento(), corridaRestaurada.getMetodoPagamento());
        assertEquals(corridaOriginal.getMotoristaAlocado(), corridaRestaurada.getMotoristaAlocado());
    }
}
