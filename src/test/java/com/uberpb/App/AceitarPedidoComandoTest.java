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

        contexto = criarContextoBase(
                mock(ServicoCadastro.class),
                mock(ServicoOtimizacaoRota.class)
        );

        comando = new AceitarPedidoComando();
    }

    // 🔧 helper pra evitar repetir 30 mocks
    private ContextoAplicacao criarContextoBase(
            ServicoCadastro servicoCadastro,
            ServicoOtimizacaoRota servicoOtimizacaoRota
    ) {
        return new ContextoAplicacao(
                sessao,
                mock(RepositorioUsuario.class),
                mock(RepositorioRestaurante.class),
                servicoCadastro,
                mock(ServicoAutenticacao.class),
                mock(RepositorioCorrida.class),
                mock(ServicoCorrida.class),
                mock(RepositorioOferta.class),
                mock(RepositorioAvaliacao.class),
                mock(ServicoOferta.class),
                mock(ServicoValidacaoMotorista.class),
                mock(ServicoPagamento.class),
                mock(ServicoAvaliacao.class),
                servicoOtimizacaoRota,
                mock(ServicoLocalizacao.class),
                mock(ServicoDirecionamentoCorrida.class),
                mock(EstimativaChegada.class),
                mock(ServicoAdmin.class),
                mock(GerenciadorCorridasAtivas.class),
                mock(ServicoCarrinho.class),
                mock(RepositorioPedido.class),
                servicoPedido,
                mock(RepositorioNotificacao.class),
                mock(ServicoNotificacao.class),
                servicoEntrega
        );
    }

    // ✅ nome()
    @Test
    void deveRetornarNomeCorreto() {
        assert comando.nome().equals("[Entregador] Aceitar Pedido");
    }

    // ✅ visivelPara()
    @Test
    void visivelParaEntregador() {
        assert comando.visivelPara(mock(Entregador.class));
    }

    @Test
    void naoVisivelParaOutroUsuario() {
        assert !comando.visivelPara(mock(Usuario.class));
    }

    // ❌ usuário não é entregador
    @Test
    void usuarioNaoEntregador() {

        Usuario usuario = mock(Usuario.class);
        when(sessao.getUsuarioAtual()).thenReturn(usuario);

        comando.executar(contexto, new Scanner(""));

        verify(sessao).getUsuarioAtual();
    }

    // ❌ conta inativa
    @Test
    void contaNaoAtiva() {

        Entregador entregador = mock(Entregador.class);
        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(false);

        comando.executar(contexto, new Scanner(""));

        verify(entregador).isContaAtiva();
    }

    // ❌ indisponível
    @Test
    void entregadorIndisponivel() {

        Entregador entregador = mock(Entregador.class);
        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(false);

        comando.executar(contexto, new Scanner(""));

        verify(entregador).isDisponivel();
    }

    // ❌ sem pedidos
    @Test
    void semPedidosDisponiveis() {

        Entregador entregador = mock(Entregador.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("email");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador(any()))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner(""));

        verify(servicoPedido).buscarPedidosDisponiveisParaEntregador(any());
    }

    // ❌ entrada inválida
    @Test
    void entradaInvalida() {

        Entregador entregador = mock(Entregador.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("email");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador(any()))
                .thenReturn(List.of(mock(Pedido.class)));

        comando.executar(contexto, new Scanner("abc\n"));
    }

    // ❌ cancelamento
    @Test
    void cancelarOperacao() {

        Entregador entregador = mock(Entregador.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("email");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador(any()))
                .thenReturn(List.of(mock(Pedido.class)));

        comando.executar(contexto, new Scanner("0\n"));
    }

    // ❌ confirmação negativa
    @Test
    void naoConfirmarPedido() {

        Entregador entregador = mock(Entregador.class);
        Pedido pedido = mock(Pedido.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);
        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("email");

        when(pedido.getEmailRestaurante()).thenReturn("rest");
        when(pedido.getEmailCliente()).thenReturn("cli");
        when(pedido.getTotal()).thenReturn(10.0);
        when(pedido.getStatus()).thenReturn("OK");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador(any()))
                .thenReturn(List.of(pedido));

        comando.executar(contexto, new Scanner("1\nn\n"));

        verify(servicoEntrega, never()).aceitarPedido(any(), any());
    }

    // ✅ sucesso simples
    @Test
    void aceitarPedidoComSucesso() {

        Entregador entregador = mock(Entregador.class);
        Pedido pedido = mock(Pedido.class);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);

        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("entregador@email.com");

        when(pedido.getEmailRestaurante()).thenReturn("rest@email.com");
        when(pedido.getEmailCliente()).thenReturn("cli@email.com");
        when(pedido.getTotal()).thenReturn(50.0);
        when(pedido.getStatus()).thenReturn("EM_PREPARO");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador(any()))
                .thenReturn(List.of(pedido));

        when(servicoEntrega.aceitarPedido(any(), any())).thenReturn(true);

        comando.executar(contexto, new Scanner("1\ns\n"));

        verify(servicoEntrega).aceitarPedido(any(), any());
    }

    // 🔥 fluxo COMPLETO com rota (100% cobertura)
    @Test
    void fluxoCompletoComRota() {

        Entregador entregador = mock(Entregador.class);
        Pedido pedido = mock(Pedido.class);

        ServicoCadastro servicoCadastro = mock(ServicoCadastro.class);
        ServicoOtimizacaoRota servicoRota = mock(ServicoOtimizacaoRota.class);

        contexto = criarContextoBase(servicoCadastro, servicoRota);

        when(sessao.getUsuarioAtual()).thenReturn(entregador);

        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);
        when(entregador.getEmail()).thenReturn("email");

        var loc1 = new com.uberpb.model.Localizacao(1,1);
        var loc2 = new com.uberpb.model.Localizacao(2,2);
        var loc3 = new com.uberpb.model.Localizacao(3,3);

        when(entregador.getLocalizacao()).thenReturn(loc1);

        Usuario rest = mock(Usuario.class);
        Usuario cli = mock(Usuario.class);

        when(rest.getLocalizacao()).thenReturn(loc2);
        when(cli.getLocalizacao()).thenReturn(loc3);

        when(servicoCadastro.buscar("rest")).thenReturn(rest);
        when(servicoCadastro.buscar("cli")).thenReturn(cli);

        when(pedido.getEmailRestaurante()).thenReturn("rest");
        when(pedido.getEmailCliente()).thenReturn("cli");
        when(pedido.getTotal()).thenReturn(10.0);
        when(pedido.getStatus()).thenReturn("OK");

        when(servicoPedido.buscarPedidosDisponiveisParaEntregador(any()))
                .thenReturn(List.of(pedido));

        when(servicoEntrega.aceitarPedido(any(), any())).thenReturn(true);

        var rota = mock(RotaOtimizada.class);
        when(rota.getDistanciaKm()).thenReturn(5.0);
        when(rota.getTempoEstimadoMinutos()).thenReturn(10.0);
        when(rota.getPontosRota()).thenReturn(List.of(loc1, loc2));

        when(servicoRota.calcularRotaOtimizada(any(), any())).thenReturn(rota);

        comando.executar(contexto, new Scanner("1\ns\n"));

        verify(servicoRota, times(2)).calcularRotaOtimizada(any(), any());
    }
}