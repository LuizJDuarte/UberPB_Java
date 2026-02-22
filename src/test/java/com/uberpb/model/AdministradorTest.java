package com.uberpb.test;

import com.uberpb.model.Administrador;
import com.uberpb.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdministradorTest {

    @Test
    void testInstanciacao() {
        Administrador admin = new Administrador("admin@uberpb.com", "senhaHash123");
        assertNotNull(admin); // verifica se o objeto foi criado
        assertTrue(admin instanceof Usuario); // verifica herança
    }

    @Test
    void testToString() {
        Administrador admin = new Administrador("admin@uberpb.com", "senhaHash123");
        String esperado = "[ADMIN] admin@uberpb.com";
        assertEquals(esperado, admin.toString());
    }

    @Test
    void testToStringParaPersistencia() {
        Administrador admin = new Administrador("admin@uberpb.com", "senhaHash123");
        String esperado = "ADMIN,admin@uberpb.com,senhaHash123";
        assertEquals(esperado, admin.toStringParaPersistencia());
    }

    @Test
    void testComDadosDiferentes() {
        // Testando com outro email e senha
        Administrador admin = new Administrador("teste@uberpb.com", "abc123");
        assertEquals("[ADMIN] teste@uberpb.com", admin.toString());
        assertEquals("ADMIN,teste@uberpb.com,abc123", admin.toStringParaPersistencia());
    }

    @Test
    void testCamposNaoNulos() {
        Administrador admin = new Administrador("a@b.com", "123");
        assertNotNull(admin.toString());
        assertNotNull(admin.toStringParaPersistencia());
    }
}