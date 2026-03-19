package com.uberpb.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntregadorTest {

    @Test
    void deveCriarEntregadorComValoresPadrao() {
        Entregador e = new Entregador("teste@email.com", "123");

        assertEquals("", e.getCnhNumero());
        assertEquals("", e.getCpfNumero());
        assertFalse(e.isCnhValida());
        assertFalse(e.isDocIdentidadeValido());
        assertFalse(e.isContaAtiva());
        assertFalse(e.isDisponivel());
    }

    @Test
    void deveSetarDadosCorretamente() {
        Entregador e = new Entregador("teste@email.com", "123");

        e.setCnhNumero("123456");
        e.setCpfNumero("99999999999");
        e.setCnhValida(true);
        e.setDocIdentidadeValido(true);
        e.setContaAtiva(true);
        e.setDisponivel(true);

        assertEquals("123456", e.getCnhNumero());
        assertEquals("99999999999", e.getCpfNumero());
        assertTrue(e.isCnhValida());
        assertTrue(e.isDocIdentidadeValido());
        assertTrue(e.isContaAtiva());
        assertTrue(e.isDisponivel());
    }

    @Test
    void deveGerarToStringParaPersistenciaSemLocalizacao() {
        Entregador e = new Entregador("email@test.com", "senha");

        String resultado = e.toStringParaPersistencia();

        assertTrue(resultado.contains("ENTREGADOR"));
        assertTrue(resultado.contains("email@test.com"));
        assertTrue(resultado.contains("senha"));
        assertTrue(resultado.endsWith("0.0,0.0"));
    }

    @Test
    void deveGerarToStringParaPersistenciaComLocalizacao() {
        Entregador e = new Entregador("email@test.com", "senha");

        // assumindo que Usuario tem setter ou acesso protegido
        e.localizacao = new Localizacao(10.0, 20.0);

        String resultado = e.toStringParaPersistencia();

        assertTrue(resultado.contains("10.0"));
        assertTrue(resultado.contains("20.0"));
    }

    @Test
    void deveGerarToStringCorretamente() {
        Entregador e = new Entregador("email@test.com", "senha");

        e.setContaAtiva(true);
        e.setDisponivel(true);
        e.setCnhNumero("123");
        e.setCpfNumero("999");
        e.setCnhValida(true);
        e.setDocIdentidadeValido(true);

        e.localizacao = new Localizacao(1.0, 2.0);

        String texto = e.toString();

        assertTrue(texto.contains("Entregador"));
        assertTrue(texto.contains("Ativa"));
        assertTrue(texto.contains("Online"));
        assertTrue(texto.contains("123"));
        assertTrue(texto.contains("999"));
        assertTrue(texto.contains("Loc"));
    }
}