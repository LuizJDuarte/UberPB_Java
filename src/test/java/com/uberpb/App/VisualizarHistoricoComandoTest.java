package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class VisualizarHistoricoComandoTest {

    private VisualizarHistoricoComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private RepositorioCorrida repositorioCorrida;

    @BeforeEach
    void setup() {

        comando = new VisualizarHistoricoComando();

        sessao = mock(Sessao.class);
        repositorioCorrida = mock(RepositorioCorrida.class);

        // mocks necessários para o construtor
        var repositorioUsuario = mock(RepositorioUsuario.class);
        var repositorioRestaurante = mock(RepositorioRestaurante.class);
        var servicoCadastro = mock(ServicoCadastro.class);
        var servicoAutenticacao = mock(ServicoAutenticacao.class);
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
    void testNome() {

        assertEquals(
                "Histórico de Corridas (filtrar por categoria)",
                comando.nome()
        );
    }

    @Test
    void testVisivelParaPassageiro() {

        Passageiro passageiro = mock(Passageiro.class);

        assertTrue(comando.visivelPara(passageiro));
    }

    @Test
    void testVisivelParaMotorista() {

        Motorista motorista = mock(Motorista.class);

        assertTrue(comando.visivelPara(motorista));
    }

    @Test
    void testVisivelParaOutroUsuario() {

        Usuario usuario = mock(Usuario.class);

        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void testExecutarSemCorridas() {

        Passageiro passageiro = mock(Passageiro.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("user@email.com");

        when(repositorioCorrida.buscarPorPassageiro("user@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));

        verify(repositorioCorrida).buscarPorPassageiro("user@email.com");
    }

    @Test
    void testExecutarComCorridaSemFiltro() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("user@email.com");

        when(corrida.getId()).thenReturn("corrida1");
        when(corrida.getOrigemEndereco()).thenReturn("Origem");
        when(corrida.getDestinoEndereco()).thenReturn("Destino");
        when(corrida.getCategoriaEscolhida()).thenReturn(CategoriaVeiculo.UBERX);
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);

        when(repositorioCorrida.buscarPorPassageiro("user@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("\n"));

        verify(repositorioCorrida).buscarPorPassageiro("user@email.com");
        verify(corrida).getId();
        verify(corrida).getStatus();
    }

    @Test
    void testExecutarComFiltroCategoria() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("user@email.com");

        when(corrida.getId()).thenReturn("corrida1");
        when(corrida.getOrigemEndereco()).thenReturn("A");
        when(corrida.getDestinoEndereco()).thenReturn("B");
        when(corrida.getCategoriaEscolhida()).thenReturn(CategoriaVeiculo.UBERX);
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);

        when(repositorioCorrida.buscarPorPassageiro("user@email.com"))
                .thenReturn(List.of(corrida));

        // usuário escolhe categoria 1 (UBERX)
        comando.executar(contexto, new Scanner("1\n"));

        verify(repositorioCorrida).buscarPorPassageiro("user@email.com");
    }
}