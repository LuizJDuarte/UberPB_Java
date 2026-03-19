package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class CancelarCorridaComandoTest {

    private ContextoAplicacao contexto;
    private Sessao sessao;
    private RepositorioCorrida repositorioCorrida;
    private CancelarCorridaComando comando;

    @BeforeEach
    void setup() {

        // Mock da sessão e do usuário logado
        sessao = mock(Sessao.class);
        Passageiro passageiro = mock(Passageiro.class);
        when(passageiro.getEmail()).thenReturn("passageiro@email.com");
        when(sessao.getUsuarioAtual()).thenReturn(passageiro);

        // Mock do repositório de corridas
        repositorioCorrida = mock(RepositorioCorrida.class);

        // Cria o contexto passando todos os mocks (pode usar null nos serviços não usados)
        contexto = new ContextoAplicacao(
                sessao,
                null,  // repositorioUsuario
                null,  // repositorioRestaurante
                null,  // servicoCadastro
                null,  // servicoAutenticacao
                repositorioCorrida,
                null,  // servicoCorrida
                null,  // repositorioOferta
                null,  // repositorioAvaliacao
                null,  // servicoOferta
                null,  // servicoValidacaoMotorista
                null,  // servicoPagamento
                null,  // servicoAvaliacao
                null,  // servicoOtimizacaoRota
                null,  // servicoLocalizacao
                null,  // servicoDirecionamento
                null,  // estimativaChegada
                null,  // servicoAdmin
                null,  // gerenciador
                null,  // servicoCarrinho
                null,  // repositorioPedido
                null,  // servicoPedido
                null,  // repositorioNotificacao
                null,  // servicoNotificacao
                null   // servicoEntrega
        );

        comando = new CancelarCorridaComando();
    }

    @Test
    void deveCancelarCorridaEmAndamento() {
        Corrida corrida = mock(Corrida.class);
        when(corrida.getStatus()).thenReturn(CorridaStatus.EM_ANDAMENTO);

        when(repositorioCorrida.buscarPorPassageiro("passageiro@email.com"))
                .thenReturn(List.of(corrida));

        Scanner scanner = new Scanner("");
        assertDoesNotThrow(() -> comando.executar(contexto, scanner));

        verify(corrida).setStatus(CorridaStatus.CANCELADA);
        verify(repositorioCorrida).atualizar(corrida);
    }

    @Test
    void naoDeveCancelarQuandoNaoHaCorrida() {
        when(repositorioCorrida.buscarPorPassageiro("passageiro@email.com"))
                .thenReturn(List.of()); // sem corridas

        Scanner scanner = new Scanner("");
        assertDoesNotThrow(() -> comando.executar(contexto, scanner));

        verify(repositorioCorrida, never()).atualizar(any());
    }

}