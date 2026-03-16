
package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SolicitarCorridaComandoTest {

    private SolicitarCorridaComando comando;

    private ContextoAplicacao contexto;

    private Sessao sessao;
    private ServicoCorrida servicoCorrida;
    private ServicoOferta servicoOferta;

    private Passageiro passageiro;

    @BeforeEach
    void setup() {

        comando = new SolicitarCorridaComando();

        sessao = mock(Sessao.class);
        servicoCorrida = mock(ServicoCorrida.class);
        servicoOferta = mock(ServicoOferta.class);

        passageiro = mock(Passageiro.class);

        when(passageiro.getEmail()).thenReturn("passageiro@email.com");
        when(sessao.getUsuarioAtual()).thenReturn(passageiro);

        var repositorioUsuario = mock(RepositorioUsuario.class);
        var repositorioRestaurante = mock(RepositorioRestaurante.class);
        var servicoCadastro = mock(ServicoCadastro.class);
        var servicoAutenticacao = mock(ServicoAutenticacao.class);
        var repositorioCorrida = mock(RepositorioCorrida.class);
        var repositorioOferta = mock(RepositorioOferta.class);
        var repositorioAvaliacao = mock(RepositorioAvaliacao.class);
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
    void deveRetornarNomeComando() {
        assertEquals("Solicitar Corrida (informar endereços)", comando.nome());
    }

    @Test
    void visivelParaPassageiro() {
        assertTrue(comando.visivelPara(passageiro));
    }

    @Test
    void naoVisivelParaOutroUsuario() {

        Usuario usuario = mock(Usuario.class);

        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void solicitarCorridaConfirmada() {

        EstimativaCorrida estimativa = mock(EstimativaCorrida.class);

        when(estimativa.getDistanciaKm()).thenReturn(10.0);
        when(estimativa.getMinutos()).thenReturn(15);
        when(estimativa.getPreco()).thenReturn(25.0);

        when(servicoCorrida.estimarPorEnderecos(any(), any(), any()))
                .thenReturn(estimativa);

        Corrida corrida = mock(Corrida.class);
        when(corrida.getId()).thenReturn("12345678");

        when(servicoCorrida.solicitarCorrida(any(), any(), any(), any(), any()))
                .thenReturn(corrida);

        when(servicoOferta.criarOfertasParaCorrida(corrida))
                .thenReturn(3);

        String entrada =
                "Origem\n" +
                "Destino\n" +
                "1\n" +
                "1\n" +
                "s\n";

        comando.executar(contexto, new Scanner(entrada));

        verify(servicoCorrida).solicitarCorrida(any(), any(), any(), any(), any());
        verify(servicoOferta).criarOfertasParaCorrida(corrida);
    }

    @Test
    void solicitarCorridaCancelada() {

        EstimativaCorrida estimativa = mock(EstimativaCorrida.class);

        when(servicoCorrida.estimarPorEnderecos(any(), any(), any()))
                .thenReturn(estimativa);

        String entrada =
                "Origem\n" +
                "Destino\n" +
                "1\n" +
                "1\n" +
                "n\n";

        comando.executar(contexto, new Scanner(entrada));

        verify(servicoCorrida, never())
                .solicitarCorrida(any(), any(), any(), any(), any());
    }

    @Test
    void categoriaInvalidaSelecionaPrimeira() {

        EstimativaCorrida estimativa = mock(EstimativaCorrida.class);

        when(servicoCorrida.estimarPorEnderecos(any(), any(), any()))
                .thenReturn(estimativa);

        String entrada =
                "Origem\n" +
                "Destino\n" +
                "99\n" +
                "1\n" +
                "n\n";

        comando.executar(contexto, new Scanner(entrada));

        verify(servicoCorrida).estimarPorEnderecos(any(), any(), any());
    }

    @Test
    void pagamentoInvalidoSelecionaPrimeiro() {

        EstimativaCorrida estimativa = mock(EstimativaCorrida.class);

        when(servicoCorrida.estimarPorEnderecos(any(), any(), any()))
                .thenReturn(estimativa);

        String entrada =
                "Origem\n" +
                "Destino\n" +
                "1\n" +
                "99\n" +
                "n\n";

        comando.executar(contexto, new Scanner(entrada));

        verify(servicoCorrida).estimarPorEnderecos(any(), any(), any());
    }

    @Test
    void erroParseNumeroCategoria() {

        EstimativaCorrida estimativa = mock(EstimativaCorrida.class);

        when(servicoCorrida.estimarPorEnderecos(any(), any(), any()))
                .thenReturn(estimativa);

        String entrada =
                "Origem\n" +
                "Destino\n" +
                "abc\n" +
                "1\n" +
                "n\n";

        comando.executar(contexto, new Scanner(entrada));

        verify(servicoCorrida).estimarPorEnderecos(any(), any(), any());
    }
}

