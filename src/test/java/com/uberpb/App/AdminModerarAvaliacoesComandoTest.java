package com.uberpb.app;

import com.uberpb.model.Usuario;
import com.uberpb.model.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class AdminModerarAvaliacoesComandoTest {

    private AdminModerarAvaliacoesComando comando;
    private ContextoAplicacao contexto;
    private Scanner scanner;

    @BeforeEach
    void setup() {
        comando = new AdminModerarAvaliacoesComando();

        // Mock de ContextoAplicacao
        contexto = mock(ContextoAplicacao.class);

        // Retorna null para sessão (ou você poderia mockar Sessao se necessário)
        when(contexto.getSessao()).thenReturn(null);

        // Mock de Scanner com entrada vazia por padrão
        scanner = new Scanner("");
    }

    // =========================
    // VISIBILIDADE
    // =========================

    @Test
    void visivelParaAdmin() {
        Usuario admin = mock(Usuario.class);
        when(admin.getTipo()).thenReturn(TipoUsuario.ADMIN);

        assert(comando.visivelPara(admin));
    }

    @Test
    void naoVisivelParaNaoAdmin() {
        Usuario user = mock(Usuario.class);
        when(user.getTipo()).thenReturn(TipoUsuario.PASSAGEIRO_CLIENTE);

        assert(!comando.visivelPara(user));
    }

    // =========================
    // EXECUÇÃO — opções do menu
    // =========================

    @Test
    void deveExecutarOpcaoInvalidaSemErro() {
        Scanner s = new Scanner("9\n"); // opção inválida
        assertDoesNotThrow(() -> comando.executar(contexto, s));
    }

    @Test
    void deveExecutarOpcaoVoltarSemErro() {
        Scanner s = new Scanner("0\n"); // voltar
        assertDoesNotThrow(() -> comando.executar(contexto, s));
    }

    @Test
    void deveExecutarListarAvaliacoesSemErro() {
        Scanner s = new Scanner("1\n"); // listar
        assertDoesNotThrow(() -> comando.executar(contexto, s));
    }

    @Test
    void deveExecutarRemoverAvaliacaoSemErro() {
        Scanner s = new Scanner("2\n123\n"); // remover
        assertDoesNotThrow(() -> comando.executar(contexto, s));
    }
}