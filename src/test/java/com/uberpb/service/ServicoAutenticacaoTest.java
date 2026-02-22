package com.uberpb.service;

import com.uberpb.model.Usuario;
import com.uberpb.model.Passageiro;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.repository.RepositorioRestaurante;
import com.uberpb.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServicoAutenticacaoTest {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioRestaurante repositorioRestaurante; // ADICIONADO
    private ServicoAutenticacao servicoAutenticacao;

    @BeforeEach
    public void setUp() {
        repositorioUsuario = mock(RepositorioUsuario.class);
        repositorioRestaurante = mock(RepositorioRestaurante.class); // MOCK ADICIONADO

        servicoAutenticacao = new ServicoAutenticacao(
                repositorioUsuario,
                repositorioRestaurante
        );
    }

    @Test
    public void testAutenticarComSucesso() {
        String email = "teste@uberpb.com";
        String senha = "senha123";
        String senhaHash = PasswordUtil.hashPassword(senha);
        Usuario usuario = new Passageiro(email, senhaHash);

        when(repositorioUsuario.buscarPorEmail(email)).thenReturn(usuario);

        Usuario usuarioAutenticado = servicoAutenticacao.autenticar(email, senha);

        assertNotNull(usuarioAutenticado);
        assertEquals(email, usuarioAutenticado.getEmail());
    }

    @Test
    public void testAutenticarFalhaUsuarioNaoEncontrado() {
        when(repositorioUsuario.buscarPorEmail("naoexiste@uberpb.com")).thenReturn(null);

        Exception exception = assertThrows(
                com.uberpb.exceptions.UsuarioNaoEncontradoException.class,
                () -> servicoAutenticacao.autenticar("naoexiste@uberpb.com", "senha123")
        );

        assertEquals("E-mail não cadastrado.", exception.getMessage());
    }

    @Test
    public void testAutenticarFalhaSenhaIncorreta() {
        String email = "teste@uberpb.com";
        String senhaCorreta = "senha123";
        String senhaIncorreta = "senha-errada";
        String senhaHash = PasswordUtil.hashPassword(senhaCorreta);
        Usuario usuario = new Passageiro(email, senhaHash);

        when(repositorioUsuario.buscarPorEmail(email)).thenReturn(usuario);

        Exception exception = assertThrows(
                com.uberpb.exceptions.CredenciaisInvalidasException.class,
                () -> servicoAutenticacao.autenticar(email, senhaIncorreta)
        );

        assertEquals("Senha incorreta.", exception.getMessage());
    }
}