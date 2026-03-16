package com.uberpb.service;

import com.uberpb.exceptions.CredenciaisInvalidasException;
import com.uberpb.exceptions.UsuarioNaoEncontradoException;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Restaurante;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioRestaurante;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoAutenticacaoTest {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioRestaurante repositorioRestaurante;
    private ServicoAutenticacao servicoAutenticacao;

    @BeforeEach
    void setUp() {
        repositorioUsuario = mock(RepositorioUsuario.class);
        repositorioRestaurante = mock(RepositorioRestaurante.class);

        servicoAutenticacao = new ServicoAutenticacao(
                repositorioUsuario,
                repositorioRestaurante
        );
    }

    @Test
    void deveAutenticarComSucesso() {

        String email = "teste@uberpb.com";
        String senha = "senha123";
        String hash = PasswordUtil.hashPassword(senha);

        Usuario usuario = new Passageiro(email, hash);

        when(repositorioUsuario.buscarPorEmail(email)).thenReturn(usuario);

        Usuario resultado = servicoAutenticacao.autenticar(email, senha);

        assertNotNull(resultado);
        assertEquals(email, resultado.getEmail());
    }

    @Test
    void deveFalharQuandoUsuarioNaoExiste() {

        when(repositorioUsuario.buscarPorEmail("naoexiste@uberpb.com"))
                .thenReturn(null);

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> servicoAutenticacao.autenticar("naoexiste@uberpb.com", "123")
        );
    }

    @Test
    void deveFalharQuandoSenhaIncorreta() {

        String email = "teste@uberpb.com";

        Usuario usuario = new Passageiro(email, PasswordUtil.hashPassword("senhaCorreta"));

        when(repositorioUsuario.buscarPorEmail(email)).thenReturn(usuario);

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> servicoAutenticacao.autenticar(email, "senhaErrada")
        );
    }

    @Test
    void deveFalharQuandoContaDesativada() {

        String email = "teste@uberpb.com";
        String senha = "123";

        Passageiro passageiro = new Passageiro(email, PasswordUtil.hashPassword(senha));
        passageiro.setContaAtiva(false);

        when(repositorioUsuario.buscarPorEmail(email)).thenReturn(passageiro);

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> servicoAutenticacao.autenticar(email, senha)
        );
    }

    @Test
    void deveBuscarRestauranteNoRepositorioRestaurante() {

        String email = "restaurante@uberpb.com";
        String senha = "123";
        String hash = PasswordUtil.hashPassword(senha);

        Restaurante restaurante = mock(Restaurante.class);

        when(restaurante.getSenhaHash()).thenReturn(hash);
        when(restaurante.getEmail()).thenReturn(email);

        when(repositorioUsuario.buscarPorEmail(email)).thenReturn(restaurante);
        when(repositorioRestaurante.buscarPorId(email)).thenReturn(restaurante);

        Usuario resultado = servicoAutenticacao.autenticar(email, senha);

        assertNotNull(resultado);

        verify(repositorioRestaurante).buscarPorId(email);
    }
}
