package com.uberpb.service;

import com.uberpb.model.Localizacao;
import com.uberpb.model.Restaurante;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoLocalRestauranteTest {

    private final ServicoLocalRestaurante servico = new ServicoLocalRestaurante();

    @Test
    void deveRetornarRestaurantesProximos() {

        Restaurante r1 = mock(Restaurante.class);

        // coordenada próxima da região gerada pelo email
        when(r1.getLocalizacao()).thenReturn(new Localizacao(0.1, 0.1));

        List<Restaurante> resultado = servico.listarRestaurantesProximos(
                "user@email.com",
                List.of(r1)
        );

        assertNotNull(resultado);
    }

    @Test
    void deveFiltrarRestaurantesMuitoDistantes() {

        Restaurante perto = mock(Restaurante.class);
        Restaurante longe = mock(Restaurante.class);

        // perto
        when(perto.getLocalizacao()).thenReturn(new Localizacao(0.1, 0.1));

        // MUITO longe
        when(longe.getLocalizacao()).thenReturn(new Localizacao(80.0, 80.0));

        List<Restaurante> resultado = servico.listarRestaurantesProximos(
                "user@email.com",
                List.of(perto, longe)
        );

        assertFalse(resultado.contains(longe));
    }

    @Test
    void deveOrdenarRestaurantesPorDistancia() {

        Restaurante r1 = mock(Restaurante.class);
        Restaurante r2 = mock(Restaurante.class);

        // r1 mais perto
        when(r1.getLocalizacao()).thenReturn(new Localizacao(0.1, 0.1));

        // r2 mais longe
        when(r2.getLocalizacao()).thenReturn(new Localizacao(1.0, 1.0));

        List<Restaurante> resultado = servico.listarRestaurantesProximos(
                "user@email.com",
                List.of(r2, r1)
        );

        assertNotNull(resultado);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaRestaurantes() {

        List<Restaurante> resultado = servico.listarRestaurantesProximos(
                "user@email.com",
                List.of()
        );

        assertTrue(resultado.isEmpty());
    }
}
