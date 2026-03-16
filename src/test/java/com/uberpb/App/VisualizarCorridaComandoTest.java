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

public class VisualizarCorridaComandoTest {

    private ContextoAplicacao contexto;
    private Sessao sessao;
    private RepositorioCorrida repositorioCorrida;

    private VisualizarCorridaComando comando;

    @BeforeEach
    void setup() {

        sessao = mock(Sessao.class);
        repositorioCorrida = mock(RepositorioCorrida.class);

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

        comando = new VisualizarCorridaComando();
    }

    @Test
    void testarNome() {
        assertEquals("Visualizar Corrida (detalhes)", comando.nome());
    }

    @Test
    void visivelParaPassageiro() {
        Passageiro passageiro = mock(Passageiro.class);
        assertTrue(comando.visivelPara(passageiro));
    }

    @Test
    void naoVisivelParaOutroUsuario() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void passageiroSemCorridas() {

        Passageiro passageiro = mock(Passageiro.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void visualizarUltimaCorrida() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        when(corrida.getId()).thenReturn("corrida1");
        when(corrida.getOrigemEndereco()).thenReturn("Rua A");
        when(corrida.getDestinoEndereco()).thenReturn("Rua B");
        when(corrida.getStatus()).thenReturn(CorridaStatus.SOLICITADA);

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void selecionarCorridaPorIndice() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida1 = mock(Corrida.class);
        Corrida corrida2 = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida1, corrida2));

        when(corrida1.getId()).thenReturn("corrida1");
        when(corrida2.getId()).thenReturn("corrida2");

        when(corrida1.getOrigemEndereco()).thenReturn("Rua A");
        when(corrida1.getDestinoEndereco()).thenReturn("Rua B");
        when(corrida1.getStatus()).thenReturn(CorridaStatus.SOLICITADA);

        when(corrida2.getOrigemEndereco()).thenReturn("Rua C");
        when(corrida2.getDestinoEndereco()).thenReturn("Rua D");
        when(corrida2.getStatus()).thenReturn(CorridaStatus.ACEITA);

        comando.executar(contexto, new Scanner("1\n"));
    }

    @Test
    void indiceInvalido() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        when(corrida.getId()).thenReturn("corrida1");

        comando.executar(contexto, new Scanner("5\n"));
    }

    @Test
    void entradaNaoNumerica() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        when(corrida.getId()).thenReturn("corrida1");

        comando.executar(contexto, new Scanner("abc\n"));
    }
}
