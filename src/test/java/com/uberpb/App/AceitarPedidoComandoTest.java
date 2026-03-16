package com.uberpb.app;

import com.uberpb.model.Entregador;
import com.uberpb.model.Pedido;
import com.uberpb.model.Usuario;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

public class AceitarPedidoComandoTest {

    private ContextoAplicacao contexto;
    private Sessao sessao;

    private ServicoPedido servicoPedido;
    private ServicoEntrega servicoEntrega;

    private AceitarPedidoComando comando;

    @BeforeEach
    void setup() {

        sessao = mock(Sessao.class);

        servicoPedido = mock(ServicoPedido.class);
        servicoEntrega = mock(ServicoEntrega.class);

        // mocks obrigatórios para o construtor
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
        EstimativaChegada estimativaChegada = mock(EstimativaChegada.class);
        ServicoAdmin servicoAdmin = mock(ServicoAdmin.class);
        GerenciadorCorridasAtivas gerenciadorCorridas = mock(GerenciadorCorridasAtivas.class);
        ServicoCarrinho servicoCarrinho = mock(ServicoCarrinho.class);

        RepositorioPedido repositorioPedido = mock(RepositorioPedido.class);

        RepositorioNotificacao repositorioNotificacao = mock(RepositorioNotificacao.class);
        ServicoNotificacao servicoNotificacao = mock(ServicoNotificacao.class);

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

        comando = new AceitarPedidoComando();
    }

    @Test
    void usuarioNaoEntregador() {

        Usuario usuario = mock(Usuario.class);

        when(sessao.getUsuarioAtual()).thenReturn(usuario);

        comando.executar(contexto, new Scanner(""));

        verify(sessao).getUsuarioAtual();
    }

    @Test
    void semPedidosDisponiveis() {

        Entregador entregador = mock(Entregador.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("entregador@email.com");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador(any()))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner(""));

        verify(servicoPedido).buscarPedidosDisponiveisParaEntregador(any());
    }

    @Test
    void aceitarPedidoComSucesso() {

        Entregador entregador = mock(Entregador.class);
        Pedido pedido = mock(Pedido.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);

        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("entregador@email.com");

        when(pedido.getEmailRestaurante()).thenReturn("restaurante@email.com");
        when(pedido.getEmailCliente()).thenReturn("cliente@email.com");
        when(pedido.getTotal()).thenReturn(50.0);
        when(pedido.getStatus()).thenReturn("EM_PREPARO");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador(any()))
                .thenReturn(List.of(pedido));

        when(servicoEntrega.aceitarPedido(any(), any())).thenReturn(true);

        Scanner scanner = new Scanner("1\ns\n");

        comando.executar(contexto, scanner);

        verify(servicoEntrega).aceitarPedido(any(), any());
    }
}
