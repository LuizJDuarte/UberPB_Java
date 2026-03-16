package com.uberpb.app;

import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContextoAplicacaoTest {

    @Test
    void construtorVazioDeveInicializarTudoComoNull() {

        ContextoAplicacao contexto = new ContextoAplicacao();

        assertNull(contexto.getSessao());
        assertNull(contexto.getRepositorioUsuario());
        assertNull(contexto.getRepositorioCorrida());
        assertNull(contexto.getRepositorioAvaliacao());
        assertNull(contexto.getServicoAvaliacao());
        assertNull(contexto.getRepositorioPedido());
        assertNull(contexto.getServicoPedido());
        assertNull(contexto.getRepositorioNotificacao());
        assertNull(contexto.getServicoNotificacao());
        assertNull(contexto.getServicoEntrega());
    }

    @Test
    void construtorCompletoDeveInicializarTodosCampos() {

        Sessao sessao = mock(Sessao.class);

        RepositorioUsuario repositorioUsuario = mock(RepositorioUsuario.class);
        RepositorioRestaurante repositorioRestaurante = mock(RepositorioRestaurante.class);

        ServicoCadastro servicoCadastro = mock(ServicoCadastro.class);
        ServicoAutenticacao servicoAutenticacao = mock(ServicoAutenticacao.class);

        RepositorioCorrida repositorioCorrida = mock(RepositorioCorrida.class);
        ServicoCorrida servicoCorrida = mock(ServicoCorrida.class);

        RepositorioOferta repositorioOferta = mock(RepositorioOferta.class);
        RepositorioAvaliacao repositorioAvaliacao = mock(RepositorioAvaliacao.class);
        ServicoOferta servicoOferta = mock(ServicoOferta.class);

        ServicoValidacaoMotorista servicoValidacaoMotorista = mock(ServicoValidacaoMotorista.class);
        ServicoPagamento servicoPagamento = mock(ServicoPagamento.class);
        ServicoAvaliacao servicoAvaliacao = mock(ServicoAvaliacao.class);

        ServicoOtimizacaoRota servicoOtimizacaoRota = mock(ServicoOtimizacaoRota.class);
        ServicoLocalizacao servicoLocalizacao = mock(ServicoLocalizacao.class);
        ServicoDirecionamentoCorrida servicoDirecionamento = mock(ServicoDirecionamentoCorrida.class);
        EstimativaChegada servicoEstimativaChegada = mock(EstimativaChegada.class);

        ServicoAdmin servicoAdmin = mock(ServicoAdmin.class);
        GerenciadorCorridasAtivas gerenciadorCorridas = mock(GerenciadorCorridasAtivas.class);

        ServicoCarrinho servicoCarrinho = mock(ServicoCarrinho.class);

        RepositorioPedido repositorioPedido = mock(RepositorioPedido.class);
        ServicoPedido servicoPedido = mock(ServicoPedido.class);

        RepositorioNotificacao repositorioNotificacao = mock(RepositorioNotificacao.class);
        ServicoNotificacao servicoNotificacao = mock(ServicoNotificacao.class);
        ServicoEntrega servicoEntrega = mock(ServicoEntrega.class);

        ContextoAplicacao contexto = new ContextoAplicacao(
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
                servicoEstimativaChegada,
                servicoAdmin,
                gerenciadorCorridas,
                servicoCarrinho,
                repositorioPedido,
                servicoPedido,
                repositorioNotificacao,
                servicoNotificacao,
                servicoEntrega
        );

        assertEquals(sessao, contexto.getSessao());
        assertEquals(repositorioUsuario, contexto.getRepositorioUsuario());
        assertEquals(repositorioCorrida, contexto.getRepositorioCorrida());
        assertEquals(repositorioAvaliacao, contexto.getRepositorioAvaliacao());
        assertEquals(servicoAvaliacao, contexto.getServicoAvaliacao());
        assertEquals(repositorioPedido, contexto.getRepositorioPedido());
        assertEquals(servicoPedido, contexto.getServicoPedido());
        assertEquals(repositorioNotificacao, contexto.getRepositorioNotificacao());
        assertEquals(servicoNotificacao, contexto.getServicoNotificacao());
        assertEquals(servicoEntrega, contexto.getServicoEntrega());
    }
}
