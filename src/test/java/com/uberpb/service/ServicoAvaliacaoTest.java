package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioAvaliacao;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
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

        when(repoAvaliacao.buscarPorCorrida(anyString())).thenReturn(List.of());
    }

    @Test
    void deveAvaliarMotoristaComSucessoERegistrarNotaComentario() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");

        Motorista motorista = mock(Motorista.class);
        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);

        servico.avaliarMotorista("1", "pass@email.com", 5, "Ótima corrida");

        ArgumentCaptor<Avaliacao> captor = ArgumentCaptor.forClass(Avaliacao.class);
        verify(repoAvaliacao).salvar(captor.capture());

        AvaliacaoPassageiro avaliacao = (AvaliacaoPassageiro) captor.getValue();
        assertEquals(5, avaliacao.getRating());
        assertEquals("Ótima corrida", avaliacao.getComentario());

        verify(repoCorrida).atualizar(corrida);
        verify(repoUsuario).atualizar(motorista);
    }

    @Test
    void deveAvaliarPassageiroComSucesso() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");

        Passageiro passageiro = mock(Passageiro.class);
        when(repoUsuario.buscarPorEmail("pass@email.com")).thenReturn(passageiro);

        servico.avaliarPassageiro("1", "motorista@email.com", 4, "Bom passageiro");

        verify(repoAvaliacao).salvar(any(AvaliacaoMotorista.class));
        verify(repoCorrida).atualizar(corrida);
        verify(repoUsuario).atualizar(passageiro);
    }

    @Test
    void devePermitirAvaliacaoMutuaNaMesmaCorrida() {
        Corrida corrida = new Corrida(
                "1",
                "pass@email.com",
                "Origem",
                "Destino",
                null,
                null,
                CategoriaVeiculo.UBERX,
                MetodoPagamento.PIX,
                "motorista@email.com",
                CorridaStatus.CONCLUIDA);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);

        Motorista motorista = new Motorista("motorista@email.com", "hash");
        Passageiro passageiro = new Passageiro("pass@email.com", "hash");
        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);
        when(repoUsuario.buscarPorEmail("pass@email.com")).thenReturn(passageiro);

        List<Avaliacao> avaliacoes = new ArrayList<>();
        doAnswer(invocation -> {
            Avaliacao avaliacao = invocation.getArgument(0);
            avaliacoes.add(avaliacao);
            return null;
        }).when(repoAvaliacao).salvar(any(Avaliacao.class));
        when(repoAvaliacao.buscarPorCorrida("1")).thenAnswer(invocation -> new ArrayList<>(avaliacoes));

        assertDoesNotThrow(() -> servico.avaliarMotorista("1", "pass@email.com", 5, "Muito bom"));
        assertDoesNotThrow(() -> servico.avaliarPassageiro("1", "motorista@email.com", 4, "Pontual"));

        assertTrue(corrida.isAvaliada());
        assertEquals(2, avaliacoes.size());
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
    void naoDevePermitirAvaliarAntesDaCorridaConcluida() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getStatus()).thenReturn(CorridaStatus.EM_ANDAMENTO);
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            servico.avaliarMotorista("1", "pass@email.com", 5, "ok");
        });

        assertEquals("A corrida precisa estar concluída para ser avaliada.", ex.getMessage());
    }

    @Test
    void naoDevePermitirMesmaParteAvaliarDuasVezes() {
        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");

        AvaliacaoPassageiro avaliacaoExistente = new AvaliacaoPassageiro(
                "1",
                "motorista@email.com",
                "pass@email.com",
                4,
                "Boa");
        when(repoAvaliacao.buscarPorCorrida("1")).thenReturn(List.of(avaliacaoExistente));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            servico.avaliarMotorista("1", "pass@email.com", 5, "Excelente");
        });

        assertEquals("O passageiro já avaliou esta corrida.", ex.getMessage());
    }

    @Test
    void naoDevePermitirPassageiroErradoAvaliarMotorista() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);
        when(corrida.getEmailPassageiro()).thenReturn("outro@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");

        assertThrows(IllegalArgumentException.class, () -> {
            servico.avaliarMotorista("1", "pass@email.com", 5, "ok");
        });
    }

    @Test
    void naoDevePermitirMotoristaErradoAvaliarPassageiro() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);
        when(corrida.getMotoristaAlocado()).thenReturn("outro@email.com");
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");

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
    void deveCalcularMediaDoMotoristaAoReceberNovaAvaliacao() {
        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");

        Motorista motorista = new Motorista("motorista@email.com", "hash");
        motorista.adicionarAvaliacao(4);
        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);

        servico.avaliarMotorista("1", "pass@email.com", 5, "Excelente");

        assertEquals(4.5, motorista.getRatingMedio(), 0.01);
        assertEquals(2, motorista.getTotalAvaliacoes());
    }

    @Test
    void devePermitirAvaliarCorrida() {

        Corrida corrida = mock(Corrida.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(corrida);
        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);
        when(corrida.getEmailPassageiro()).thenReturn("pass@email.com");

        boolean resultado = servico.podeAvaliarCorrida("1", "pass@email.com");

        assertTrue(resultado);
    }

    @Test
    void deveListarCorridasParaAvaliar() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getStatus()).thenReturn(CorridaStatus.CONCLUIDA);
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
