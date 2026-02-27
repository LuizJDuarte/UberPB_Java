package com.uberpb.test;

import com.uberpb.model.Passageiro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassageiroTest {

    @Test
    void testConstrutorEPadroes() {
        Passageiro p = new Passageiro("teste@passageiro.com", "senha123");

        assertEquals("teste@passageiro.com", p.getEmail());
        assertEquals("senha123", p.getSenhaHash());
        assertTrue(p.isContaAtiva());
        assertEquals(0.0, p.getRatingMedio(), 0.01);
        assertEquals(0, p.getTotalAvaliacoes());
    }

    @Test
    void testGettersESetters() {
        Passageiro p = new Passageiro("a@b.com", "123");

        p.setContaAtiva(false);
        assertFalse(p.isContaAtiva());

        p.setTotalAvaliacoes(5);
        assertEquals(5, p.getTotalAvaliacoes());

        p.setRatingMedio(4.567);
        assertEquals(4.6, p.getRatingMedio(), 0.01);
    }

    @Test
    void testAdicionarAvaliacaoSimples() {
        Passageiro p = new Passageiro("a@b.com", "123");
        p.adicionarAvaliacao(5);

        assertEquals(1, p.getTotalAvaliacoes());
        assertEquals(5.0, p.getRatingMedio(), 0.01);
    }

    @Test
    void testAdicionarAvaliacaoMultipla() {
        Passageiro p = new Passageiro("a@b.com", "123");
        p.adicionarAvaliacao(5);
        p.adicionarAvaliacao(4);
        p.adicionarAvaliacao(3);

        assertEquals(3, p.getTotalAvaliacoes());
        assertEquals(4.0, p.getRatingMedio(), 0.01);
    }

    @Test
    void testAdicionarAvaliacaoComArredondamento() {
        Passageiro p = new Passageiro("a@b.com", "123");
        p.adicionarAvaliacao(4);
        p.adicionarAvaliacao(5);
        p.adicionarAvaliacao(4);

        // média: (4+5+4)/3 = 4.333..., arredondado para 4.3
        assertEquals(4.3, p.getRatingMedio(), 0.01);
    }

    @Test
    void testToStringParaPersistenciaSemAvaliacoes() {
        Passageiro p = new Passageiro("teste@passageiro.com", "senha123");
        String persistencia = p.toStringParaPersistencia();
        assertTrue(persistencia.startsWith("PASSAGEIRO,teste@passageiro.com,senha123"));
        assertTrue(persistencia.contains("0.0"));
        assertTrue(persistencia.contains("0"));
        assertTrue(persistencia.contains("true"));
    }

    @Test
    void testToStringParaPersistenciaComAvaliacoes() {
        Passageiro p = new Passageiro("teste@passageiro.com", "senha123");
        p.adicionarAvaliacao(5);
        p.adicionarAvaliacao(4);
        String persistencia = p.toStringParaPersistencia();
        assertTrue(persistencia.contains("4.5"));
        assertTrue(persistencia.contains("2"));
        assertTrue(persistencia.contains("true"));
    }

    @Test
    void testToStringSemAvaliacoes() {
        Passageiro p = new Passageiro("teste@passageiro.com", "senha123");
        String str = p.toString();

        // Não depende do símbolo ⭐
        assertTrue(str.contains("Sem avaliações"));
        assertTrue(str.contains("Ativa"));
        assertTrue(str.contains("teste@passageiro.com"));
    }

    @Test
    void testToStringComAvaliacoes() {
        Passageiro p = new Passageiro("teste@passageiro.com", "senha123");
        p.adicionarAvaliacao(5);
        p.adicionarAvaliacao(4);

        // Verifique o valor numérico real
        assertEquals(4.5, p.getRatingMedio(), 0.01);
        assertEquals(2, p.getTotalAvaliacoes());
        assertTrue(p.isContaAtiva());
        assertEquals("teste@passageiro.com", p.getEmail());

        // Apenas garanta que o toString() contém "Passageiro" e "Status"
        String str = p.toString();
        assertTrue(str.contains("Passageiro"));
        assertTrue(str.contains("Status"));
    }

    @Test
    void testAdicionarAvaliacaoExtremos() {
        Passageiro p = new Passageiro("a@b.com", "123");

        // rating mínimo
        p.adicionarAvaliacao(0);
        assertEquals(0, p.getRatingMedio(), 0.01);

        // rating máximo
        p.adicionarAvaliacao(5);
        assertEquals(2.5, p.getRatingMedio(), 0.01);

        // rating médio arredondado
        p.adicionarAvaliacao(4);
        assertEquals(3.0, p.getRatingMedio(), 0.01);
    }

    @Test
    void testContaInativa() {
        Passageiro p = new Passageiro("teste@passageiro.com", "senha123");
        p.setContaAtiva(false);
        assertFalse(p.isContaAtiva());

        p.adicionarAvaliacao(5);
        assertEquals(1, p.getTotalAvaliacoes());
        assertEquals(5.0, p.getRatingMedio(), 0.01);
    }
}