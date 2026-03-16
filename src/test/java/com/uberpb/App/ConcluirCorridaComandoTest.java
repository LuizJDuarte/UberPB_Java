
package com.uberpb.app;

import com.uberpb.exceptions.CorridaAindaEmAndamentoException;
import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConcluirCorridaComandoTest {

    private ContextoAplicacao contexto;
    private Sessao sessao;
    private RepositorioCorrida repositorioCorrida;
    private ServicoCorrida servicoCorrida;
    private GerenciadorCorridasAtivas gerenciador;

    private ConcluirCorridaComando comando;

    @BeforeEach
    void setup() {

        sessao = mock(Sessao.class);
        repositorioCorrida = mock(RepositorioCorrida.class);
        servicoCorrida = mock(ServicoCorrida.class);
        gerenciador = mock(GerenciadorCorridasAtivas.class);

        var repositorioUsuario = mock(RepositorioUsuario.class);
        var repositorioRestaurante = mock(RepositorioRestaurante.class);
        var servicoCadastro = mock(ServicoCadastro.class);
        var servicoAutenticacao = mock(ServicoAutenticacao.class);
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
                gerenciador,
                servicoCarrinho,
                repositorioPedido,
                servicoPedido,
                repositorioNotificacao,
                servicoNotificacao,
                servicoEntrega
        );

        comando = new ConcluirCorridaComando();
    }

    @Test
    void testarNome() {
        assertEquals("Concluir Corrida", comando.nome());
    }

    @Test
    void semCorridas() {

        Usuario usuario = mock(Usuario.class);

        when(sessao.getUsuarioAtual()).thenReturn(usuario);
        when(usuario.getEmail()).thenReturn("cliente@email.com");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void selecionarCorridaValida() throws Exception {

        Usuario usuario = mock(Usuario.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(usuario);
        when(usuario.getEmail()).thenReturn("cliente@email.com");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        when(corrida.getId()).thenReturn("corrida1");
        when(corrida.getOrigemEndereco()).thenReturn("Rua A");
        when(corrida.getDestinoEndereco()).thenReturn("Rua B");

        comando.executar(contexto, new Scanner("1\n"));

        verify(servicoCorrida).concluirCorrida("corrida1", "cliente@email.com", gerenciador);
    }

    @Test
    void excecaoCorridaEmAndamento() throws Exception {

        Usuario usuario = mock(Usuario.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(usuario);
        when(usuario.getEmail()).thenReturn("cliente@email.com");

        when(repositorioCorrida.buscarPorPassageiro("cliente@email.com"))
                .thenReturn(List.of(corrida));

        when(corrida.getId()).thenReturn("corrida1");

        doThrow(new CorridaAindaEmAndamentoException("Ainda em andamento"))
                .when(servicoCorrida)
                .concluirCorrida(anyString(), anyString(), any());

        comando.executar(contexto, new Scanner("1\n"));
    }

}

