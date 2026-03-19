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
    void devePermitirValoresNulos() {
        Entregador usuario = new Entregador("email@test.com", "hash123");

        usuario.setEmail(null);
        usuario.setSenhaHash(null);

        assertNull(usuario.getEmail());
        assertNull(usuario.getSenhaHash());
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
    void deveGerarToStringComEmailNull() {
        Usuario usuario = new Usuario(null, "123") {
            @Override
            public String toStringParaPersistencia() {
                return "";
            }
        };

        assertTrue(usuario.toString().contains("Email: null"));
    }

    // 🔥 NOVO — cobre construtor (localizacao padrão)
    @Test
    void deveIniciarComLocalizacaoPadrao() {
        Entregador usuario = new Entregador("email@test.com", "123");

        Localizacao loc = usuario.getLocalizacao();

        assertNotNull(loc);
        assertEquals(0.0, loc.latitude());
        assertEquals(0.0, loc.longitude());
    }

    // 🔥 NOVO — getter
    @Test
    void deveRetornarLocalizacao() {
        Entregador usuario = new Entregador("email@test.com", "123");

        Localizacao loc = new Localizacao(5.5, 6.6);
        usuario.setLocalizacao(loc);

        assertEquals(loc, usuario.getLocalizacao());
    }

    // 🔥 NOVO — setter
    @Test
    void deveAlterarLocalizacao() {
        Entregador usuario = new Entregador("email@test.com", "123");

        Localizacao loc = new Localizacao(1.1, 2.2);
        usuario.setLocalizacao(loc);

        assertEquals(1.1, usuario.getLocalizacao().latitude());
        assertEquals(2.2, usuario.getLocalizacao().longitude());
    }

    // 🔥 NOVO — atualizarLocalizacao()
    @Test
    void deveAtualizarLocalizacaoPorMetodoConveniente() {
        Entregador usuario = new Entregador("email@test.com", "123");

        usuario.atualizarLocalizacao(9.9, 8.8);

        assertEquals(9.9, usuario.getLocalizacao().latitude());
        assertEquals(8.8, usuario.getLocalizacao().longitude());
    }

    // 🔥 NOVO — sobrescrever localizacao várias vezes
    @Test
    void deveSobrescreverLocalizacao() {
        Entregador usuario = new Entregador("email@test.com", "123");

        usuario.setLocalizacao(new Localizacao(1, 1));
        usuario.setLocalizacao(new Localizacao(2, 2));

        assertEquals(2, usuario.getLocalizacao().latitude());
        assertEquals(2, usuario.getLocalizacao().longitude());
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

    // 🔥 NOVO — força o "fallback" do getTipo()
    @Test
    void deveRetornarTipoPadraoParaUsuarioGenerico() {
        Usuario usuario = new Usuario("x@email.com", "123") {
            @Override
            public String toStringParaPersistencia() {
                return "";
            }
        };

        assertEquals(TipoUsuario.PASSAGEIRO_CLIENTE, usuario.getTipo());
    }
}