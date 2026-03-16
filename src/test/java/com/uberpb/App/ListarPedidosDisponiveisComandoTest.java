
package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ListarPedidosDisponiveisComandoTest {

    private ContextoAplicacao contexto;
    private Sessao sessao;
    private ServicoPedido servicoPedido;

    private ListarPedidosDisponiveisComando comando;

    @BeforeEach
    void setup() {

        sessao = mock(Sessao.class);
        servicoPedido = mock(ServicoPedido.class);

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

        comando = new ListarPedidosDisponiveisComando();
    }

    @Test
    void testarNome() {
        assertEquals("[Entregador] Pedidos Disponíveis", comando.nome());
    }

    @Test
    void visivelParaEntregador() {
        Entregador entregador = mock(Entregador.class);
        assertTrue(comando.visivelPara(entregador));
    }

    @Test
    void naoVisivelParaOutroUsuario() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void usuarioNaoEhEntregador() {

        Usuario usuario = mock(Usuario.class);

        when(sessao.getUsuarioAtual()).thenReturn(usuario);

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void entregadorNaoAtivo() {

        Entregador entregador = mock(Entregador.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(false);

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void entregadorOffline() {

        Entregador entregador = mock(Entregador.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(false);

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void semPedidosDisponiveis() {

        Entregador entregador = mock(Entregador.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("entregador@email.com");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador("entregador@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void listarPedidosDisponiveis() {

        Entregador entregador = mock(Entregador.class);
        Pedido pedido = mock(Pedido.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("entregador@email.com");

        when(pedido.getEmailRestaurante()).thenReturn("restaurante@email.com");
        when(pedido.getEmailCliente()).thenReturn("cliente@email.com");
        when(pedido.getTotal()).thenReturn(25.0);
        when(pedido.getStatus()).thenReturn("EM_PREPARO");
        when(pedido.getFormaPagamento()).thenReturn("PIX");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador("entregador@email.com"))
                .thenReturn(List.of(pedido));

        comando.executar(contexto, new Scanner("\n"));
    }
}
