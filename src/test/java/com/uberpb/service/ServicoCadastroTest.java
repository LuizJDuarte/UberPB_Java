package com.uberpb.service;

import com.uberpb.exceptions.EmailJaExistenteException;
import com.uberpb.model.*;
import com.uberpb.repository.RepositorioRestaurante;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoCadastroTest {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioRestaurante repositorioRestaurante;
    private ServicoCadastro servico;

    @BeforeEach
    void setup() {
        repositorioUsuario = mock(RepositorioUsuario.class);
        repositorioRestaurante = mock(RepositorioRestaurante.class);
        servico = new ServicoCadastro(repositorioUsuario);
        servico.setRepositorioRestaurante(repositorioRestaurante);
    }

    // =========================
    // PASSAGEIRO
    // =========================

    @Test
    void deveCadastrarPassageiro() {
        when(repositorioUsuario.buscarPorEmail("teste@email.com")).thenReturn(null);

        Passageiro p = servico.cadastrarPassageiro("teste@email.com", "123");

        assertNotNull(p);
        verify(repositorioUsuario).salvar(p);
    }

    @Test
    void naoDeveCadastrarPassageiroEmailInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                servico.cadastrarPassageiro("email_invalido", "123"));
    }

    @Test
    void naoDeveCadastrarPassageiroEmailDuplicado() {
        when(repositorioUsuario.buscarPorEmail("teste@email.com"))
                .thenReturn(new Passageiro("teste@email.com", "hash"));

        assertThrows(EmailJaExistenteException.class, () ->
                servico.cadastrarPassageiro("teste@email.com", "123"));
    }

    // =========================
    // MOTORISTA
    // =========================

    @Test
    void deveCadastrarMotorista() {
        when(repositorioUsuario.buscarPorEmail("m@email.com")).thenReturn(null);

        Motorista m = servico.cadastrarMotorista("m@email.com", "123");

        assertNotNull(m);
        verify(repositorioUsuario).salvar(m);
    }

    @Test
    void naoDeveCadastrarMotoristaEmailDuplicado() {
        when(repositorioUsuario.buscarPorEmail("m@email.com"))
                .thenReturn(new Motorista("m@email.com", "hash"));

        assertThrows(EmailJaExistenteException.class, () ->
                servico.cadastrarMotorista("m@email.com", "123"));
    }

    // =========================
    // ENTREGADOR
    // =========================

    @Test
    void deveCadastrarEntregadorComDados() {
        when(repositorioUsuario.buscarPorEmail("e@email.com")).thenReturn(null);

        Entregador e = servico.cadastrarEntregador("e@email.com", "123", "123CNH", "123CPF");

        assertEquals("123CNH", e.getCnhNumero());
        assertEquals("123CPF", e.getCpfNumero());
        assertTrue(e.isContaAtiva());

        verify(repositorioUsuario).salvar(e);
    }

    @Test
    void deveCadastrarEntregadorComCamposNull() {
        when(repositorioUsuario.buscarPorEmail("e@email.com")).thenReturn(null);

        Entregador e = servico.cadastrarEntregador("e@email.com", "123", null, null);

        assertEquals("", e.getCnhNumero());
        assertEquals("", e.getCpfNumero());
    }

    @Test
    void naoDeveCadastrarEntregadorEmailDuplicado() {
        when(repositorioUsuario.buscarPorEmail("e@email.com"))
                .thenReturn(new Entregador("e@email.com", "hash"));

        assertThrows(EmailJaExistenteException.class, () ->
                servico.cadastrarEntregador("e@email.com", "123", "cnh", "cpf"));
    }

    // =========================
    // RESTAURANTE
    // =========================

    @Test
    void deveCadastrarRestauranteComRepositorio() {
        when(repositorioUsuario.buscarPorEmail("r@email.com")).thenReturn(null);

        Restaurante r = servico.cadastrarRestaurante(
                "r@email.com", "123", "Meu Restaurante", "123CNPJ"
        );

        assertEquals("Meu Restaurante", r.getNomeFantasia());
        assertEquals("123CNPJ", r.getCnpj());

        verify(repositorioUsuario).salvar(r);
        verify(repositorioRestaurante).salvar(r);
    }

    @Test
    void deveCadastrarRestauranteSemRepositorioRestaurante() {
        ServicoCadastro servicoSemRepo = new ServicoCadastro(repositorioUsuario);

        when(repositorioUsuario.buscarPorEmail("r@email.com")).thenReturn(null);

        Restaurante r = servicoSemRepo.cadastrarRestaurante(
                "r@email.com", "123", "Restaurante", "CNPJ"
        );

        assertNotNull(r);
        verify(repositorioUsuario).salvar(r);
        // NÃO deve lançar erro mesmo sem repositorioRestaurante
    }

    @Test
    void naoDeveCadastrarRestauranteEmailDuplicado() {
        when(repositorioUsuario.buscarPorEmail("r@email.com"))
                .thenReturn(new Restaurante("r@email.com", "hash"));

        assertThrows(EmailJaExistenteException.class, () ->
                servico.cadastrarRestaurante("r@email.com", "123", "Nome", "CNPJ"));
    }

    // =========================
    // BUSCAR
    // =========================

    @Test
    void deveBuscarUsuario() {
        Usuario u = new Passageiro("teste@email.com", "hash");

        when(repositorioUsuario.buscarPorEmail("teste@email.com"))
                .thenReturn(u);

        Usuario resultado = servico.buscar("teste@email.com");

        assertEquals(u, resultado);
    }
}