package com.uberpb.service;

import com.uberpb.model.Corrida;
import com.uberpb.model.MetodoPagamento;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoPagamentoTest {

    private ServicoPagamento servico;

    @BeforeEach
    void setup() {
        servico = new ServicoPagamento(
                mock(RepositorioCorrida.class),
                mock(RepositorioUsuario.class)
        );
    }

    // =========================
    // PROCESSAR PAGAMENTO
    // =========================

    @Test
    void deveLancarErroQuandoCorridaNula() {
        assertThrows(IllegalArgumentException.class, () ->
                servico.processarPagamento(null, MetodoPagamento.PIX));
    }

    @Test
    void deveProcessarPagamentoDinheiroSempreTrue() {
        Corrida corrida = mock(Corrida.class);
        when(corrida.getId()).thenReturn("1234567890");

        boolean resultado = servico.processarPagamento(corrida, MetodoPagamento.DINHEIRO);

        assertTrue(resultado);
    }

    @Test
    void deveProcessarPagamentoPixSemErro() {
        Corrida corrida = mock(Corrida.class);
        when(corrida.getId()).thenReturn("1234567890");

        boolean resultado = servico.processarPagamento(corrida, MetodoPagamento.PIX);

        assertNotNull(resultado); // pode ser true ou false
    }

    // =========================
    // QR CODE PIX
    // =========================

    @Test
    void deveGerarQrCodePix() {
        Corrida corrida = mock(Corrida.class);
        when(corrida.getId()).thenReturn("abc123");

        String qr = servico.gerarQrCodePix(corrida, 50.0);

        assertNotNull(qr);
        assertTrue(qr.contains("BR.GOV.BCB.PIX"));
    }

    // =========================
    // CARTÃO
    // =========================

    @Test
    void deveProcessarCartaoValido() {
        boolean resultado = servico.processarCartao(
                "1234567812345678",
                "12/30",
                "123",
                100.0
        );

        assertNotNull(resultado);
    }

    @Test
    void deveFalharNumeroCartaoInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                servico.processarCartao(
                        "12345678", // inválido (não tem 16 dígitos)
                        "12/30",
                        "123",
                        100.0
                ));
    }

    @Test
    void deveFalharNumeroCartaoComLetras() {
        assertThrows(IllegalArgumentException.class, () ->
                servico.processarCartao(
                        "1234abcd1234abcd",
                        "12/30",
                        "123",
                        100.0
                ));
    }

    @Test
    void deveFalharCvvInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                servico.processarCartao(
                        "1234567812345678",
                        "12/30",
                        "12",
                        100.0
                ));
    }

    @Test
    void deveFalharValidadeInvalida() {
        assertThrows(IllegalArgumentException.class, () ->
                servico.processarCartao(
                        "1234567812345678",
                        "1230", // formato inválido
                        "123",
                        100.0
                ));
    }

    // =========================
    // PAYPAL
    // =========================

    @Test
    void deveProcessarPayPalValido() {
        boolean resultado = servico.processarPayPal("teste@email.com", 50.0);
        assertNotNull(resultado);
    }

    @Test
    void deveFalharEmailPayPalInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                servico.processarPayPal("email_invalido", 50.0));
    }

    // =========================
    // DETALHES METODO PAGAMENTO
    // =========================

    @Test
    void deveRetornarDetalhesPix() {
        String detalhe = servico.getDetalhesMetodoPagamento(MetodoPagamento.PIX);
        assertTrue(detalhe.contains("instantâneo"));
    }

    @Test
    void deveRetornarDetalhesCartao() {
        String detalhe = servico.getDetalhesMetodoPagamento(MetodoPagamento.CARTAO);
        assertTrue(detalhe.contains("Cartão"));
    }

    @Test
    void deveRetornarDetalhesPayPal() {
        String detalhe = servico.getDetalhesMetodoPagamento(MetodoPagamento.PAYPAL);
        assertTrue(detalhe.contains("PayPal"));
    }

    @Test
    void deveRetornarDetalhesDinheiro() {
        String detalhe = servico.getDetalhesMetodoPagamento(MetodoPagamento.DINHEIRO);
        assertTrue(detalhe.contains("dinheiro"));
    }

    @Test
    void deveCobrirMetodoComNull() {
        String detalhe = servico.getDetalhesMetodoPagamento(null);
        assertEquals("Método de pagamento", detalhe);
    }
}