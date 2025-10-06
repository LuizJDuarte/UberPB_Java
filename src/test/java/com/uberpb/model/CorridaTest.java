package com.uberpb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CorridaTest {

    @Test
    public void testCorridaPersistence() {
        Localizacao origem = new Localizacao(-7.115, -34.86);
        Localizacao destino = new Localizacao(-7.125, -34.87);
        Corrida corridaOriginal = Corrida.novaComCoordenadas("test@test.com", origem, destino, CategoriaVeiculo.UBERX, MetodoPagamento.PIX);
        corridaOriginal.setStatus(CorridaStatus.CONCLUIDA);
        corridaOriginal.setMotoristaAlocado("driver@test.com");

        String persisted = corridaOriginal.toStringParaPersistencia();
        Corrida corridaRestaurada = Corrida.fromStringGenerico(persisted);

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