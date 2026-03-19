package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.ServicoAdmin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminComandoTest {

    private AdminComando comando;
    private ContextoAplicacao contexto;

    private RepositorioUsuario repoUsuarioMock;
    private ServicoAdmin servicoAdminMock;
    private Sessao sessaoMock;

    @BeforeEach
    void setup() {
        comando = new AdminComando();

        repoUsuarioMock = mock(RepositorioUsuario.class);
        servicoAdminMock = mock(ServicoAdmin.class);
        sessaoMock = mock(Sessao.class);

        contexto = new ContextoAplicacao(
                sessaoMock,
                repoUsuarioMock,
                null, // repositorioRestaurante
                null, // servicoCadastro
                null, // servicoAutenticacao
                null, // repositorioCorrida
                null, // servicoCorrida
                null, // repositorioOferta
                null, // repositorioAvaliacao
                null, // servicoOferta
                null, // servicoValidacaoMotorista
                null, // servicoPagamento
                null, // servicoAvaliacao
                null, // servicoOtimizacaoRota
                null, // servicoLocalizacao
                null, // servicoDirecionamento
                null, // estimativaChegada
                servicoAdminMock, // ✅ importante
                null, // gerenciadorCorridas
                null, // servicoCarrinho
                null, // repositorioPedido
                null, // servicoPedido
                null, // repositorioNotificacao
                null, // servicoNotificacao
                null  // servicoEntrega
        );
    }

    @Test
    void deveRetornarNomeCorreto() {
        assertEquals("Admin: Gerenciar (listar/remover)", comando.nome());
    }

    @Test
    void deveSerVisivelParaAdministrador() {
        Usuario admin = mock(Administrador.class);
        assertTrue(comando.visivelPara(admin));
    }

    @Test
    void naoDeveSerVisivelParaUsuarioComum() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void deveListarUsuarios() {
        String input = "1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Usuario u1 = mock(Usuario.class);
        when(u1.getEmail()).thenReturn("a@email.com");
        when(u1.getSenhaHash()).thenReturn("hash1");

        when(repoUsuarioMock.buscarTodos()).thenReturn(List.of(u1));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        comando.executar(contexto, scanner);

        String output = out.toString();

        assertTrue(output.contains("a@email.com"));
        assertTrue(output.contains("hash1"));
    }

    @Test
    void deveListarUsuariosVazio() {
        String input = "1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(repoUsuarioMock.buscarTodos()).thenReturn(List.of());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        comando.executar(contexto, scanner);

        String output = out.toString();

        assertTrue(output.contains("(vazio)"));
    }

    @Test
    void deveRemoverUsuario() {
        String input = String.join("\n", "2", "teste@email.com");
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Usuario admin = mock(Administrador.class);
        when(sessaoMock.getUsuarioAtual()).thenReturn(admin);

        comando.executar(contexto, scanner);

        verify(servicoAdminMock, times(1))
                .removerUsuario("teste@email.com", admin);
    }

    @Test
    void deveRemoverCorrida() {
        String input = String.join("\n", "3", "123");
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Usuario admin = mock(Administrador.class);
        when(sessaoMock.getUsuarioAtual()).thenReturn(admin);

        comando.executar(contexto, scanner);

        verify(servicoAdminMock, times(1))
                .removerCorrida("123", admin);
    }

    @Test
    void deveMostrarErroParaOpcaoInvalida() {
        String input = "99\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        comando.executar(contexto, scanner);

        String output = out.toString();

        assertTrue(output.toLowerCase().contains("inválida"));
    }
}