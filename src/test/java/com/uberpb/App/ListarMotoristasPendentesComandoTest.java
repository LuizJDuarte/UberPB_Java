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

public class ListarMotoristasPendentesComandoTest {

    private ContextoAplicacao contexto;
    private Sessao sessao;
    private RepositorioUsuario repositorioUsuario;

    private ListarMotoristasPendentesComando comando;

    @BeforeEach
    void setup() {

        sessao = mock(Sessao.class);
        repositorioUsuario = mock(RepositorioUsuario.class);

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

        comando = new ListarMotoristasPendentesComando();
    }

    @Test
    void testarNome() {
        assertEquals("Listar Motoristas Pendentes", comando.nome());
    }

    @Test
    void visivelParaAdministrador() {
        Administrador admin = mock(Administrador.class);
        assertTrue(comando.visivelPara(admin));
    }

    @Test
    void naoVisivelParaOutroUsuario() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void nenhumMotoristaPendente() {

        Usuario usuario = mock(Usuario.class);

        when(repositorioUsuario.buscarTodos())
                .thenReturn(List.of(usuario));

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void listarMotoristaComPendencia() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(repositorioUsuario.buscarTodos())
                .thenReturn(List.of(motorista));

        when(motorista.getEmail()).thenReturn("motorista@email.com");
        when(motorista.isCnhValida()).thenReturn(false);
        when(motorista.isCrlvValido()).thenReturn(true);
        when(motorista.isContaAtiva()).thenReturn(false);

        when(motorista.getVeiculo()).thenReturn(veiculo);
        when(veiculo.getModelo()).thenReturn("Toyota");
        when(veiculo.getPlaca()).thenReturn("ABC-1234");

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void motoristaSemVeiculo() {

        Motorista motorista = mock(Motorista.class);

        when(repositorioUsuario.buscarTodos())
                .thenReturn(List.of(motorista));

        when(motorista.getEmail()).thenReturn("motorista@email.com");
        when(motorista.isCnhValida()).thenReturn(false);
        when(motorista.isCrlvValido()).thenReturn(false);
        when(motorista.isContaAtiva()).thenReturn(false);

        when(motorista.getVeiculo()).thenReturn(null);

        comando.executar(contexto, new Scanner("\n"));
    }
}

