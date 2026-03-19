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

public class AdminVisualizarCorridasComandoTest {

    private AdminVisualizarCorridasComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private RepositorioUsuario repositorioUsuario;
    private RepositorioRestaurante repositorioRestaurante;
    private RepositorioCorrida repositorioCorrida;
    private RepositorioOferta repositorioOferta;
    private RepositorioAvaliacao repositorioAvaliacao;
    private RepositorioPedido repositorioPedido;
    private RepositorioNotificacao repositorioNotificacao;

    @BeforeEach
    void setup() {
        comando = new AdminVisualizarCorridasComando();

        sessao = mock(Sessao.class);
        repositorioUsuario = mock(RepositorioUsuario.class);
        repositorioRestaurante = mock(RepositorioRestaurante.class);
        repositorioCorrida = mock(RepositorioCorrida.class);
        repositorioOferta = mock(RepositorioOferta.class);
        repositorioAvaliacao = mock(RepositorioAvaliacao.class);
        repositorioPedido = mock(RepositorioPedido.class);
        repositorioNotificacao = mock(RepositorioNotificacao.class);

        // Mocks restantes de serviços que não afetam diretamente o teste
        var servicoCadastro = mock(ServicoCadastro.class);
        var servicoAutenticacao = mock(ServicoAutenticacao.class);
        var servicoCorrida = mock(ServicoCorrida.class);
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
        var servicoPedido = mock(ServicoPedido.class);
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
                "Visualizar Todas as Corridas",
                comando.nome()
        );
    }

    @Test
    void testVisivelParaAdmin() {
        Usuario admin = mock(Usuario.class);
        when(admin.getTipo()).thenReturn(TipoUsuario.ADMIN);
        assertTrue(comando.visivelPara(admin));
    }

    @Test
    void testNaoVisivelParaOutroUsuario() {
        Usuario passageiro = mock(Usuario.class);
        when(passageiro.getTipo()).thenReturn(TipoUsuario.PASSAGEIRO_CLIENTE);
        assertFalse(comando.visivelPara(passageiro));
    }

    @Test
    void testExecutarSemCorridas() {
        Usuario admin = mock(Usuario.class);
        when(sessao.getUsuarioAtual()).thenReturn(admin);
        when(admin.getTipo()).thenReturn(TipoUsuario.ADMIN);

        when(repositorioCorrida.buscarTodas()).thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));

        verify(repositorioCorrida).buscarTodas();
    }

    @Test
    void testExecutarComCorridas() {
        Usuario admin = mock(Usuario.class);
        when(sessao.getUsuarioAtual()).thenReturn(admin);
        when(admin.getTipo()).thenReturn(TipoUsuario.ADMIN);

        Corrida corrida = mock(Corrida.class);

        when(corrida.getId()).thenReturn("1");
        when(corrida.getEmailPassageiro()).thenReturn("user@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");
        when(corrida.getOrigemEndereco()).thenReturn("Rua A, 123");
        when(corrida.getDestinoEndereco()).thenReturn("Av B, 456");
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);

        when(repositorioCorrida.buscarTodas()).thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("\n"));

        verify(repositorioCorrida).buscarTodas();
    }
}