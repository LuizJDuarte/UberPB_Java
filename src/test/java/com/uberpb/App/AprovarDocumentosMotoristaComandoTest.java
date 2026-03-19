package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class AprovarDocumentosMotoristaComandoTest {

    private AprovarDocumentosMotoristaComando comando;
    private ContextoAplicacao contexto;
    private RepositorioUsuario repositorio;
    private Administrador admin;

    @BeforeEach
    void setup() throws Exception {
        comando = new AprovarDocumentosMotoristaComando();
        contexto = new ContextoAplicacao();

        repositorio = mock(RepositorioUsuario.class);
        admin = new Administrador("admin@email.com", "senha");

        // Injeta o repositório de usuários via reflection
        setField(contexto, "repositorioUsuario", repositorio);
    }

    // Helper para reflection
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = ContextoAplicacao.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // =========================
    // VISIBILIDADE
    // =========================
    @Test
    void visivelApenasParaAdministrador() {
        Usuario usuarioComum = mock(Usuario.class);

        // Deve ser visível apenas para administrador
        assert comando.visivelPara(admin);
        assert !comando.visivelPara(usuarioComum);
        assert !comando.visivelPara(null);
    }

    // =========================
    // EXECUÇÃO
    // =========================

    @Test
    void motoristaNaoEncontrado() {
        String input = "inexistente@email.com\n";
        Scanner scanner = new Scanner(input);

        when(repositorio.buscarPorEmail("inexistente@email.com")).thenReturn(null);

        assertDoesNotThrow(() -> comando.executar(contexto, scanner));
        // Não deve chamar atualizar
        verify(repositorio, never()).atualizar(any());
    }

    @Test
    void emailNaoEhMotorista() {
        String input = "usuario@email.com\n";
        Scanner scanner = new Scanner(input);

        Usuario u = mock(Usuario.class);
        when(repositorio.buscarPorEmail("usuario@email.com")).thenReturn(u);

        assertDoesNotThrow(() -> comando.executar(contexto, scanner));
        verify(repositorio, never()).atualizar(any());
    }

    @Test
    void motoristaJaAprovado() {
        String input = "motorista@email.com\n";
        Scanner scanner = new Scanner(input);

        Motorista motorista = mock(Motorista.class);
        when(motorista.isCnhValida()).thenReturn(true);
        when(motorista.isCrlvValido()).thenReturn(true);

        when(repositorio.buscarPorEmail("motorista@email.com")).thenReturn(motorista);

        assertDoesNotThrow(() -> comando.executar(contexto, scanner));

        // Como já estava aprovado, atualizar não deve ser chamado
        verify(repositorio, never()).atualizar(any());
    }

    @Test
    void aprovarMotoristaComSucesso() {
        String input = "motorista@email.com\n";
        Scanner scanner = new Scanner(input);

        Motorista motorista = mock(Motorista.class);
        when(motorista.isCnhValida()).thenReturn(false);
        when(motorista.isCrlvValido()).thenReturn(false);

        when(repositorio.buscarPorEmail("motorista@email.com")).thenReturn(motorista);

        assertDoesNotThrow(() -> comando.executar(contexto, scanner));

        verify(motorista).setCnhValida(true);
        verify(motorista).setCrlvValido(true);
        verify(repositorio).atualizar(motorista);
    }
}