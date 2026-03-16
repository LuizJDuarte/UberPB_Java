package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DetalhesPagamentoComandoTest {

    private DetalhesPagamentoComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private RepositorioCorrida repositorioCorrida;

    @BeforeEach
    void setup() {

        comando = new DetalhesPagamentoComando();

        sessao = mock(Sessao.class);
        repositorioCorrida = mock(RepositorioCorrida.class);

        Passageiro passageiro = mock(Passageiro.class);
        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        var repositorioUsuario = mock(com.uberpb.repository.RepositorioUsuario.class);
        var repositorioRestaurante = mock(com.uberpb.repository.RepositorioRestaurante.class);
        var servicoCadastro = mock(ServicoCadastro.class);
        var servicoAutenticacao = mock(ServicoAutenticacao.class);
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
        var servicoPedido = mock(ServicoPedido.class);
        var repositorioNotificacao = mock(com.uberpb.repository.RepositorioNotificacao.class);
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

        String nome = comando.nome();

        assertEquals("Ver Detalhes de Pagamento", nome);
    }

    @Test
    void visivelParaPassageiro() {

        Passageiro passageiro = mock(Passageiro.class);

        boolean resultado = comando.visivelPara(passageiro);

        assertTrue(resultado);
    }

    @Test
    void naoVisivelParaOutroUsuario() {

        Usuario usuario = mock(Usuario.class);

        boolean resultado = comando.visivelPara(usuario);

        assertFalse(resultado);
    }

    @Test
    void executarSemCorridas() {

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));

        verify(repositorioCorrida).buscarPorPassageiro("cliente@email.com");
    }

    @Test
    void executarComCorrida() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getId()).thenReturn("123456789");
        when(corrida.getOrigemEndereco()).thenReturn("Origem");
        when(corrida.getDestinoEndereco()).thenReturn("Destino");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("1\n"));

        verify(repositorioCorrida).buscarPorPassageiro("cliente@email.com");
    }

    @Test
    void executarOpcaoInvalida() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getId()).thenReturn("123456789");
        when(corrida.getOrigemEndereco()).thenReturn("Origem");
        when(corrida.getDestinoEndereco()).thenReturn("Destino");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("5\n"));

        verify(repositorioCorrida).buscarPorPassageiro("cliente@email.com");
    }

    @Test
    void executarEntradaNaoNumerica() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getId()).thenReturn("123456789");
        when(corrida.getOrigemEndereco()).thenReturn("Origem");
        when(corrida.getDestinoEndereco()).thenReturn("Destino");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("abc\n"));

        verify(repositorioCorrida).buscarPorPassageiro("cliente@email.com");
    }

    @Test
    void corridaSemCategoria() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getId()).thenReturn("123456789");
        when(corrida.getOrigemEndereco()).thenReturn("Origem");
        when(corrida.getDestinoEndereco()).thenReturn("Destino");
        when(corrida.getCategoriaEscolhida()).thenReturn(null);

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("1\n"));

        verify(repositorioCorrida).buscarPorPassageiro("cliente@email.com");
    }

    @Test
    void corridaComCategoria() {

        Corrida corrida = mock(Corrida.class);
        CategoriaVeiculo categoria = mock(CategoriaVeiculo.class);

        when(corrida.getId()).thenReturn("123456789");
        when(corrida.getOrigemEndereco()).thenReturn("Origem");
        when(corrida.getDestinoEndereco()).thenReturn("Destino");
        when(corrida.getCategoriaEscolhida()).thenReturn(categoria);
        when(categoria.getNome()).thenReturn("UberX");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("1\n"));

        verify(repositorioCorrida).buscarPorPassageiro("cliente@email.com");
    }
}
