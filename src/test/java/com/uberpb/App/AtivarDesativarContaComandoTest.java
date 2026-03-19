package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AtivarDesativarContaComandoTest {

    private AtivarDesativarContaComando comando;
    private ContextoAplicacao contexto;

    private RepositorioUsuario repositorioUsuario;

    @BeforeEach
    void setup() {

        comando = new AtivarDesativarContaComando();

        // Mock principal
        repositorioUsuario = mock(RepositorioUsuario.class);

        // mocks restantes para satisfazer construtor
        var sessao = mock(Sessao.class);
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
    }

    @Test
    void deveMostrarErroQuandoUsuarioNaoEncontrado() {
        Scanner entrada = mock(Scanner.class);
        when(entrada.nextLine()).thenReturn("email@inexistente.com");

        when(repositorioUsuario.buscarPorEmail("email@inexistente.com")).thenReturn(null);

        assertDoesNotThrow(() -> comando.executar(contexto, entrada));
    }

    @Test
    void naoPodeAlterarAdministrador() {
        Scanner entrada = mock(Scanner.class);
        when(entrada.nextLine()).thenReturn("admin@teste.com");

        // só dois argumentos agora: nome, email
        Administrador admin = new Administrador("Administrador", "admin@teste.com");
        when(repositorioUsuario.buscarPorEmail("admin@teste.com")).thenReturn(admin);

        assertDoesNotThrow(() -> comando.executar(contexto, entrada));
    }

    @Test
    void deveAtivarMotorista() {
        Scanner entrada = mock(Scanner.class);
        when(entrada.nextLine()).thenReturn("motorista@teste.com");

        Motorista motorista = new Motorista("João", "motorista@teste.com");
        motorista.setContaAtiva(false);
        when(repositorioUsuario.buscarPorEmail("motorista@teste.com")).thenReturn(motorista);

        comando.executar(contexto, entrada);

        assertTrue(motorista.isContaAtiva());
        verify(repositorioUsuario).atualizar(motorista);
    }

    @Test
    void deveDesativarPassageiro() {
        Scanner entrada = mock(Scanner.class);
        when(entrada.nextLine()).thenReturn("passageiro@teste.com");

        Passageiro passageiro = new Passageiro("Maria", "passageiro@teste.com");
        passageiro.setContaAtiva(true);
        when(repositorioUsuario.buscarPorEmail("passageiro@teste.com")).thenReturn(passageiro);

        comando.executar(contexto, entrada);

        assertFalse(passageiro.isContaAtiva());
        verify(repositorioUsuario).atualizar(passageiro);
    }
}