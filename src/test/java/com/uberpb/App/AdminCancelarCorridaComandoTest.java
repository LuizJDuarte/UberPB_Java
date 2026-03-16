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

public class AdminCancelarCorridaComandoTest {

    private ContextoAplicacao contexto;
    private RepositorioCorrida repositorioCorrida;
    private RepositorioUsuario repositorioUsuario;

    private AdminCancelarCorridaComando comando;

    @BeforeEach
    void setup() {

        repositorioCorrida = mock(RepositorioCorrida.class);
        repositorioUsuario = mock(RepositorioUsuario.class);

        var sessao = mock(Sessao.class);
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

        comando = new AdminCancelarCorridaComando();
    }

    @Test
    void testarNome() {
        assertEquals("Cancelar Corrida (Admin)", comando.nome());
    }

    @Test
    void visivelParaAdmin() {

        Usuario admin = mock(Usuario.class);
        when(admin.getTipo()).thenReturn(TipoUsuario.ADMIN);

        assertTrue(comando.visivelPara(admin));
    }

    @Test
    void naoVisivelParaUsuarioNormal() {

        Usuario usuario = mock(Usuario.class);
        when(usuario.getTipo()).thenReturn(TipoUsuario.PASSAGEIRO_CLIENTE);

        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void nenhumaCorrida() {

        when(repositorioCorrida.buscarTodas()).thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void cancelarCorridaComSucesso() {

        Corrida corrida = mock(Corrida.class);

        when(repositorioCorrida.buscarTodas()).thenReturn(List.of(corrida));

        when(corrida.getId()).thenReturn("corrida1");
        when(corrida.getEmailPassageiro()).thenReturn("cliente@email.com");
        when(corrida.getOrigemEndereco()).thenReturn("Rua A");
        when(corrida.getDestinoEndereco()).thenReturn("Rua B");

        when(repositorioUsuario.buscarPorEmail("cliente@email.com"))
                .thenReturn(mock(Passageiro.class));

        when(repositorioCorrida.buscarPorId("corrida1"))
                .thenReturn(corrida);

        comando.executar(contexto, new Scanner("corrida1\n"));

        verify(corrida).setStatus(CorridaStatus.CANCELADA);
        verify(repositorioCorrida).atualizar(corrida);
    }

    @Test
    void corridaNaoEncontrada() {

        Corrida corrida = mock(Corrida.class);

        when(repositorioCorrida.buscarTodas()).thenReturn(List.of(corrida));

        when(corrida.getId()).thenReturn("corrida1");
        when(corrida.getEmailPassageiro()).thenReturn("cliente@email.com");

        when(repositorioUsuario.buscarPorEmail("cliente@email.com"))
                .thenReturn(mock(Passageiro.class));

        when(repositorioCorrida.buscarPorId("999"))
                .thenReturn(null);

        comando.executar(contexto, new Scanner("999\n"));
    }
}
