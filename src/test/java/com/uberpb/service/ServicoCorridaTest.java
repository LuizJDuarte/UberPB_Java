package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoCorridaTest {

    private RepositorioCorrida repoCorrida;
    private RepositorioUsuario repoUsuario;
    private EstimativaCorrida estimativaCorrida;
    private ServicoLocalizacao servicoLocalizacao;
    private ServicoCorrida servicoCorrida;

    @BeforeEach
    void setup() {

        repoCorrida = mock(RepositorioCorrida.class);
        repoUsuario = mock(RepositorioUsuario.class);
        estimativaCorrida = mock(EstimativaCorrida.class);
        servicoLocalizacao = mock(ServicoLocalizacao.class);

        servicoCorrida = new ServicoCorrida(
                repoCorrida,
                repoUsuario,
                estimativaCorrida,
                servicoLocalizacao);
    }

    @Test
    @DisplayName("Deve estimar corrida por endereços")
    void deveEstimarCorrida() {

        EstimativaCorrida estimativa = new EstimativaCorrida(5.0, 10, 25.0);

        when(estimativaCorrida.calcularPorEnderecos(
                "Shopping Manaira",
                "UFPB",
                CategoriaVeiculo.BLACK)).thenReturn(estimativa);

        EstimativaCorrida resultado = servicoCorrida.estimarPorEnderecos(
                "Shopping Manaira",
                "UFPB",
                CategoriaVeiculo.BLACK);

        assertEquals(10, resultado.getMinutos());
    }

    @Test
    @DisplayName("Passageiro João deve conseguir solicitar corrida")
    void deveSolicitarCorrida() {

        Passageiro joao = new Passageiro("joao@email.com", "123");

        when(repoUsuario.buscarPorEmail("joao@email.com")).thenReturn(joao);

        Corrida corrida = servicoCorrida.solicitarCorrida(
                "joao@email.com",
                "Shopping Manaira",
                "UFPB",
                CategoriaVeiculo.COMFORT,
                MetodoPagamento.PIX);

        assertNotNull(corrida);

        verify(repoCorrida).salvar(any(Corrida.class));
    }

    @Test
    @DisplayName("Não deve permitir passageiro inválido")
    void naoDevePermitirPassageiroInvalido() {

        assertThrows(IllegalArgumentException.class, () -> {

            servicoCorrida.solicitarCorrida(
                    "",
                    "Shopping Manaira",
                    "UFPB",
                    CategoriaVeiculo.BLACK,
                    MetodoPagamento.PIX);

        });
    }

    @Test
    @DisplayName("Não deve permitir origem vazia")
    void naoDevePermitirOrigemVazia() {

        Passageiro joao = new Passageiro("joao@email.com", "123");

        when(repoUsuario.buscarPorEmail("joao@email.com")).thenReturn(joao);

        assertThrows(IllegalArgumentException.class, () -> {

            servicoCorrida.solicitarCorrida(
                    "joao@email.com",
                    "",
                    "UFPB",
                    CategoriaVeiculo.BLACK,
                    MetodoPagamento.PIX);

        });
    }

    @Test
    @DisplayName("Deve iniciar corrida aceita")
    void deveIniciarCorrida() {

        GerenciadorCorridasAtivas gerenciador = mock(GerenciadorCorridasAtivas.class);

        Corrida corrida = mock(Corrida.class);

        when(corrida.getId()).thenReturn("corrida123");
        when(corrida.getStatus()).thenReturn(CorridaStatus.ACEITA);
        when(gerenciador.isAtiva("corrida123")).thenReturn(false);

        EstimativaCorrida estimativa = new EstimativaCorrida(5.0, 10, 25.0);

        when(estimativaCorrida.calcularPorEnderecos(any(), any(), any()))
                .thenReturn(estimativa);

        servicoCorrida.iniciarSeAceita(corrida, gerenciador);

        verify(gerenciador).iniciar(anyString(), anyInt(), anyDouble());
    }

    @Test
    @DisplayName("Deve atualizar status quando corrida concluída no progresso")
    void deveAtualizarStatusNoProgresso() {

        GerenciadorCorridasAtivas gerenciador = mock(GerenciadorCorridasAtivas.class);

        GerenciadorCorridasAtivas.Progresso progresso = new GerenciadorCorridasAtivas.Progresso(
                "corrida123",
                5,
                3.5,
                100,
                true);

        when(gerenciador.progresso("corrida123")).thenReturn(progresso);

        Corrida corrida = mock(Corrida.class);

        when(corrida.getStatus()).thenReturn(CorridaStatus.EM_ANDAMENTO);

        when(repoCorrida.buscarPorId("corrida123")).thenReturn(corrida);

        servicoCorrida.progresso("corrida123", gerenciador);

        verify(repoCorrida).atualizar(corrida);
        verify(gerenciador).encerrar("corrida123");
    }

    @Test
    @DisplayName("Deve encerrar corrida concluída")
    void deveEncerrarCorrida() {

        GerenciadorCorridasAtivas gerenciador = mock(GerenciadorCorridasAtivas.class);

        Corrida corrida = mock(Corrida.class);

        when(corrida.getId()).thenReturn("corrida123");

        when(gerenciador.isConcluida("corrida123")).thenReturn(true);
        when(repoCorrida.buscarPorId("corrida123")).thenReturn(corrida);

        servicoCorrida.encerrarSeConcluida("corrida123", gerenciador);

        verify(repoCorrida).atualizar(corrida);
        verify(gerenciador).encerrar("corrida123");
    }

    @Test
    @DisplayName("Passageiro pode concluir corrida")
    void passageiroPodeConcluirCorrida() {

        GerenciadorCorridasAtivas gerenciador = mock(GerenciadorCorridasAtivas.class);

        Corrida corrida = mock(Corrida.class);

        when(corrida.getEmailPassageiro()).thenReturn("joao@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("maria@email.com");

        when(repoCorrida.buscarPorId("corrida123")).thenReturn(corrida);

        servicoCorrida.concluirCorrida("corrida123", "joao@email.com", gerenciador);

        verify(repoCorrida).atualizar(corrida);
        verify(gerenciador).encerrar("corrida123");
    }

}