package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

public class MotoristaVerOfertasComandoTest {

    private ContextoAplicacao criarContexto() {

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

        ContextoAplicacao ctx = new ContextoAplicacao(
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

        Motorista motorista = mock(Motorista.class);
        when(motorista.getEmail()).thenReturn("motorista@test.com");
        when(sessao.getUsuarioAtual()).thenReturn(motorista);

        return ctx;
    }

    @Test
    void listaVazia() {

        ContextoAplicacao ctx = criarContexto();
        MotoristaVerOfertasComando comando = new MotoristaVerOfertasComando();

        when(ctx.servicoOferta.listarOfertasDoMotorista("motorista@test.com"))
                .thenReturn(List.of());

        Scanner entrada = new Scanner(new ByteArrayInputStream("\n".getBytes()));

        comando.executar(ctx, entrada);
    }

    @Test
    void numeroInvalido() {

        ContextoAplicacao ctx = criarContexto();
        MotoristaVerOfertasComando comando = new MotoristaVerOfertasComando();

        OfertaCorrida oferta = mock(OfertaCorrida.class);
        when(oferta.getCorridaId()).thenReturn("1");
        when(oferta.getId()).thenReturn("of1");

        Corrida corrida = mock(Corrida.class);

        when(ctx.servicoOferta.listarOfertasDoMotorista("motorista@test.com"))
                .thenReturn(List.of(oferta));

        when(ctx.repositorioCorrida.buscarPorId("1")).thenReturn(corrida);

        Scanner entrada = new Scanner(new ByteArrayInputStream("abc\n".getBytes()));

        comando.executar(ctx, entrada);
    }

    @Test
    void aceitarOferta() {

        ContextoAplicacao ctx = criarContexto();
        MotoristaVerOfertasComando comando = new MotoristaVerOfertasComando();

        OfertaCorrida oferta = mock(OfertaCorrida.class);
        when(oferta.getCorridaId()).thenReturn("1");
        when(oferta.getId()).thenReturn("of1");
        when(oferta.getStatus()).thenReturn(OfertaStatus.PENDENTE);

        Corrida corrida = mock(Corrida.class);

        when(ctx.servicoOferta.listarOfertasDoMotorista("motorista@test.com"))
                .thenReturn(List.of(oferta));

        when(ctx.repositorioCorrida.buscarPorId("1")).thenReturn(corrida);

        Scanner entrada = new Scanner(new ByteArrayInputStream("1\n1\n".getBytes()));

        comando.executar(ctx, entrada);

        verify(ctx.servicoOferta).aceitarOferta("of1", "motorista@test.com");
    }

    @Test
    void recusarOferta() {

        ContextoAplicacao ctx = criarContexto();
        MotoristaVerOfertasComando comando = new MotoristaVerOfertasComando();

        OfertaCorrida oferta = mock(OfertaCorrida.class);
        when(oferta.getCorridaId()).thenReturn("1");
        when(oferta.getId()).thenReturn("of1");
        when(oferta.getStatus()).thenReturn(OfertaStatus.PENDENTE);

        Corrida corrida = mock(Corrida.class);

        when(ctx.servicoOferta.listarOfertasDoMotorista("motorista@test.com"))
                .thenReturn(List.of(oferta));

        when(ctx.repositorioCorrida.buscarPorId("1")).thenReturn(corrida);

        Scanner entrada = new Scanner(new ByteArrayInputStream("1\n2\n".getBytes()));

        comando.executar(ctx, entrada);

        verify(ctx.servicoOferta).recusarOferta("of1", "motorista@test.com");
    }
}