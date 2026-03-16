package com.uberpb.service;

import com.uberpb.model.Localizacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoRotaMockTest {

    private final ServicoRotaMock servico = new ServicoRotaMock();

    @Test
    void deveGeocodificarRuaExistente() {

        Localizacao loc = servico.geocodificar("Rua das Acácias");

        assertNotNull(loc);
        assertEquals(-7.115, loc.latitude());
        assertEquals(-34.86, loc.longitude());
    }

    @Test
    void geocodificarRuaInexistenteDeveRetornarNull() {

        Localizacao loc = servico.geocodificar("Rua Inexistente");

        assertNull(loc);
    }

    @Test
    void deveConfirmarRotaExistente() {

        boolean existe = servico.rotaExiste(
                "Rua das Acácias",
                "Avenida Epitácio Pessoa"
        );

        assertTrue(existe);
    }

    @Test
    void rotaNaoExisteQuandoOrigemInvalida() {

        boolean existe = servico.rotaExiste(
                "Rua Inexistente",
                "Avenida Epitácio Pessoa"
        );

        assertFalse(existe);
    }

    @Test
    void rotaNaoExisteQuandoDestinoInvalido() {

        boolean existe = servico.rotaExiste(
                "Rua das Acácias",
                "Rua Fantasma"
        );

        assertFalse(existe);
    }
}
