package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoDirecionamentoCorridaTest {

    private RepositorioUsuario repoUsuario;
    private RepositorioCorrida repoCorrida;
    private ServicoDirecionamentoCorrida servico;

    @BeforeEach
    void setup() {
        repoUsuario = mock(RepositorioUsuario.class);
        repoCorrida = mock(RepositorioCorrida.class);
        servico = new ServicoDirecionamentoCorrida(repoUsuario, repoCorrida);
    }

    // =============================
    // FILTRAR CANDIDATOS
    // =============================

    @Test
    void deveFiltrarMotoristasAtivos() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);
        Corrida corrida = mock(Corrida.class);

        when(motorista.isContaAtiva()).thenReturn(true);
        when(motorista.getVeiculo()).thenReturn(veiculo);

        when(veiculo.getCategoriasDisponiveis()).thenReturn(List.of(CategoriaVeiculo.UBERX));
        when(corrida.getCategoriaEscolhida()).thenReturn(CategoriaVeiculo.UBERX);

        when(repoUsuario.buscarTodos()).thenReturn(List.of(motorista));

        List<Motorista> resultado = servico.filtrarCandidatos(corrida);

        assertEquals(1, resultado.size());
    }

    @Test
    void naoDeveRetornarMotoristaInativo() {

        Motorista motorista = mock(Motorista.class);
        Corrida corrida = mock(Corrida.class);

        when(motorista.isContaAtiva()).thenReturn(false);

        when(repoUsuario.buscarTodos()).thenReturn(List.of(motorista));

        List<Motorista> resultado = servico.filtrarCandidatos(corrida);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void naoDeveRetornarMotoristaSemVeiculo() {

        Motorista motorista = mock(Motorista.class);
        Corrida corrida = mock(Corrida.class);

        when(motorista.isContaAtiva()).thenReturn(true);
        when(motorista.getVeiculo()).thenReturn(null);

        when(repoUsuario.buscarTodos()).thenReturn(List.of(motorista));

        List<Motorista> resultado = servico.filtrarCandidatos(corrida);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveAceitarQualquerCategoriaSeCorridaNull() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);
        Corrida corrida = mock(Corrida.class);

        when(motorista.isContaAtiva()).thenReturn(true);
        when(motorista.getVeiculo()).thenReturn(veiculo);

        when(veiculo.getCategoriasDisponiveis()).thenReturn(List.of(CategoriaVeiculo.UBERX));
        when(corrida.getCategoriaEscolhida()).thenReturn(null);

        when(repoUsuario.buscarTodos()).thenReturn(List.of(motorista));

        List<Motorista> resultado = servico.filtrarCandidatos(corrida);

        assertEquals(1, resultado.size());
    }

    @Test
    void naoDeveRetornarCategoriaIncompativel() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);
        Corrida corrida = mock(Corrida.class);

        when(motorista.isContaAtiva()).thenReturn(true);
        when(motorista.getVeiculo()).thenReturn(veiculo);

        when(veiculo.getCategoriasDisponiveis()).thenReturn(List.of(CategoriaVeiculo.BLACK));
        when(corrida.getCategoriaEscolhida()).thenReturn(CategoriaVeiculo.UBERX);

        when(repoUsuario.buscarTodos()).thenReturn(List.of(motorista));

        List<Motorista> resultado = servico.filtrarCandidatos(corrida);

        assertTrue(resultado.isEmpty());
    }

    // =============================
    // MOTORISTA MAIS PRÓXIMO
    // =============================

    @Test
    void deveEscolherMotoristaMaisProximo() {

        ServicoLocalizacao sl = mock(ServicoLocalizacao.class);

        Corrida corrida = mock(Corrida.class);
        Motorista m1 = mock(Motorista.class);
        Motorista m2 = mock(Motorista.class);

        when(m1.getEmail()).thenReturn("m1@email.com");
        when(m2.getEmail()).thenReturn("m2@email.com");

        when(corrida.getOrigem()).thenReturn(null);
        when(corrida.getOrigemEndereco()).thenReturn("Centro");

        when(sl.geocodificar(anyString())).thenReturn(null);

        when(sl.obterLocalizacaoAtual("m1@email.com")).thenReturn(null);
        when(sl.obterLocalizacaoAtual("m2@email.com")).thenReturn(null);

        when(sl.distanciaKm(null, null)).thenReturn(10.0, 3.0);

        String resultado = servico.escolherMotoristaMaisProximo(
                corrida,
                List.of(m1, m2),
                sl
        );

        assertEquals("m2@email.com", resultado);
    }

    @Test
    void deveRetornarNullSemCandidatos() {

        ServicoLocalizacao sl = mock(ServicoLocalizacao.class);
        Corrida corrida = mock(Corrida.class);

        String resultado = servico.escolherMotoristaMaisProximo(
                corrida,
                List.of(),
                sl
        );

        assertNull(resultado);
    }
}