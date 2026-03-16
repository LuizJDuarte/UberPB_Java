package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

public class AvaliarCorridaComandoTest {

    private AvaliarCorridaComando comando;

    private ContextoAplicacao contexto;
    private Sessao sessao;
    private ServicoAvaliacao servicoAvaliacao;

    @BeforeEach
    void setup() {

        comando = new AvaliarCorridaComando();

        sessao = mock(Sessao.class);
        servicoAvaliacao = mock(ServicoAvaliacao.class);

        var repositorioUsuario = mock(RepositorioUsuario.class);
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
    void semCorridasParaAvaliar() {

        Passageiro passageiro = mock(Passageiro.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(servicoAvaliacao.getCorridasParaAvaliar("cliente@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));

        verify(servicoAvaliacao).getCorridasParaAvaliar("cliente@email.com");
    }

    @Test
    void opcaoCorridaInvalida() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(corrida.getId()).thenReturn("corrida12345678");
        when(corrida.getEmailPassageiro()).thenReturn("cliente@email.com");

        when(servicoAvaliacao.getCorridasParaAvaliar("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("5\n"));
    }

    @Test
    void entradaNaoNumerica() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(corrida.getId()).thenReturn("corrida12345678");
        when(corrida.getEmailPassageiro()).thenReturn("cliente@email.com");

        when(servicoAvaliacao.getCorridasParaAvaliar("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("abc\n"));
    }

    @Test
    void ratingInvalido() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(corrida.getId()).thenReturn("corrida12345678");
        when(corrida.getEmailPassageiro()).thenReturn("cliente@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");

        when(servicoAvaliacao.getCorridasParaAvaliar("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("1\n10\n"));
    }

    @Test
    void cancelarAvaliacao() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(corrida.getId()).thenReturn("corrida12345678");
        when(corrida.getEmailPassageiro()).thenReturn("cliente@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");

        when(servicoAvaliacao.getCorridasParaAvaliar("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("1\n5\n\nn\n"));
    }

    @Test
    void passageiroAvaliaMotorista() {

        Passageiro passageiro = mock(Passageiro.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("cliente@email.com");

        when(corrida.getId()).thenReturn("corrida12345678");
        when(corrida.getEmailPassageiro()).thenReturn("cliente@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");

        when(servicoAvaliacao.getCorridasParaAvaliar("cliente@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("1\n5\nMuito bom\ns\n"));

        verify(servicoAvaliacao).avaliarMotorista(
                eq("corrida12345678"),
                eq("cliente@email.com"),
                eq(5),
                eq("Muito bom")
        );
    }

    @Test
    void motoristaAvaliaPassageiro() {

        Motorista motorista = mock(Motorista.class);
        Corrida corrida = mock(Corrida.class);

        when(sessao.getUsuarioAtual()).thenReturn(motorista);
        when(motorista.getEmail()).thenReturn("motorista@email.com");

        when(corrida.getId()).thenReturn("corrida12345678");
        when(corrida.getEmailPassageiro()).thenReturn("cliente@email.com");
        when(corrida.getMotoristaAlocado()).thenReturn("motorista@email.com");

        when(servicoAvaliacao.getCorridasParaAvaliar("motorista@email.com"))
                .thenReturn(List.of(corrida));

        comando.executar(contexto, new Scanner("1\n5\nÓtimo passageiro\ns\n"));

        verify(servicoAvaliacao).avaliarPassageiro(
                eq("corrida12345678"),
                eq("motorista@email.com"),
                eq(5),
                eq("Ótimo passageiro")
        );
    }
}