package com.uberpb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    void deveRetornarEmailESenhaCorretamente() {
        Entregador usuario = new Entregador("email@test.com", "hash123");

        assertEquals("email@test.com", usuario.getEmail());
        assertEquals("hash123", usuario.getSenhaHash());
    }

    @Test
    void deveAlterarEmailESenha() {
        Entregador usuario = new Entregador("email@test.com", "hash123");

        usuario.setEmail("novo@email.com");
        usuario.setSenhaHash("novoHash");

        assertEquals("novo@email.com", usuario.getEmail());
        assertEquals("novoHash", usuario.getSenhaHash());
    }

    @Test
    void deveGerarToStringCorreto() {

        Usuario usuario = new Usuario("email@test.com", "hash123") {
            @Override
            public String toStringParaPersistencia() {
                return "";
            }
        };

        assertEquals("Email: email@test.com", usuario.toString());
    }

    @Test
    void deveRetornarTipoEntregador() {
        Entregador usuario = new Entregador("email@test.com", "hash123");
        assertEquals(TipoUsuario.ENTREGADOR, usuario.getTipo());
    }

    @Test
    void deveRetornarTipoAdministrador() {
        Administrador admin = new Administrador("admin@email.com", "123");
        assertEquals(TipoUsuario.ADMIN, admin.getTipo());
    }

    @Test
    void deveRetornarTipoMotorista() {
        Motorista motorista = new Motorista("m@email.com", "123");
        assertEquals(TipoUsuario.MOTORISTA, motorista.getTipo());
    }

    @Test
    void deveRetornarTipoRestaurante() {
        Restaurante restaurante = new Restaurante("r@email.com", "123");
        assertEquals(TipoUsuario.RESTAURANTE, restaurante.getTipo());
    }

    @Test
    void deveRetornarTipoPassageiroPadrao() {
        Passageiro passageiro = new Passageiro("p@email.com", "123");
        assertEquals(TipoUsuario.PASSAGEIRO_CLIENTE, passageiro.getTipo());
    }
}