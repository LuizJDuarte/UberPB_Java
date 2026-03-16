package com.uberpb.service;

import com.uberpb.model.ItemCardapio;
import com.uberpb.model.Restaurante;
import com.uberpb.repository.RepositorioRestaurante;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoRestauranteTest {

    private RepositorioRestaurante repositorio;
    private ServicoRestaurante servico;

    @BeforeEach
    void setUp() {
        repositorio = mock(RepositorioRestaurante.class);
        servico = new ServicoRestaurante(repositorio);
    }

    @Test
    void deveBuscarRestaurantePorEmail() {

        Restaurante restaurante = new Restaurante("teste@email.com", "123");
        restaurante.setNomeFantasia("Restaurante Teste");

        when(repositorio.buscarPorId("teste@email.com")).thenReturn(restaurante);

        Restaurante resultado = servico.buscarPorEmail("teste@email.com");

        assertNotNull(resultado);
        assertEquals("teste@email.com", resultado.getEmail());
        assertEquals("Restaurante Teste", resultado.getNomeFantasia());
    }

    @Test
    void deveListarTodosRestaurantes() {

        Restaurante r1 = new Restaurante("a@email.com", "123");
        r1.setNomeFantasia("Restaurante A");

        Restaurante r2 = new Restaurante("b@email.com", "123");
        r2.setNomeFantasia("Restaurante B");

        List<Restaurante> lista = List.of(r1, r2);

        when(repositorio.listarTodos()).thenReturn(lista);

        List<Restaurante> resultado = servico.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Restaurante A", resultado.get(0).getNomeFantasia());
    }

    @Test
    void deveAdicionarItemAoCardapio() {

        Restaurante restaurante = new Restaurante("teste@email.com", "123");

        when(repositorio.buscarPorId("teste@email.com")).thenReturn(restaurante);

        servico.adicionarItemAoCardapio(
                "teste@email.com",
                "Pizza",
                "Pizza de queijo",
                30.0
        );

        assertEquals(1, restaurante.getCardapio().size());

        ItemCardapio item = restaurante.getCardapio().get(0);
        assertEquals("Pizza", item.getNome());

        verify(repositorio).salvar(restaurante);
    }

    @Test
    void deveAtualizarInfoEntrega() {

        Restaurante restaurante = new Restaurante("teste@email.com", "123");

        when(repositorio.buscarPorId("teste@email.com")).thenReturn(restaurante);

        servico.atualizarInfoEntrega("teste@email.com", 5.0, 40);

        assertEquals(5.0, restaurante.getTaxaEntrega());
        assertEquals(40, restaurante.getTempoEstimadoEntregaMinutos());

        verify(repositorio).salvar(restaurante);
    }

    @Test
    void naoDeveAdicionarItemSeRestauranteNaoExistir() {

        when(repositorio.buscarPorId("inexistente@email.com")).thenReturn(null);

        servico.adicionarItemAoCardapio(
                "inexistente@email.com",
                "Hamburguer",
                "Hamburguer artesanal",
                25.0
        );

        verify(repositorio, never()).salvar(any());
    }
}
