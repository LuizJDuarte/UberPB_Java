package com.uberpb.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class MotoristaTest {

    @Test
    void deveCriarMotoristaComValoresPadrao() {
        Motorista m = new Motorista("email@test.com", "senha");

        assertFalse(m.isContaAtiva());
        assertFalse(m.isCnhValida());
        assertFalse(m.isCrlvValido());
        assertFalse(m.isDisponivel());
        assertEquals(0.0, m.getRatingMedio());
        assertEquals(0, m.getTotalAvaliacoes());
        assertNull(m.getVeiculo());
    }

    @Test
    void deveSetarDadosCorretamente() {
        Motorista m = new Motorista("email@test.com", "senha");

        m.setContaAtiva(true);
        m.setCnhValida(true);
        m.setCrlvValido(true);
        m.setDisponivel(true);
        m.setRatingMedio(4.567); // testa arredondamento
        m.setTotalAvaliacoes(10);

        assertTrue(m.isContaAtiva());
        assertTrue(m.isCnhValida());
        assertTrue(m.isCrlvValido());
        assertTrue(m.isDisponivel());
        assertEquals(4.6, m.getRatingMedio()); // arredondado
        assertEquals(10, m.getTotalAvaliacoes());
    }

    @Test
    void deveAdicionarAvaliacaoCorretamente() {
        Motorista m = new Motorista("email@test.com", "senha");

        m.adicionarAvaliacao(5);
        assertEquals(1, m.getTotalAvaliacoes());
        assertEquals(5.0, m.getRatingMedio());

        m.adicionarAvaliacao(3);
        assertEquals(2, m.getTotalAvaliacoes());
        assertEquals(4.0, m.getRatingMedio()); // média
    }

    @Test
    void deveGerarPersistenciaSemLocalizacaoESemVeiculo() {
        Motorista m = new Motorista("email@test.com", "senha");

        String texto = m.toStringParaPersistencia();

        assertTrue(texto.contains("MOTORISTA"));
        assertTrue(texto.contains("email@test.com"));
        assertTrue(texto.contains("senha"));
        assertTrue(texto.contains("0.0,0.0"));
        assertTrue(texto.endsWith("null"));
    }

    @Test
    void deveGerarPersistenciaComLocalizacao() throws Exception {
        Motorista m = new Motorista("email@test.com", "senha");

        setLocalizacao(m, new Localizacao(10.0, 20.0));

        String texto = m.toStringParaPersistencia();

        assertTrue(texto.contains("10.0"));
        assertTrue(texto.contains("20.0"));
    }

    @Test
    void deveGerarPersistenciaComVeiculo() {
        Motorista m = new Motorista("email@test.com", "senha");

        // ✅ CONSTRUTOR CORRETO (6 PARÂMETROS)
        Veiculo v = new Veiculo("ABC1234", 2020, "Gol", "Branco", 4, "DOC123");
        m.setVeiculo(v);

        String texto = m.toStringParaPersistencia();

        assertTrue(texto.contains("ABC1234"));
    }

    @Test
    void deveGerarToStringSemAvaliacoes() {
        Motorista m = new Motorista("email@test.com", "senha");

        String texto = m.toString();

        assertTrue(texto.contains("Sem avaliações"));
    }

    @Test
    void deveGerarToStringComAvaliacoesELocalizacao() throws Exception {
        Motorista m = new Motorista("email@test.com", "senha");

        m.adicionarAvaliacao(5);
        m.setContaAtiva(true);
        m.setDisponivel(true);

        setLocalizacao(m, new Localizacao(1.0, 2.0));

        String texto = m.toString();

        assertTrue(texto.contains("⭐"));
        assertTrue(texto.contains("Loc"));
        assertTrue(texto.contains("Online"));
        assertTrue(texto.contains("Ativa"));
    }

    // helper pra setar localização sem mexer na classe
    private void setLocalizacao(Motorista m, Localizacao loc) throws Exception {
        Field field = Usuario.class.getDeclaredField("localizacao");
        field.setAccessible(true);
        field.set(m, loc);
    }
}