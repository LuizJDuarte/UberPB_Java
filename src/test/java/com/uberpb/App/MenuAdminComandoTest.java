package com.uberpb.app;

import com.uberpb.model.TipoUsuario;
import com.uberpb.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuAdminComandoTest {

    private MenuAdminComando menu;
    private ContextoAplicacao contexto;
    private Sessao sessao;
    private Usuario admin;

    @BeforeEach
    void setup() {
        menu = new MenuAdminComando();

        contexto = mock(ContextoAplicacao.class);
        sessao = new Sessao();

        admin = mock(Usuario.class);
        when(admin.getTipo()).thenReturn(TipoUsuario.ADMIN);

        sessao.logar(admin);
        when(contexto.getSessao()).thenReturn(sessao);
    }

    // =========================
    // VISIBILIDADE
    // =========================

    @Test
    void deveSerVisivelParaAdmin() {
        assertTrue(menu.visivelPara(admin));
    }

    @Test
    void naoDeveSerVisivelParaUsuarioComum() {
        Usuario user = mock(Usuario.class);
        when(user.getTipo()).thenReturn(TipoUsuario.PASSAGEIRO_CLIENTE);

        assertFalse(menu.visivelPara(user));
    }

    @Test
    void naoDeveSerVisivelParaUsuarioNulo() {
        assertFalse(menu.visivelPara(null));
    }

    // =========================
    // EXECUÇÃO
    // =========================

    @Test
    void deveSairDoMenuQuandoOpcaoZero() {
        Scanner scanner = new Scanner("0\n");

        assertDoesNotThrow(() ->
                menu.executar(contexto, scanner)
        );
    }

    @Test
    void deveTratarOpcaoInvalidaNumeroForaDoRange() {
        Scanner scanner = new Scanner("999\n0\n");

        assertDoesNotThrow(() ->
                menu.executar(contexto, scanner)
        );
    }

    @Test
    void deveTratarEntradaNaoNumerica() {
        Scanner scanner = new Scanner("abc\n0\n");

        assertDoesNotThrow(() ->
                menu.executar(contexto, scanner)
        );
    }

    @Test
    void deveExecutarFluxoComMultiplasEntradas() {
        Scanner scanner = new Scanner("1\n0\n");

        assertDoesNotThrow(() ->
                menu.executar(contexto, scanner)
        );
    }

    // =========================
    // PROTEÇÃO DE SESSÃO
    // =========================

    @Test
    void deveNaoExecutarSemUsuarioLogado() {
        Sessao sessaoVazia = new Sessao();
        when(contexto.getSessao()).thenReturn(sessaoVazia);

        Scanner scanner = new Scanner("0\n");

        assertDoesNotThrow(() ->
                menu.executar(contexto, scanner)
        );
    }

    @Test
    void deveNaoExecutarComSessaoNula() {
        when(contexto.getSessao()).thenReturn(null);

        Scanner scanner = new Scanner("0\n");

        assertDoesNotThrow(() ->
                menu.executar(contexto, scanner)
        );
    }
}