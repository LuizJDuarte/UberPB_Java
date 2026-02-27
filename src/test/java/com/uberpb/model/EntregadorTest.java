package com.uberpb.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntregadorTest {

    @Test
    void deveCriarEntregadorComValoresPadrao() {

        Entregador e = new Entregador("email@test.com", "hash123");

        assertEquals("email@test.com", e.getEmail());
        assertEquals("hash123", e.getSenhaHash());

        assertEquals("", e.getCnhNumero());
        assertEquals("", e.getCpfNumero());
        assertFalse(e.isCnhValida());
        assertFalse(e.isDocIdentidadeValido());
        assertFalse(e.isContaAtiva());
    }

    @Test
    void deveAlterarDadosComSetters() {

        Entregador e = new Entregador("email@test.com", "hash123");

        e.setCnhNumero("123456");
        e.setCpfNumero("99999999999");
        e.setCnhValida(true);
        e.setDocIdentidadeValido(true);
        e.setContaAtiva(true);

        assertEquals("123456", e.getCnhNumero());
        assertEquals("99999999999", e.getCpfNumero());
        assertTrue(e.isCnhValida());
        assertTrue(e.isDocIdentidadeValido());
        assertTrue(e.isContaAtiva());
    }

    @Test
    void deveGerarStringParaPersistenciaCorretamente() {

        Entregador e = new Entregador("email@test.com", "hash123");
        e.setCnhNumero("123456");
        e.setCpfNumero("99999999999");
        e.setCnhValida(true);
        e.setDocIdentidadeValido(false);
        e.setContaAtiva(true);

        String esperado = "ENTREGADOR,email@test.com,hash123,123456,99999999999,true,false,true";

        assertEquals(esperado, e.toStringParaPersistencia());
    }

    @Test
    void deveGerarToStringLegivel() {

        Entregador e = new Entregador("email@test.com", "hash123");
        e.setCnhNumero("123456");
        e.setCpfNumero("99999999999");
        e.setCnhValida(true);
        e.setDocIdentidadeValido(false);
        e.setContaAtiva(true);

        String texto = e.toString();

        assertTrue(texto.contains("Entregador"));
        assertTrue(texto.contains("Conta: Ativa"));
        assertTrue(texto.contains("CNH: 123456 (OK)"));
        assertTrue(texto.contains("CPF: 99999999999 (Pendente)"));
    }
}