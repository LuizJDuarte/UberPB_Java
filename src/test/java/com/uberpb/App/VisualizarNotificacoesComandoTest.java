package com.uberpb.app;

import com.uberpb.model.Notificacao;
import com.uberpb.model.Usuario;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class VisualizarNotificacoesComandoTest {

    private VisualizarNotificacoesComando comando;
    private ContextoAplicacao contexto;
    private Sessao sessao;
    private ServicoNotificacao servicoNotificacao;

    @BeforeEach
    void setup() {

        comando = new VisualizarNotificacoesComando();

        sessao = mock(Sessao.class);
        servicoNotificacao = mock(ServicoNotificacao.class);

        // mocks necessários para o construtor do ContextoAplicacao
        var repositorioUsuario = mock(com.uberpb.repository.RepositorioUsuario.class);
        var repositorioRestaurante = mock(com.uberpb.repository.RepositorioRestaurante.class);
        var servicoCadastro = mock(ServicoCadastro.class);
        var servicoAutenticacao = mock(ServicoAutenticacao.class);
        var repositorioCorrida = mock(com.uberpb.repository.RepositorioCorrida.class);
        var servicoCorrida = mock(ServicoCorrida.class);
        var repositorioOferta = mock(com.uberpb.repository.RepositorioOferta.class);
        var repositorioAvaliacao = mock(com.uberpb.repository.RepositorioAvaliacao.class);
        var servicoOferta = mock(ServicoOferta.class);
        var servicoValidacaoMotorista = mock(ServicoValidacaoMotorista.class);
        var servicoPagamento = mock(ServicoPagamento.class);
        var servicoAvaliacao = mock(ServicoAvaliacao.class);
        var servicoOtimizacaoRota = mock(ServicoOtimizacaoRota.class);
        var servicoLocalizacao = mock(ServicoLocalizacao.class);
        var servicoDirecionamento = mock(ServicoDirecionamentoCorrida.class);
        var estimativaChegada = mock(EstimativaChegada.class);
        var servicoAdmin = mock(ServicoAdmin.class);
        var gerenciadorCorridas = mock(GerenciadorCorridasAtivas.class);
        var servicoCarrinho = mock(ServicoCarrinho.class);
        var repositorioPedido = mock(com.uberpb.repository.RepositorioPedido.class);
        var servicoPedido = mock(ServicoPedido.class);
        var repositorioNotificacao = mock(com.uberpb.repository.RepositorioNotificacao.class);
        var servicoEntrega = mock(ServicoEntrega.class);

        contexto = new ContextoAplicacao(
                sessao,
                repositorioUsuario,
                repositorioRestaurante,
                servicoCadastro,
                servicoAutenticacao,
                repositorioCorrida,
                servicoCorrida,
                repositorioOferta,
                repositorioAvaliacao,
                servicoOferta,
                servicoValidacaoMotorista,
                servicoPagamento,
                servicoAvaliacao,
                servicoOtimizacaoRota,
                servicoLocalizacao,
                servicoDirecionamento,
                estimativaChegada,
                servicoAdmin,
                gerenciadorCorridas,
                servicoCarrinho,
                repositorioPedido,
                servicoPedido,
                repositorioNotificacao,
                servicoNotificacao,
                servicoEntrega
        );
    }

    @Test
    void visivelParaUsuarioLogado() {

        Usuario usuario = mock(Usuario.class);

        boolean resultado = comando.visivelPara(usuario);

        assertTrue(resultado);
    }

    @Test
    void visivelParaUsuarioNull() {

        boolean resultado = comando.visivelPara(null);

        assertFalse(resultado);
    }

    @Test
    void executarSemNotificacoes() {

        Usuario usuario = mock(Usuario.class);

        when(sessao.getUsuarioAtual()).thenReturn(usuario);
        when(usuario.getEmail()).thenReturn("user@email.com");

        when(servicoNotificacao.buscarNotificacoes("user@email.com"))
                .thenReturn(List.of());

        when(servicoNotificacao.buscarNotificacoesNaoLidas("user@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));

        verify(servicoNotificacao).buscarNotificacoes("user@email.com");
    }

    @Test
    void executarMarcandoComoLidas() {

        Usuario usuario = mock(Usuario.class);
        Notificacao notificacao = mock(Notificacao.class);

        when(sessao.getUsuarioAtual()).thenReturn(usuario);
        when(usuario.getEmail()).thenReturn("user@email.com");

        when(notificacao.getId()).thenReturn("notif1");

        when(servicoNotificacao.buscarNotificacoes("user@email.com"))
                .thenReturn(List.of(notificacao));

        when(servicoNotificacao.buscarNotificacoesNaoLidas("user@email.com"))
                .thenReturn(List.of(notificacao));

        comando.executar(contexto, new Scanner("s\n"));

        verify(servicoNotificacao).marcarComoLida("notif1");
    }
}
