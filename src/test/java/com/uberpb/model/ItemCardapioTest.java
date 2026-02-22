package com.uberpb.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemCardapioTest {

    @Test
    public void testConstrutorEGetters() {
        ItemCardapio item = new ItemCardapio("Pizza", "Calabresa", 35.50);

        assertEquals("Pizza", item.getNome());
        assertEquals("Calabresa", item.getDescricao());
        assertEquals(35.50, item.getPreco());
    }

    @Test
    public void testToStringParaPersistencia() {
        ItemCardapio item = new ItemCardapio("Pizza", "Calabresa", 35.5);

        String esperado = "Pizza#Calabresa#35.50";

        assertEquals(esperado, item.toStringParaPersistencia());
    }

    @Test
    public void testToStringParaPersistenciaRemoveCaracteresInvalidos() {
        ItemCardapio item = new ItemCardapio("Pi#zza|", "Calab#resa|", 40);

        String resultado = item.toStringParaPersistencia();

        assertEquals("Pizza#Calabresa#40.00", resultado);
    }

    @Test
    public void testFromStringParaPersistenciaValido() {
        String dados = "Hamburguer#Artesanal#25.90";

        ItemCardapio item = ItemCardapio.fromStringParaPersistencia(dados);

        assertNotNull(item);
        assertEquals("Hamburguer", item.getNome());
        assertEquals("Artesanal", item.getDescricao());
        assertEquals(25.90, item.getPreco());
    }

    @Test
    public void testFromStringParaPersistenciaInvalidoFormatoErrado() {
        String dados = "Hamburguer#Artesanal";

        ItemCardapio item = ItemCardapio.fromStringParaPersistencia(dados);

        assertNull(item);
    }

    @Test
    public void testFromStringParaPersistenciaPrecoInvalido() {
        String dados = "Hamburguer#Artesanal#abc";

        ItemCardapio item = ItemCardapio.fromStringParaPersistencia(dados);

        assertNull(item);
    }

    @Test
    public void testFromStringParaPersistenciaNullOuVazio() {
        assertNull(ItemCardapio.fromStringParaPersistencia(null));
        assertNull(ItemCardapio.fromStringParaPersistencia(""));
        assertNull(ItemCardapio.fromStringParaPersistencia("   "));
    }

    @Test
    public void testToString() {
        ItemCardapio item = new ItemCardapio("Suco", "Laranja", 7.5);

        String resultado = item.toString();

        assertTrue(resultado.contains("Suco"));
        assertTrue(resultado.contains("Laranja"));
        assertTrue(resultado.contains("7")); 
    }
}