package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

public class GerenciarCardapioComandoTest {

    private GerenciarCardapioComando comando;

    private ContextoAplicacao contexto;
    private Sessao sessao;
    private RepositorioRestaurante repositorioRestaurante;

    private Restaurante restaurante;

    @BeforeEach
    void setup() {

        comando = new GerenciarCardapioComando();

        sessao = mock(Sessao.class);
        repositorioRestaurante = mock(RepositorioRestaurante.class);
        restaurante = mock(Restaurante.class);

        when(sessao.getUsuarioAtual()).thenReturn(restaurante);
        when(restaurante.getNomeFantasia()).thenReturn("Pizza Top");
        when(restaurante.getCardapio()).thenReturn(List.of());
        when(restaurante.getTaxaEntrega()).thenReturn(5.0);
        when(restaurante.getTempoEstimadoEntregaMinutos()).thenReturn(30);

        var repositorioUsuario = mock(RepositorioUsuario.class);
        var servicoCadastro = mock(ServicoCadastro.class);
        var servicoAutenticacao = mock(ServicoAutenticacao.class);
        var repositorioCorrida = mock(RepositorioCorrida.class);
        var servicoCorrida = mock(ServicoCorrida.class);
        var repositorioOferta = mock(RepositorioOferta.class);
        var repositorioAvaliacao = mock(RepositorioAvaliacao.class);
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
        var repositorioPedido = mock(RepositorioPedido.class);
        var servicoPedido = mock(ServicoPedido.class);
        var repositorioNotificacao = mock(RepositorioNotificacao.class);
        var servicoNotificacao = mock(ServicoNotificacao.class);
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
    void opcaoVoltar() {

        comando.executar(contexto, new Scanner("0\n"));
    }

    @Test
    void opcaoInvalida() {

        comando.executar(contexto, new Scanner("9\n"));
    }

    @Test
    void adicionarItemSucesso() {

        comando.executar(contexto,
                new Scanner("1\nPizza\nPizza deliciosa\n30\n"));

        verify(restaurante).adicionarItemCardapio(any(ItemCardapio.class));
        verify(repositorioRestaurante).salvar(restaurante);
    }

    @Test
    void adicionarItemPrecoZero() {

        comando.executar(contexto,
                new Scanner("1\nPizza\nPizza deliciosa\n0\n"));
    }

    @Test
    void adicionarItemPrecoInvalido() {

        comando.executar(contexto,
                new Scanner("1\nPizza\nPizza deliciosa\nabc\n"));
    }

    @Test
    void exibirCardapioVazio() {

        when(restaurante.getCardapio()).thenReturn(List.of());

        comando.executar(contexto, new Scanner("2\n"));
    }

    @Test
    void exibirCardapioComItens() {

        ItemCardapio item = mock(ItemCardapio.class);

        when(item.toString()).thenReturn("Pizza Calabresa - R$30");

        when(restaurante.getCardapio()).thenReturn(List.of(item));

        comando.executar(contexto, new Scanner("2\n"));
    }

    @Test
    void alterarEntregaSucesso() {

        comando.executar(contexto,
                new Scanner("3\n5\n40\n"));

        verify(restaurante).setTaxaEntrega(5);
        verify(restaurante).setTempoEstimadoEntregaMinutos(40);
        verify(repositorioRestaurante).salvar(restaurante);
    }

    @Test
    void alterarEntregaTaxaNegativa() {

        comando.executar(contexto,
                new Scanner("3\n-2\n"));
    }

    @Test
    void alterarEntregaTempoInvalido() {

        comando.executar(contexto,
                new Scanner("3\n5\n0\n"));
    }

    @Test
    void alterarEntregaNumeroInvalido() {

        comando.executar(contexto,
                new Scanner("3\nabc\n"));
    }
}