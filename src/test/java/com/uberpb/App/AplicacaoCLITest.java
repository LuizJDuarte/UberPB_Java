
package com.uberpb.app;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AplicacaoCLITest {

    private void executarComInput(String input) {

        InputStream original = System.in;

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            assertDoesNotThrow(AplicacaoCLI::executar);
        } finally {
            System.setIn(original);
        }
    }

    @Test
    void sairComQ() {
        executarComInput("q\n");
    }

    @Test
    void sairComTextoSair() {
        executarComInput("sair\n");
    }

    @Test
    void numeroInvalido() {
        executarComInput("999\n\nq\n");
    }

    @Test
    void numeroNegativo() {
        executarComInput("-1\n\nq\n");
    }

    @Test
    void textoInvalido() {
        executarComInput("abc\n\nq\n");
    }

    @Test
    void textoComEspaco() {
        executarComInput("   \n\nq\n");
    }

    @Test
    void abrirAjudaComH() {
        executarComInput("h\n\nq\n");
    }

    @Test
    void abrirAjudaMaiusculo() {
        executarComInput("H\n\nq\n");
    }

    @Test
    void abrirAjudaTexto() {
        executarComInput("ajuda\n\nq\n");
    }

    @Test
    void abrirAjudaMaiusculoTexto() {
        executarComInput("AJUDA\n\nq\n");
    }

    @Test
    void executarPrimeiraOpcao() {
        executarComInput("1\n\n\n\nq\n");
    }

    @Test
    void executarSegundaOpcao() {
        executarComInput("2\n\n\n\nq\n");
    }

    @Test
    void executarTerceiraOpcao() {
        executarComInput("3\n\n\n\nq\n");
    }

    @Test
    void executarVariasOpcoes() {
        executarComInput("1\n\n\n\n2\n\n\n\n3\n\n\n\nq\n");
    }

    @Test
    void variosErrosAntesDeSair() {
        executarComInput("abc\n\n999\n\n-10\n\nh\n\nq\n");
    }
}
