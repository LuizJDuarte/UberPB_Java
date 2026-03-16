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

public class VisualizarPedidosComandoTest {

    private VisualizarPedidosComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private ServicoPedido servicoPedido;

    @BeforeEach
    void setup() {

        comando = new VisualizarPedidosComando();

        sessao = mock(Sessao.class);
        servicoPedido = mock(ServicoPedido.class);

        // mocks restantes para satisfazer o construtor
        var repositorioUsuario = mock(RepositorioUsuario.class);
        var repositorioRestaurante = mock(RepositorioRestaurante.class);
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
                "Meus Pedidos Anteriores",
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
    void testExecutarSemPedidos() {

        Passageiro passageiro = mock(Passageiro.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(servicoPedido.buscarPorCliente("cliente@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));

        verify(servicoPedido).buscarPorCliente("cliente@email.com");
    }

    @Test
    void testExecutarComPedidos() {

        Passageiro passageiro = mock(Passageiro.class);
        Pedido pedido = mock(Pedido.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(pedido.getEmailRestaurante()).thenReturn("restaurante@email.com");
        when(pedido.getTotal()).thenReturn(50.0);
        when(pedido.getStatus()).thenReturn("ENTREGUE");

        when(servicoPedido.buscarPorCliente("cliente@email.com"))
                .thenReturn(List.of(pedido));

        comando.executar(contexto, new Scanner("\n"));

        verify(servicoPedido).buscarPorCliente("cliente@email.com");
        verify(pedido).getTotal();
        verify(pedido).getStatus();
    }
}