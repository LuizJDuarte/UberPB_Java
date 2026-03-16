package com.uberpb.app;

import com.uberpb.model.Pedido;
import com.uberpb.model.Restaurante;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

public class GerenciarPedidosComandoTest {

    private GerenciarPedidosComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private ServicoPedido servicoPedido;
    private ServicoNotificacao servicoNotificacao;

    private Pedido pedido;

    @BeforeEach
    void setup() {

        comando = new GerenciarPedidosComando();

        sessao = mock(Sessao.class);
        servicoPedido = mock(ServicoPedido.class);
        servicoNotificacao = mock(ServicoNotificacao.class);

        Restaurante restaurante = mock(Restaurante.class);

        when(sessao.getUsuarioAtual()).thenReturn(restaurante);
        when(restaurante.getEmail()).thenReturn("rest@email.com");

        pedido = mock(Pedido.class);
        when(pedido.getEmailCliente()).thenReturn("cliente@email.com");

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

        when(servicoPedido.buscarPorRestaurante("rest@email.com"))
                .thenReturn(List.of(pedido));
    }

    @Test
    void deveConfirmarPedido() {

        when(pedido.getStatus()).thenReturn("CRIADO");

        Scanner scanner = new Scanner(
                "1\n" +
                "1\n"
        );

        comando.executar(contexto, scanner);

        verify(pedido).setStatus("CONFIRMADO");
        verify(servicoPedido).atualizarPedido(pedido);
    }

    @Test
    void deveColocarEmPreparoQuandoConfirmado() {

        when(pedido.getStatus()).thenReturn("CONFIRMADO");
        when(pedido.getEntregadorAlocado()).thenReturn(null);

        Scanner scanner = new Scanner(
                "1\n" +
                "2\n"
        );

        comando.executar(contexto, scanner);

        verify(pedido).setStatus("EM_PREPARO");
        verify(servicoPedido).atualizarPedido(pedido);
        verify(servicoNotificacao).notificarCliente(any(), any());
    }

    @Test
    void naoDeveColocarEmPreparoSeStatusInvalido() {

        when(pedido.getStatus()).thenReturn("CRIADO");

        Scanner scanner = new Scanner(
                "1\n" +
                "2\n"
        );

        comando.executar(contexto, scanner);

        verify(pedido, never()).setStatus("EM_PREPARO");
    }

    @Test
    void deveNotificarEntregadorQuandoExistir() {

        when(pedido.getStatus()).thenReturn("CONFIRMADO");
        String entregador = "entregador@email.com";

        when(pedido.getEntregadorAlocado()).thenReturn(entregador);

        Scanner scanner = new Scanner(
                "1\n" +
                "2\n"
        );

        comando.executar(contexto, scanner);

        verify(servicoNotificacao).enviarNotificacao(
                eq(entregador),
                any(),
                contains("agora está em preparo")
        );
    }

    @Test
    void deveEnviarParaEntrega() {

        when(pedido.getStatus()).thenReturn("EM_PREPARO");

        Scanner scanner = new Scanner(
                "1\n" +
                "3\n"
        );

        comando.executar(contexto, scanner);

        verify(pedido).setStatus("SAIU_PARA_ENTREGA");
        verify(servicoPedido).atualizarPedido(pedido);
    }

    @Test
    void deveRecusarPedido() {

        when(pedido.getStatus()).thenReturn("CRIADO");

        Scanner scanner = new Scanner(
                "1\n" +
                "4\n"
        );

        comando.executar(contexto, scanner);

        verify(pedido).setStatus("RECUSADO");
        verify(servicoPedido).atualizarPedido(pedido);
    }

    @Test
    void deveExecutarQuandoNaoHaPedidos() {

        when(servicoPedido.buscarPorRestaurante("rest@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("0\n"));
    }
}