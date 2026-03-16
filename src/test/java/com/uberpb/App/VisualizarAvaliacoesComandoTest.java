package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class VisualizarAvaliacoesComandoTest {

    private ContextoAplicacao contexto;
    private Sessao sessao;
    private ServicoAvaliacao servicoAvaliacao;

    private VisualizarAvaliacoesComando comando;

    @BeforeEach
    void setup() {

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

        comando = new VisualizarAvaliacoesComando();
    }

    @Test
    void testarNome() {

        assertEquals("Visualizar Minhas Avaliações", comando.nome());
    }

    @Test
    void visivelParaPassageiro() {

        Passageiro p = mock(Passageiro.class);

        assertTrue(comando.visivelPara(p));
    }

    @Test
    void visivelParaMotorista() {

        Motorista m = mock(Motorista.class);

        assertTrue(comando.visivelPara(m));
    }

    @Test
    void naoVisivelParaOutroUsuario() {

        Usuario usuario = mock(Usuario.class);

        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void passageiroSemAvaliacoes() {

        Passageiro passageiro = mock(Passageiro.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);
        when(passageiro.getEmail()).thenReturn("p@email.com");
        when(passageiro.getRatingMedio()).thenReturn(0.0);
        when(passageiro.getTotalAvaliacoes()).thenReturn(0);

        when(servicoAvaliacao.getAvaliacoesPassageiro("p@email.com"))
                .thenReturn(List.of());

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void passageiroComAvaliacoes() {

        Passageiro passageiro = mock(Passageiro.class);
        Avaliacao a1 = mock(Avaliacao.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);

        when(passageiro.getEmail()).thenReturn("p@email.com");
        when(passageiro.getRatingMedio()).thenReturn(4.2);
        when(passageiro.getTotalAvaliacoes()).thenReturn(1);

        when(a1.getRating()).thenReturn(5);
        when(a1.getComentario()).thenReturn("Muito bom");
        when(a1.getCorridaId()).thenReturn("corrida12345678");
        when(a1.getDataAvaliacao()).thenReturn(LocalDateTime.now());

        when(servicoAvaliacao.getAvaliacoesPassageiro("p@email.com"))
                .thenReturn(List.of(a1));

        comando.executar(contexto, new Scanner("n\n"));
    }

    @Test
    void motoristaComAvaliacoes() {

        Motorista motorista = mock(Motorista.class);
        AvaliacaoPassageiro a1 = mock(AvaliacaoPassageiro.class);

        when(sessao.getUsuarioAtual()).thenReturn(motorista);

        when(motorista.getEmail()).thenReturn("m@email.com");
        when(motorista.getRatingMedio()).thenReturn(4.9);
        when(motorista.getTotalAvaliacoes()).thenReturn(10);

        when(a1.getRating()).thenReturn(5);
        when(a1.getComentario()).thenReturn("Excelente");
        when(a1.getPassageiroEmail()).thenReturn("cliente@email.com");
        when(a1.getCorridaId()).thenReturn("corrida987654321");
        when(a1.getDataAvaliacao()).thenReturn(LocalDateTime.now());

        when(servicoAvaliacao.getAvaliacoesMotorista("m@email.com"))
                .thenReturn(List.of(a1));

        comando.executar(contexto, new Scanner("n\n"));
    }

    @Test
    void motoristaVisualizaAvaliacoesFeitas() {

        Motorista motorista = mock(Motorista.class);
        AvaliacaoMotorista avaliacao = mock(AvaliacaoMotorista.class);

        when(sessao.getUsuarioAtual()).thenReturn(motorista);

        when(motorista.getEmail()).thenReturn("motorista@email.com");
        when(motorista.getRatingMedio()).thenReturn(4.5);
        when(motorista.getTotalAvaliacoes()).thenReturn(2);

        when(avaliacao.getRating()).thenReturn(5);
        when(avaliacao.getComentario()).thenReturn("Ótimo passageiro");
        when(avaliacao.getPassageiroEmail()).thenReturn("cliente@email.com");
        when(avaliacao.getCorridaId()).thenReturn("corrida12345678");
        when(avaliacao.getDataAvaliacao()).thenReturn(LocalDateTime.now());

        when(servicoAvaliacao.getAvaliacoesMotorista("motorista@email.com"))
                .thenReturn(List.of(avaliacao));

        comando.executar(contexto, new Scanner("n\n"));
    }

    @Test
    void avaliacaoSemComentario() {

        Passageiro passageiro = mock(Passageiro.class);
        Avaliacao avaliacao = mock(Avaliacao.class);

        when(sessao.getUsuarioAtual()).thenReturn(passageiro);

        when(passageiro.getEmail()).thenReturn("p@email.com");
        when(passageiro.getRatingMedio()).thenReturn(3.0);
        when(passageiro.getTotalAvaliacoes()).thenReturn(1);

        when(avaliacao.getRating()).thenReturn(3);
        when(avaliacao.getComentario()).thenReturn("Sem comentário");
        when(avaliacao.getCorridaId()).thenReturn("corrida99999999");
        when(avaliacao.getDataAvaliacao()).thenReturn(LocalDateTime.now());

        when(servicoAvaliacao.getAvaliacoesPassageiro("p@email.com"))
                .thenReturn(List.of(avaliacao));

        comando.executar(contexto, new Scanner("n\n"));
    }

    @Test
void exibirEstatisticasDetalhadas() {

    Passageiro passageiro = mock(Passageiro.class);

    Avaliacao a1 = mock(Avaliacao.class);
    Avaliacao a2 = mock(Avaliacao.class);
    Avaliacao a3 = mock(Avaliacao.class);

    when(sessao.getUsuarioAtual()).thenReturn(passageiro);

    when(passageiro.getEmail()).thenReturn("p@email.com");
    when(passageiro.getRatingMedio()).thenReturn(4.0);
    when(passageiro.getTotalAvaliacoes()).thenReturn(3);

    when(a1.getRating()).thenReturn(5);
    when(a1.getComentario()).thenReturn("Excelente");
    when(a1.getCorridaId()).thenReturn("corrida11111111");
    when(a1.getDataAvaliacao()).thenReturn(LocalDateTime.now());

    when(a2.getRating()).thenReturn(4);
    when(a2.getComentario()).thenReturn("Bom");
    when(a2.getCorridaId()).thenReturn("corrida22222222");
    when(a2.getDataAvaliacao()).thenReturn(LocalDateTime.now());

    when(a3.getRating()).thenReturn(3);
    when(a3.getComentario()).thenReturn("Ok");
    when(a3.getCorridaId()).thenReturn("corrida33333333");
    when(a3.getDataAvaliacao()).thenReturn(LocalDateTime.now());

    when(servicoAvaliacao.getAvaliacoesPassageiro("p@email.com"))
            .thenReturn(List.of(a1, a2, a3));

    // "s" ativa exibirEstatisticasDetalhadas()
    comando.executar(contexto, new Scanner("s\n"));
}
}
