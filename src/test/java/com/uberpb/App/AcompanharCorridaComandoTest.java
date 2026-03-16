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

public class AcompanharCorridaComandoTest {

    private AcompanharCorridaComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private RepositorioCorrida repositorioCorrida;
    private ServicoCorrida servicoCorrida;
    private GerenciadorCorridasAtivas gerenciador;

    @BeforeEach
    void setup() {

        comando = new AcompanharCorridaComando();

        sessao = mock(Sessao.class);
        repositorioCorrida = mock(RepositorioCorrida.class);
        servicoCorrida = mock(ServicoCorrida.class);
        gerenciador = mock(GerenciadorCorridasAtivas.class);

        var repositorioUsuario = mock(RepositorioUsuario.class);
        var repositorioRestaurante = mock(RepositorioRestaurante.class);
        var servicoCadastro = mock(ServicoCadastro.class);
        var servicoAutenticacao = mock(ServicoAutenticacao.class);
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
                gerenciador,
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
        assertEquals("Acompanhar Corrida (tempo real)", comando.nome());
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
        when(passageiro.getTipo()).thenReturn(TipoUsuario.PASSAGEIRO_CLIENTE);

        when(repositorioCorrida.buscarPorPassageiro("user@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));

        verify(repositorioCorrida).buscarPorPassageiro("user@email.com");
    }

    @Test
    void testEntradaInvalida() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("user@email.com");
        when(passageiro.getTipo()).thenReturn(TipoUsuario.PASSAGEIRO_CLIENTE);

        when(repositorioCorrida.buscarPorPassageiro("user@email.com"))
                .thenReturn(List.of(corrida));

        when(corrida.getId()).thenReturn("12345678");
        when(corrida.getOrigemEndereco()).thenReturn("A");
        when(corrida.getDestinoEndereco()).thenReturn("B");
        when(corrida.getStatus()).thenReturn(CorridaStatus.ACEITA);

        comando.executar(contexto, new Scanner("abc\n"));

        verify(repositorioCorrida).buscarPorPassageiro("user@email.com");
    }

    @Test
    void testExecutarSelecionarCorridaESair() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("user@email.com");
        when(passageiro.getTipo()).thenReturn(TipoUsuario.PASSAGEIRO_CLIENTE);

        when(repositorioCorrida.buscarPorPassageiro("user@email.com"))
                .thenReturn(List.of(corrida));

        when(corrida.getId()).thenReturn("1234567890abcdef");
        when(corrida.getOrigemEndereco()).thenReturn("Origem");
        when(corrida.getDestinoEndereco()).thenReturn("Destino");
        when(corrida.getStatus()).thenReturn(CorridaStatus.ACEITA);

        when(gerenciador.isAtiva(any())).thenReturn(true);

        GerenciadorCorridasAtivas.Progresso progresso =
                new GerenciadorCorridasAtivas.Progresso("corrida1", 10, 5, 5, false);

        when(servicoCorrida.progresso(anyString(), any())).thenReturn(progresso);

        comando.executar(contexto, new Scanner("1\nq\n"));

        verify(servicoCorrida).progresso(anyString(), any());
    }
}