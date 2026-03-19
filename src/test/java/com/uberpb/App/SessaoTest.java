package com.uberpb.app;

import com.uberpb.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessaoTest {

    private Sessao sessao;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        sessao = new Sessao();
        usuarioMock = mock(Usuario.class);
    }

    @Test
    void novaSessaoNaoEstaLogada() {
        assertFalse(sessao.estaLogado(), "Sessão nova deve estar deslogada");
        assertNull(sessao.getUsuarioAtual(), "Usuário atual deve ser null");
    }

    @Test
    void logarUsuarioAtualizaSessao() {
        sessao.logar(usuarioMock);

        assertTrue(sessao.estaLogado(), "Sessão deve estar logada após logar usuário");
        assertEquals(usuarioMock, sessao.getUsuarioAtual(), "Usuário atual deve ser o que foi logado");
    }

    @Test
    void deslogarLimpaSessao() {
        sessao.logar(usuarioMock);
        sessao.deslogar();

        assertFalse(sessao.estaLogado(), "Sessão não deve estar logada após deslogar");
        assertNull(sessao.getUsuarioAtual(), "Usuário atual deve ser null após deslogar");
    }

    @Test
    void logarSubstituiUsuarioAnterior() {
        Usuario outroUsuario = mock(Usuario.class);

        sessao.logar(usuarioMock);
        sessao.logar(outroUsuario);

        assertTrue(sessao.estaLogado(), "Sessão deve estar logada");
        assertEquals(outroUsuario, sessao.getUsuarioAtual(), "Usuário atual deve ser o último logado");
    }
}