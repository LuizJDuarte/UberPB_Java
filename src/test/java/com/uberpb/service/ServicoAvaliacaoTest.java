package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioAvaliacao;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoAvaliacaoTest {

    private RepositorioAvaliacao repoAvaliacao;
    private RepositorioCorrida repoCorrida;
    private RepositorioUsuario repoUsuario;

    private ServicoAvaliacao servico;

    @BeforeEach
    void setUp() {
        repoAvaliacao = mock(RepositorioAvaliacao.class);
        repoCorrida = mock(RepositorioCorrida.class);
        repoUsuario = mock(RepositorioUsuario.class);

        servico = new ServicoAvaliacao(repoAvaliacao, repoCorrida, repoUsuario);
    }

    @Test
    void deveAvaliarMotoristaComSucesso() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");
        when(repoAvaliacao.corridaFoiAvaliada("1")).thenReturn(false);

        Motorista motorista = mock(Motorista.class);
        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);

        servico.avaliarMotorista("1", "pass@email.com", 5, "Ótima corrida");

        verify(repoAvaliacao).salvar(any(AvaliacaoPassageiro.class));
        verify(repoCorrida).atualizar(corrida);
        verify(repoUsuario).atualizar(motorista);
    }

    @Test
    void deveAvaliarPassageiroComSucesso() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");
        when(repoAvaliacao.corridaFoiAvaliada("1")).thenReturn(false);

        Passageiro passageiro = mock(Passageiro.class);
        when(repoUsuario.buscarPorEmail("pass@email.com")).thenReturn(passageiro);

        servico.avaliarPassageiro("1", "motorista@email.com", 4, "Bom passageiro");

        verify(repoAvaliacao).salvar(any(AvaliacaoMotorista.class));
        verify(repoCorrida).atualizar(corrida);
        verify(repoUsuario).atualizar(passageiro);
    }

    @Test
    void naoDevePermitirRatingInvalido() {

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            servico.avaliarMotorista("1", "pass@email.com", 6, "erro");
        });

        assertEquals("Rating deve ser entre 1 e 5 estrelas.", ex.getMessage());
    }

    @Test
    void naoDeveAvaliarCorridaInexistente() {

        when(repoCorrida.buscarPorId("1")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            servico.avaliarMotorista("1", "pass@email.com", 5, "ok");
        });

        assertTrue(ex.getMessage().contains("Corrida não encontrada"));
    }

    @Test
    void naoDevePermitirAvaliarCorridaJaAvaliada() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");
        when(repoAvaliacao.corridaFoiAvaliada("1")).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            servico.avaliarMotorista("1", "pass@email.com", 5, "ok");
        });

        assertEquals("Esta corrida já foi avaliada.", ex.getMessage());
    }

    @Test
    void naoDevePermitirPassageiroErradoAvaliarMotorista() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getEmailPassageiro()).thenReturn("outro@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");
        when(repoAvaliacao.corridaFoiAvaliada("1")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            servico.avaliarMotorista("1", "pass@email.com", 5, "ok");
        });
    }

    @Test
    void naoDevePermitirMotoristaErradoAvaliarPassageiro() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getMotoristaAlocado()).thenReturn("outro@email.com");
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");
        when(repoAvaliacao.corridaFoiAvaliada("1")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            servico.avaliarPassageiro("1", "motorista@email.com", 5, "ok");
        });
    }

    @Test
    void deveRetornarAvaliacoesMotorista() {

        List<Avaliacao> lista = List.of(mock(Avaliacao.class));

        when(repoAvaliacao.buscarPorMotorista("motorista@email.com"))
                .thenReturn(lista);

        List<Avaliacao> resultado = servico.getAvaliacoesMotorista("motorista@email.com");

        assertEquals(1, resultado.size());
    }

    @Test
    void deveRetornarAvaliacoesPassageiro() {

        List<Avaliacao> lista = List.of(mock(Avaliacao.class));

        when(repoAvaliacao.buscarPorPassageiro("pass@email.com"))
                .thenReturn(lista);

        List<Avaliacao> resultado = servico.getAvaliacoesPassageiro("pass@email.com");

        assertEquals(1, resultado.size());
    }

    @Test
    void devePermitirAvaliarCorrida() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.isAvaliada()).thenReturn(false);
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");

        boolean resultado = servico.podeAvaliarCorrida("1", "pass@email.com");

        assertTrue(resultado);
    }


    @Test
    void deveListarCorridasParaAvaliar() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);
        when(corrida.isAvaliada()).thenReturn(false);
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");

        when(repoCorrida.buscarTodas()).thenReturn(List.of(corrida));

        List<Corrida> resultado = servico.getCorridasParaAvaliar("pass@email.com");

        assertEquals(1, resultado.size());
    }

    @Test
    void naoDeveListarCorridasNaoConcluidas() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getStatus()).thenReturn(CorridaStatus.EM_ANDAMENTO);
        when(repoCorrida.buscarTodas()).thenReturn(List.of(corrida));

        List<Corrida> resultado = servico.getCorridasParaAvaliar("user@email.com");

        assertTrue(resultado.isEmpty());
    }
}
