package com.uberpb.app;

import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.ServicoAutenticacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.mockito.Mockito.*;

public class LoginComandoTest {

    private LoginComando comando;

    private ContextoAplicacao contexto;
    private Sessao sessao;
    private RepositorioUsuario repositorioUsuario;
    private ServicoAutenticacao servicoAutenticacao;

    private Usuario usuarioMock;

    @BeforeEach
    void setup() {

        comando = new LoginComando();

        sessao = mock(Sessao.class);
        repositorioUsuario = mock(RepositorioUsuario.class);
        servicoAutenticacao = mock(ServicoAutenticacao.class);
        usuarioMock = mock(Usuario.class);

        when(usuarioMock.getEmail()).thenReturn("teste@uber.com");

        contexto = new ContextoAplicacao(
                sessao,
                repositorioUsuario,
                null, // repositorioRestaurante
                null, // servicoCadastro
                servicoAutenticacao,
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
                null, // servicoAdmin
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
    void loginUsuarioNaoEncontrado() {
        when(repositorioUsuario.buscarPorEmail("nao@existe.com")).thenReturn(null);

        comando.executar(contexto, new Scanner("nao@existe.com\nsenha123\n"));

        // Verifica que não logou
        verify(sessao, never()).logar(any());
    }

    @Test
    void loginUsuarioValido() {
        when(repositorioUsuario.buscarPorEmail("teste@uber.com")).thenReturn(usuarioMock);
        when(servicoAutenticacao.autenticar("teste@uber.com", "senha123")).thenReturn(usuarioMock);

        comando.executar(contexto, new Scanner("teste@uber.com\nsenha123\n"));

        // Verifica que logou
        verify(sessao).logar(usuarioMock);
    }

    @Test
    void visivelParaNullRetornaTrue() {
        assert comando.visivelPara(null);
    }

    @Test
    void visivelParaUsuarioLogadoRetornaFalse() {
        Usuario u = mock(Usuario.class);
        assert !comando.visivelPara(u);
    }

    @Test
    void nomeDeveSerLogin() {
        assert "Login".equals(comando.nome());
    }
}