package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VerificarStatusAprovacaoComandoTest {

    private ContextoAplicacao contexto;
    private Sessao sessao;

    private VerificarStatusAprovacaoComando comando;

    @BeforeEach
    void setup() {

        sessao = mock(Sessao.class);

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
        var servicoAvaliacao = mock(ServicoAvaliacao.class);
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

        comando = new VerificarStatusAprovacaoComando();
    }

    @Test
    void testarNome() {
        assertEquals("Verificar Status de Aprovação", comando.nome());
    }

    @Test
    void naoVisivelParaUsuarioComum() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void motoristaSemVeiculoNaoVeComando() {

        Motorista motorista = mock(Motorista.class);

        when(motorista.getVeiculo()).thenReturn(null);

        assertFalse(comando.visivelPara(motorista));
    }

    @Test
    void motoristaTotalmenteAprovadoNaoVeComando() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(motorista.getVeiculo()).thenReturn(veiculo);
        when(motorista.isCnhValida()).thenReturn(true);
        when(motorista.isCrlvValido()).thenReturn(true);
        when(motorista.isContaAtiva()).thenReturn(true);

        assertFalse(comando.visivelPara(motorista));
    }

    @Test
    void motoristaPendenteVeComando() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(motorista.getVeiculo()).thenReturn(veiculo);
        when(motorista.isCnhValida()).thenReturn(false);
        when(motorista.isCrlvValido()).thenReturn(true);
        when(motorista.isContaAtiva()).thenReturn(true);

        assertTrue(comando.visivelPara(motorista));
    }

    @Test
    void executarSemMotoristaLogado() {

        when(sessao.getUsuarioAtual()).thenReturn(null);

        comando.executar(contexto, new Scanner("\n"));
    }

    @Test
    void executarComMotoristaLogado() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(sessao.getUsuarioAtual()).thenReturn(motorista);

        when(motorista.getEmail()).thenReturn("motorista@email.com");
        when(motorista.getVeiculo()).thenReturn(veiculo);
        when(motorista.isCnhValida()).thenReturn(false);
        when(motorista.isCrlvValido()).thenReturn(true);
        when(motorista.isContaAtiva()).thenReturn(false);

        comando.executar(contexto, new Scanner("\n"));
    }
}
