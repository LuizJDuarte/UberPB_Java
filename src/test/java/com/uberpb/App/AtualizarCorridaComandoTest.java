package com.uberpb.app;

import com.uberpb.model.*;

import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.service.ServicoOferta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtualizarCorridaComandoTest {

    private AtualizarCorridaComando comando;
    private ContextoAplicacao contexto;

    private RepositorioCorrida repositorioCorrida;
    private ServicoOferta servicoOferta;
    private Sessao sessao;

    @BeforeEach
    void setup() throws Exception {
        comando = new AtualizarCorridaComando();

        contexto = new ContextoAplicacao();

        repositorioCorrida = mock(RepositorioCorrida.class);
        servicoOferta = mock(ServicoOferta.class);
        sessao = new Sessao();

        Usuario usuario = mock(Usuario.class);
        when(usuario.getEmail()).thenReturn("teste@email.com");
        sessao.logar(usuario);

        setField("repositorioCorrida", repositorioCorrida);
        setField("servicoOferta", servicoOferta);
        setField("sessao", sessao);
    }

    private void setField(String nome, Object valor) throws Exception {
        Field field = ContextoAplicacao.class.getDeclaredField(nome);
        field.setAccessible(true);
        field.set(contexto, valor);
    }

    // =========================
    // VISIBILIDADE
    // =========================

    @Test
    void deveSerVisivelQuandoUsuarioLogado() {
        assertTrue(comando.visivelPara(mock(Usuario.class)));
    }

    @Test
    void naoDeveSerVisivelQuandoUsuarioNull() {
        assertFalse(comando.visivelPara(null));
    }

    // =========================
    // SEM CORRIDA
    // =========================

    @Test
    void deveInformarQuandoNaoHaCorrida() {

        when(repositorioCorrida.buscarPorPassageiro(anyString()))
                .thenReturn(List.of());

        Scanner scanner = new Scanner("\n");

        comando.executar(contexto, scanner);

        verify(servicoOferta, never()).criarOfertasParaCorrida(any());
    }

    // =========================
    // ATUALIZAÇÃO COMPLETA
    // =========================

    @Test
    void deveAtualizarCorridaComNovosDados() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getStatus()).thenReturn(CorridaStatus.SOLICITADA);
        when(corrida.getId()).thenReturn("1");
        when(corrida.getEmailPassageiro()).thenReturn("teste@email.com");
        when(corrida.getOrigemEndereco()).thenReturn("Origem Antiga");
        when(corrida.getDestinoEndereco()).thenReturn("Destino Antigo");
        when(corrida.getCategoriaEscolhida()).thenReturn(CategoriaVeiculo.UBERX);
        when(corrida.getOrigem()).thenReturn(null);
        when(corrida.getDestino()).thenReturn(null);
        when(corrida.getMetodoPagamento()).thenReturn(null);
        when(corrida.getMotoristaAlocado()).thenReturn(null);

        when(repositorioCorrida.buscarPorPassageiro(anyString()))
                .thenReturn(List.of(corrida));

        Scanner scanner = new Scanner(String.join("\n",
                "Nova Origem",
                "Novo Destino",
                "UberX"
        ) + "\n");

        comando.executar(contexto, scanner);

        verify(repositorioCorrida).atualizar(any(Corrida.class));
        verify(servicoOferta).criarOfertasParaCorrida(corrida);
    }

    // =========================
    // MANTER VALORES ANTIGOS
    // =========================

    @Test
    void deveManterValoresQuandoEntradaVazia() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getStatus()).thenReturn(CorridaStatus.SOLICITADA);
        when(corrida.getId()).thenReturn("1");
        when(corrida.getEmailPassageiro()).thenReturn("teste@email.com");
        when(corrida.getOrigemEndereco()).thenReturn("Origem Antiga");
        when(corrida.getDestinoEndereco()).thenReturn("Destino Antigo");
        when(corrida.getCategoriaEscolhida()).thenReturn(CategoriaVeiculo.BLACK);
        when(corrida.getOrigem()).thenReturn(null);
        when(corrida.getDestino()).thenReturn(null);
        when(corrida.getMetodoPagamento()).thenReturn(null);
        when(corrida.getMotoristaAlocado()).thenReturn(null);

        when(repositorioCorrida.buscarPorPassageiro(anyString()))
                .thenReturn(List.of(corrida));

        Scanner scanner = new Scanner("\n\n\n");

        comando.executar(contexto, scanner);

        verify(repositorioCorrida).atualizar(any(Corrida.class));
    }

    // =========================
    // CATEGORIA INVÁLIDA
    // =========================

    @Test
    void devePermitirCategoriaInvalidaSemQuebrar() {

        Corrida corrida = mock(Corrida.class);

        when(corrida.getStatus()).thenReturn(CorridaStatus.SOLICITADA);
        when(corrida.getId()).thenReturn("1");
        when(corrida.getEmailPassageiro()).thenReturn("teste@email.com");
        when(corrida.getOrigemEndereco()).thenReturn("Origem");
        when(corrida.getDestinoEndereco()).thenReturn("Destino");
        when(corrida.getCategoriaEscolhida()).thenReturn(CategoriaVeiculo.UBERX);
        when(corrida.getOrigem()).thenReturn(null);
        when(corrida.getDestino()).thenReturn(null);
        when(corrida.getMetodoPagamento()).thenReturn(null);
        when(corrida.getMotoristaAlocado()).thenReturn(null);

        when(repositorioCorrida.buscarPorPassageiro(anyString()))
                .thenReturn(List.of(corrida));

        Scanner scanner = new Scanner(String.join("\n",
                "",
                "",
                "INVALIDA"
        ) + "\n");

        comando.executar(contexto, scanner);

        verify(repositorioCorrida).atualizar(any(Corrida.class));
    }
}