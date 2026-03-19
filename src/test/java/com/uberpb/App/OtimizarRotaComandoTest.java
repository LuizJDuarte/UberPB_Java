package com.uberpb.app;

import com.uberpb.model.Localizacao;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.service.RotaOtimizada;
import com.uberpb.service.ServicoOtimizacaoRota;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OtimizarRotaComandoTest {

    private OtimizarRotaComando comando;
    private ContextoAplicacao contexto;
    private ServicoOtimizacaoRota servicoMock;

    @BeforeEach
    void setup() {
        comando = new OtimizarRotaComando();

        servicoMock = mock(ServicoOtimizacaoRota.class);

        // ✅ USANDO CONSTRUTOR COMPLETO (única forma por causa do final)
        contexto = new ContextoAplicacao(
                null, // sessao
                null, // repositorioUsuario
                null, // repositorioRestaurante
                null, // servicoCadastro
                null, // servicoAutenticacao
                null, // repositorioCorrida
                null, // servicoCorrida
                null, // repositorioOferta
                null, // repositorioAvaliacao
                null, // servicoOferta
                null, // servicoValidacaoMotorista
                null, // servicoPagamento
                null, // servicoAvaliacao
                servicoMock, // ✅ AQUI
                null, // servicoLocalizacao
                null, // servicoDirecionamento
                null, // estimativaChegada
                null, // servicoAdmin
                null, // gerenciadorCorridas
                null, // servicoCarrinho
                null, // repositorioPedido
                null, // servicoPedido
                null, // repositorioNotificacao
                null, // servicoNotificacao
                null  // servicoEntrega
        );
    }

    @Test
    void deveRetornarNomeCorreto() {
        assertEquals("Otimizar Rota", comando.nome());
    }

    @Test
    void deveSerVisivelParaPassageiro() {
        Usuario passageiro = mock(Passageiro.class);
        assertTrue(comando.visivelPara(passageiro));
    }

    @Test
    void naoDeveSerVisivelParaUsuarioNaoPassageiro() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    @Test
    void deveExecutarERetornarDadosDaRota() {

        String input = String.join("\n",
                "10.0",
                "20.0",
                "30.0",
                "40.0"
        );

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // ✅ LISTA CORRETA (Localizacao)
        List<Localizacao> pontos = List.of(
                new Localizacao(1, 1),
                new Localizacao(2, 2)
        );

        RotaOtimizada rotaMock = mock(RotaOtimizada.class);
        when(rotaMock.getDistanciaKm()).thenReturn(15.5);
        when(rotaMock.getTempoEstimadoMinutos()).thenReturn(25.0);
        when(rotaMock.getEconomiaTempoPercentual()).thenReturn(10.0);
        when(rotaMock.getPontosRota()).thenReturn(pontos);

        when(servicoMock.calcularRotaOtimizada(any(), any()))
                .thenReturn(rotaMock);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        comando.executar(contexto, scanner);

        String output = out.toString();

        assertTrue(output.contains("OTIMIZAÇÃO DE ROTA"));
        assertTrue(output.contains("ROTA OTIMIZADA"));
        assertTrue(output.contains("Distância"));
        assertTrue(output.contains("Tempo estimado"));
        assertTrue(output.contains("Economia de tempo"));
        assertTrue(output.contains("Pontos da rota: 2"));

        verify(servicoMock, times(1))
                .calcularRotaOtimizada(any(), any());
    }
}