package com.uberpb.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestauranteTest {

    @Test
    public void testConstrutorValoresPadrao() {
        Restaurante r = new Restaurante("rest@teste.com", "hash123");

        assertEquals("rest@teste.com", r.getEmail());
        assertEquals("hash123", r.getSenhaHash());
        assertEquals("", r.getNomeFantasia());
        assertEquals("", r.getCnpj());
        assertTrue(r.isContaAtiva());
        assertEquals(0.0, r.getTaxaEntrega());
        assertEquals(30, r.getTempoEstimadoEntregaMinutos());
        assertNotNull(r.getCardapio());
        assertEquals(0, r.getCardapio().size());
    }

    @Test
    public void testAdicionarItemCardapio() {
        Restaurante r = new Restaurante("rest@teste.com", "hash");
        ItemCardapio item = new ItemCardapio("Pizza", "Calabresa", 40.0);

        r.adicionarItemCardapio(item);

        assertEquals(1, r.getCardapio().size());
        assertEquals("Pizza", r.getCardapio().get(0).getNome());
    }

    @Test
    public void testToStringParaPersistenciaComCardapio() {
        Restaurante r = new Restaurante("rest@teste.com", "hash");
        r.setNomeFantasia("Pizzaria");
        r.setCnpj("123456");
        r.setTaxaEntrega(5.5);
        r.setTempoEstimadoEntregaMinutos(45);

        ItemCardapio item = new ItemCardapio("Pizza", "Calabresa", 40.0);
        r.adicionarItemCardapio(item);

        String persistencia = r.toStringParaPersistencia();

        assertTrue(persistencia.contains("RESTAURANTE"));
        assertTrue(persistencia.contains("Pizzaria"));
        assertTrue(persistencia.contains("123456"));
        assertTrue(persistencia.contains("5.50"));
        assertTrue(persistencia.contains("45"));
        assertTrue(persistencia.contains("Pizza#Calabresa#40.00"));
    }

    @Test
    public void testToStringParaPersistenciaSemCardapio() {
        Restaurante r = new Restaurante("rest@teste.com", "hash");

        String persistencia = r.toStringParaPersistencia();

        assertTrue(persistencia.contains("VAZIO"));
    }

    @Test
    public void testFromStringValidoComCardapio() {
        String linha = "RESTAURANTE,rest@teste.com,hash,Pizzaria,123456,true,1.0,2.0,5.50,45,Pizza#Calabresa#40.00";

        Restaurante r = Restaurante.fromString(linha);

        assertNotNull(r);
        assertEquals("rest@teste.com", r.getEmail());
        assertEquals("Pizzaria", r.getNomeFantasia());
        assertEquals("123456", r.getCnpj());
        assertTrue(r.isContaAtiva());
        assertEquals(5.50, r.getTaxaEntrega());
        assertEquals(45, r.getTempoEstimadoEntregaMinutos());
        assertEquals(1, r.getCardapio().size());
    }

    @Test
    public void testFromStringValidoSemCardapio() {
        String linha = "RESTAURANTE,rest@teste.com,hash,Pizzaria,123456,true,1.0,2.0,5.50,45,VAZIO";

        Restaurante r = Restaurante.fromString(linha);

        assertNotNull(r);
        assertEquals(0, r.getCardapio().size());
    }

    @Test
    public void testFromStringInvalido() {
        assertNull(Restaurante.fromString(null));
        assertNull(Restaurante.fromString("INVALIDO"));
        assertNull(Restaurante.fromString("RESTAURANTE,apenas,3,campos"));
    }

    @Test
    public void testToString() {
        Restaurante r = new Restaurante("rest@teste.com", "hash");
        r.setNomeFantasia("Pizzaria");
        r.setCnpj("123456");
        r.setTaxaEntrega(7.0);
        r.setContaAtiva(false);

        String esperado = String.format(
                "%s (CNPJ: %s) [Taxa: R$ %.2f, %s]",
                "Pizzaria",
                "123456",
                7.0,
                "Inativa");

        assertEquals(esperado, r.toString());
    }
}