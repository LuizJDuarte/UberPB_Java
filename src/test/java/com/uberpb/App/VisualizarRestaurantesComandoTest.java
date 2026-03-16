package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.service.*;
import com.uberpb.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class VisualizarRestaurantesComandoTest {

    private VisualizarRestaurantesComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private ServicoLocalizacao servicoLocalizacao;
    private RepositorioRestaurante repositorioRestaurante;
    private ServicoCarrinho servicoCarrinho;

    @BeforeEach
    void setup() {

        comando = new VisualizarRestaurantesComando();

        sessao = mock(Sessao.class);
        servicoLocalizacao = mock(ServicoLocalizacao.class);
        repositorioRestaurante = mock(RepositorioRestaurante.class);
        servicoCarrinho = mock(ServicoCarrinho.class);

        // mocks restantes para satisfazer construtor
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
        var servicoDirecionamento = mock(ServicoDirecionamentoCorrida.class);
        var estimativaChegada = mock(EstimativaChegada.class);
        var servicoAdmin = mock(ServicoAdmin.class);
        var gerenciadorCorridas = mock(GerenciadorCorridasAtivas.class);
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
    void testNome() {

        assertEquals(
                "Acessar restaurantes disponíveis.",
                comando.nome()
        );
    }

    @Test
    void testVisivelParaPassageiro() {

        Passageiro passageiro = mock(Passageiro.class);

        assertTrue(comando.visivelPara(passageiro));
    }

    @Test
    void testVisivelParaOutroUsuario() {

        Usuario usuario = mock(Usuario.class);

        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void testExecutarSemRestaurantes() {

        Passageiro passageiro = mock(Passageiro.class);
        Localizacao loc = mock(Localizacao.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("user@email.com");

        when(servicoLocalizacao.obterLocalizacaoAtual("user@email.com"))
                .thenReturn(loc);

        when(repositorioRestaurante.listarTodos())
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));

        verify(repositorioRestaurante).listarTodos();
    }

    @Test
    void testExecutarRestauranteListadosOpcaoInvalida() {

        Passageiro passageiro = mock(Passageiro.class);
        Restaurante restaurante = mock(Restaurante.class);
        Localizacao loc = mock(Localizacao.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("user@email.com");

        when(servicoLocalizacao.obterLocalizacaoAtual("user@email.com"))
                .thenReturn(loc);

        when(restaurante.isContaAtiva()).thenReturn(true);
        when(restaurante.getLocalizacao()).thenReturn(loc);
        when(restaurante.getNomeFantasia()).thenReturn("Restaurante Teste");
        when(restaurante.getTaxaEntrega()).thenReturn(5.0);
        when(restaurante.getTempoEstimadoEntregaMinutos()).thenReturn(30);

        when(servicoLocalizacao.distanciaKm(any(), any()))
                .thenReturn(2.0);

        when(repositorioRestaurante.listarTodos())
                .thenReturn(List.of(restaurante));

        comando.executar(contexto, new Scanner("5\n"));

        verify(repositorioRestaurante).listarTodos();
    }

    @Test
    void testEntradaNaoNumerica() {

        Passageiro passageiro = mock(Passageiro.class);
        Localizacao loc = mock(Localizacao.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("user@email.com");

        when(servicoLocalizacao.obterLocalizacaoAtual("user@email.com"))
                .thenReturn(loc);

        when(repositorioRestaurante.listarTodos())
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("abc\n"));

        verify(repositorioRestaurante).listarTodos();
    }

    @Test
    void testAdicionarItemCarrinho() {

        Passageiro passageiro = mock(Passageiro.class);
        Restaurante restaurante = mock(Restaurante.class);
        Localizacao loc = mock(Localizacao.class);
        ItemCardapio item = mock(ItemCardapio.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("user@email.com");

        when(servicoLocalizacao.obterLocalizacaoAtual("user@email.com"))
                .thenReturn(loc);

        when(restaurante.isContaAtiva()).thenReturn(true);
        when(restaurante.getLocalizacao()).thenReturn(loc);
        when(restaurante.getNomeFantasia()).thenReturn("Restaurante Teste");
        when(restaurante.getTaxaEntrega()).thenReturn(5.0);
        when(restaurante.getTempoEstimadoEntregaMinutos()).thenReturn(30);

        when(servicoLocalizacao.distanciaKm(any(), any()))
                .thenReturn(1.0);

        when(item.toString()).thenReturn("Pizza - R$ 20");

        when(restaurante.getCardapio())
                .thenReturn(List.of(item));

        when(repositorioRestaurante.listarTodos())
                .thenReturn(List.of(restaurante));

        Scanner entrada = new Scanner(
                "1\n" + // escolher restaurante
                "1\n" + // escolher item
                "2\n" + // quantidade
                "2\n" + // continuar no cardápio
                "0\n"   // voltar
        );

        comando.executar(contexto, entrada);

        verify(servicoCarrinho).adicionarAoCarrinho(
                eq("user@email.com"),
                eq(restaurante),
                eq(item),
                eq(2)
        );
    }
}