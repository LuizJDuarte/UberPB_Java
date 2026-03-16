package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioOferta;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoOfertaTest {

    private RepositorioOferta repoOferta;
    private RepositorioUsuario repoUsuario;
    private RepositorioCorrida repoCorrida;

    private ServicoDirecionamentoCorrida servicoDirecionamento;
    private ServicoLocalizacao servicoLocalizacao;

    private ServicoOferta servicoOferta;

    @BeforeEach
    void setup() {

        repoOferta = mock(RepositorioOferta.class);
        repoUsuario = mock(RepositorioUsuario.class);
        repoCorrida = mock(RepositorioCorrida.class);

        servicoDirecionamento = mock(ServicoDirecionamentoCorrida.class);
        servicoLocalizacao = mock(ServicoLocalizacao.class);

        servicoOferta = new ServicoOferta(
                repoOferta,
                repoUsuario,
                repoCorrida,
                servicoDirecionamento,
                servicoLocalizacao
        );
    }

    // =============================
    // criarOfertasParaCorrida
    // =============================

    @Test
    void deveCriarOferta() {

        Corrida corrida = mock(Corrida.class);
        when(corrida.getId()).thenReturn("corrida1");

        Motorista motorista = mock(Motorista.class);

        when(servicoDirecionamento.filtrarCandidatos(corrida))
                .thenReturn(List.of(motorista));

        when(servicoDirecionamento.escolherMotoristaMaisProximo(
                eq(corrida), anyList(), any()))
                .thenReturn("motorista@email.com");

        int resultado = servicoOferta.criarOfertasParaCorrida(corrida);

        assertEquals(1, resultado);

        verify(repoOferta).salvar(any(OfertaCorrida.class));
    }

    @Test
    void naoDeveCriarOfertaSemMotorista() {

        Corrida corrida = mock(Corrida.class);

        when(servicoDirecionamento.filtrarCandidatos(corrida))
                .thenReturn(List.of());

        int resultado = servicoOferta.criarOfertasParaCorrida(corrida);

        assertEquals(0, resultado);
    }

    @Test
    void naoDeveCriarOfertaSeMotoristaNaoEncontrado() {

        Corrida corrida = mock(Corrida.class);

        Motorista motorista = mock(Motorista.class);

        when(servicoDirecionamento.filtrarCandidatos(corrida))
                .thenReturn(List.of(motorista));

        when(servicoDirecionamento.escolherMotoristaMaisProximo(
                any(), anyList(), any()))
                .thenReturn(null);

        int resultado = servicoOferta.criarOfertasParaCorrida(corrida);

        assertEquals(0, resultado);
    }

    // =============================
    // aceitarOferta
    // =============================

    @Test
    void motoristaAceitaOferta() {

        OfertaCorrida oferta = mock(OfertaCorrida.class);
        Corrida corrida = mock(Corrida.class);

        when(oferta.getMotoristaEmail()).thenReturn("motorista@email.com");
        when(oferta.getStatus()).thenReturn(OfertaStatus.PENDENTE);
        when(oferta.getCorridaId()).thenReturn("corrida1");

        when(repoOferta.buscarPorId("oferta1")).thenReturn(oferta);
        when(repoCorrida.buscarPorId("corrida1")).thenReturn(corrida);
        when(repoOferta.buscarPorCorrida("corrida1")).thenReturn(List.of(oferta));

        servicoOferta.aceitarOferta("oferta1","motorista@email.com");

        verify(repoOferta).atualizar(oferta);
        verify(repoCorrida).atualizar(corrida);
    }

    @Test
    void erroOfertaNaoEncontrada() {

        when(repoOferta.buscarPorId("oferta1")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () ->
                servicoOferta.aceitarOferta("oferta1","motorista@email.com"));
    }

    @Test
    void erroOfertaOutroMotorista() {

        OfertaCorrida oferta = mock(OfertaCorrida.class);

        when(oferta.getMotoristaEmail()).thenReturn("outro@email.com");

        when(repoOferta.buscarPorId("oferta1")).thenReturn(oferta);

        assertThrows(IllegalArgumentException.class, () ->
                servicoOferta.aceitarOferta("oferta1","motorista@email.com"));
    }

    @Test
    void erroOfertaJaRespondida() {

        OfertaCorrida oferta = mock(OfertaCorrida.class);

        when(oferta.getMotoristaEmail()).thenReturn("motorista@email.com");
        when(oferta.getStatus()).thenReturn(OfertaStatus.ACEITA);

        when(repoOferta.buscarPorId("oferta1")).thenReturn(oferta);

        assertThrows(IllegalArgumentException.class, () ->
                servicoOferta.aceitarOferta("oferta1","motorista@email.com"));
    }

    @Test
    void erroCorridaNaoEncontrada() {

        OfertaCorrida oferta = mock(OfertaCorrida.class);

        when(oferta.getMotoristaEmail()).thenReturn("motorista@email.com");
        when(oferta.getStatus()).thenReturn(OfertaStatus.PENDENTE);
        when(oferta.getCorridaId()).thenReturn("corrida1");

        when(repoOferta.buscarPorId("oferta1")).thenReturn(oferta);
        when(repoCorrida.buscarPorId("corrida1")).thenReturn(null);

        assertThrows(IllegalStateException.class, () ->
                servicoOferta.aceitarOferta("oferta1","motorista@email.com"));
    }

    // =============================
    // recusarOferta
    // =============================

    @Test
    void motoristaRecusaOferta() {

        OfertaCorrida oferta = mock(OfertaCorrida.class);
        Corrida corrida = mock(Corrida.class);

        when(oferta.getMotoristaEmail()).thenReturn("motorista@email.com");
        when(oferta.getStatus()).thenReturn(OfertaStatus.PENDENTE);
        when(oferta.getCorridaId()).thenReturn("corrida1");

        when(repoOferta.buscarPorId("oferta1")).thenReturn(oferta);
        when(repoCorrida.buscarPorId("corrida1")).thenReturn(corrida);
        when(corrida.getStatus()).thenReturn(CorridaStatus.SOLICITADA);

        servicoOferta.recusarOferta("oferta1","motorista@email.com");

        verify(repoOferta).atualizar(oferta);
    }

    @Test
    void recusarOfertaCorridaNull() {

        OfertaCorrida oferta = mock(OfertaCorrida.class);

        when(oferta.getMotoristaEmail()).thenReturn("motorista@email.com");
        when(oferta.getStatus()).thenReturn(OfertaStatus.PENDENTE);
        when(oferta.getCorridaId()).thenReturn("corrida1");

        when(repoOferta.buscarPorId("oferta1")).thenReturn(oferta);
        when(repoCorrida.buscarPorId("corrida1")).thenReturn(null);

        servicoOferta.recusarOferta("oferta1","motorista@email.com");

        verify(repoOferta).atualizar(oferta);
    }

    // =============================
    // utilitarios
    // =============================

    @Test
    void listarOfertasMotorista() {

        OfertaCorrida oferta = mock(OfertaCorrida.class);

        when(repoOferta.buscarPorMotorista("motorista@email.com"))
                .thenReturn(List.of(oferta));

        List<OfertaCorrida> lista =
                servicoOferta.listarOfertasDoMotorista("motorista@email.com");

        assertEquals(1, lista.size());
    }

    @Test
    void buscarOfertasPorCorrida() {

        OfertaCorrida oferta = mock(OfertaCorrida.class);

        when(repoOferta.buscarPorCorrida("corrida1"))
                .thenReturn(List.of(oferta));

        List<OfertaCorrida> lista =
                servicoOferta.buscarOfertasPorCorrida("corrida1");

        assertEquals(1, lista.size());
    }

    @Test
    void corridaTemMotoristaTrue() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");
        when(repoCorrida.buscarPorId("corrida1")).thenReturn(corrida);

        assertTrue(servicoOferta.corridaTemMotoristaAlocado("corrida1"));
    }

    @Test
    void corridaTemMotoristaFalse() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getMotoristaAlocado()).thenReturn(null);
        when(repoCorrida.buscarPorId("corrida1")).thenReturn(corrida);

        assertFalse(servicoOferta.corridaTemMotoristaAlocado("corrida1"));
    }

    @Test
    void obterMotoristaAlocado() {

        Corrida corrida = mock(Corrida.class);
        Motorista motorista = mock(Motorista.class);

        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");
        when(repoCorrida.buscarPorId("corrida1")).thenReturn(corrida);

        when(repoUsuario.buscarPorEmail("motorista@email.com"))
                .thenReturn(motorista);

        Optional<Motorista> resultado =
                servicoOferta.obterMotoristaAlocado("corrida1");

        assertTrue(resultado.isPresent());
    }

    @Test
    void obterMotoristaAlocadoVazio() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getMotoristaAlocado()).thenReturn(null);
        when(repoCorrida.buscarPorId("corrida1")).thenReturn(corrida);

        Optional<Motorista> resultado =
                servicoOferta.obterMotoristaAlocado("corrida1");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void cancelarOfertas() {

        OfertaCorrida oferta1 = mock(OfertaCorrida.class);
        OfertaCorrida oferta2 = mock(OfertaCorrida.class);

        when(oferta1.getStatus()).thenReturn(OfertaStatus.PENDENTE);
        when(oferta2.getStatus()).thenReturn(OfertaStatus.PENDENTE);

        when(repoOferta.buscarPorCorrida("corrida1"))
                .thenReturn(List.of(oferta1, oferta2));

        servicoOferta.cancelarOfertasDaCorrida("corrida1");

        verify(repoOferta, times(2)).atualizar(any());
    }

    @Test
    void cancelarOfertasIgnoraNaoPendentes() {

        OfertaCorrida oferta = mock(OfertaCorrida.class);

        when(oferta.getStatus()).thenReturn(OfertaStatus.ACEITA);

        when(repoOferta.buscarPorCorrida("corrida1"))
                .thenReturn(List.of(oferta));

        servicoOferta.cancelarOfertasDaCorrida("corrida1");

        verify(repoOferta, never()).atualizar(oferta);
    }
}
