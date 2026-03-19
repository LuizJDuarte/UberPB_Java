package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Entregador;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AprovarDocumentosEntregadorComandoTest {

    private AprovarDocumentosEntregadorComando comando;
    private ContextoAplicacao contexto;
    private RepositorioUsuario repositorioUsuario;

    @BeforeEach
    void setup() throws Exception {
        comando = new AprovarDocumentosEntregadorComando();

        contexto = new ContextoAplicacao();
        repositorioUsuario = mock(RepositorioUsuario.class);

        // Injeta o repositório usando reflection
        setField(contexto, "repositorioUsuario", repositorioUsuario);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = ContextoAplicacao.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ===============================
    // VISIBILIDADE
    // ===============================
    @Test
    void deveSerVisivelApenasParaAdministrador() {
        Usuario admin = mock(Administrador.class);
        Usuario usuarioNormal = mock(Usuario.class);

        assertTrue(comando.visivelPara(admin));
        assertFalse(comando.visivelPara(usuarioNormal));
        assertFalse(comando.visivelPara(null));
    }

    // ===============================
    // EXECUÇÃO
    // ===============================

    @Test
    void deveTratarUsuarioNaoEncontrado() {
        String input = "email@naoexiste.com\n";
        Scanner scanner = new Scanner(input);

        when(repositorioUsuario.buscarPorEmail("email@naoexiste.com")).thenReturn(null);

        assertDoesNotThrow(() -> comando.executar(contexto, scanner));

        verify(repositorioUsuario).buscarPorEmail("email@naoexiste.com");
    }

    @Test
    void deveTratarEmailNaoEntregador() {
        String input = "usuario@email.com\n";
        Scanner scanner = new Scanner(input);

        Usuario usuario = mock(Usuario.class);
        when(repositorioUsuario.buscarPorEmail("usuario@email.com")).thenReturn(usuario);

        assertDoesNotThrow(() -> comando.executar(contexto, scanner));
        verify(repositorioUsuario).buscarPorEmail("usuario@email.com");
    }

    @Test
    void deveInformarDocumentosJaAprovados() {
        String input = "entregador@email.com\n";
        Scanner scanner = new Scanner(input);

        Entregador entregador = mock(Entregador.class);
        when(entregador.isCnhValida()).thenReturn(true);
        when(entregador.isDocIdentidadeValido()).thenReturn(true);
        when(repositorioUsuario.buscarPorEmail("entregador@email.com")).thenReturn(entregador);

        assertDoesNotThrow(() -> comando.executar(contexto, scanner));

        verify(entregador).isCnhValida();
        verify(entregador).isDocIdentidadeValido();
        verifyNoMoreInteractions(entregador);
    }

    @Test
    void deveAprovarDocumentosComSucesso() {
        String input = "entregador@email.com\n";
        Scanner scanner = new Scanner(input);

        Entregador entregador = mock(Entregador.class);
        when(entregador.isCnhValida()).thenReturn(false);
        when(entregador.isDocIdentidadeValido()).thenReturn(false);
        when(repositorioUsuario.buscarPorEmail("entregador@email.com")).thenReturn(entregador);

        assertDoesNotThrow(() -> comando.executar(contexto, scanner));

        verify(entregador).setCnhValida(true);
        verify(entregador).setDocIdentidadeValido(true);
        verify(repositorioUsuario).atualizar(entregador);
    }
}