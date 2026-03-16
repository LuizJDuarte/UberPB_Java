
package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EstimativaCorridaComandoTest {

    private ContextoAplicacao contexto;
    private Sessao sessao;

    private EstimativaCorridaComando comando;

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

        comando = new EstimativaCorridaComando();
    }

    @Test
    void testarNome() {
        assertEquals("Ver Estimativa de Corrida", comando.nome());
    }

    @Test
    void visivelParaPassageiro() {
        Passageiro passageiro = mock(Passageiro.class);
        assertTrue(comando.visivelPara(passageiro));
    }

    @Test
    void naoVisivelParaOutroUsuario() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void origemOuDestinoVazio() {

        String entrada = "\nDestino\n";

        comando.executar(contexto, new Scanner(entrada));
    }

    @Test
    void origemIgualDestino() {

        String entrada = "Rua A\nRua A\n";

        comando.executar(contexto, new Scanner(entrada));
    }

    @Test
    void fluxoNormalEstimativa() {

        String entrada = "Rua A\nRua B\n";

        comando.executar(contexto, new Scanner(entrada));
    }
}
