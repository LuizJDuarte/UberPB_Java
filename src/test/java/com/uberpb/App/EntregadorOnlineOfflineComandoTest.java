package com.uberpb.app;

import com.uberpb.model.Entregador;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioUsuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EntregadorOnlineOfflineComandoTest {

    private EntregadorOnlineOfflineComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private RepositorioUsuario repositorioUsuario;

    @BeforeEach
    void setup() throws Exception {
        comando = new EntregadorOnlineOfflineComando();

        contexto = new ContextoAplicacao();

        sessao = new Sessao();
        repositorioUsuario = mock(RepositorioUsuario.class);

        setField("sessao", sessao);
        setField("repositorioUsuario", repositorioUsuario);
    }

    private void setField(String nome, Object valor) throws Exception {
        Field field = ContextoAplicacao.class.getDeclaredField(nome);
        field.setAccessible(true);
        field.set(contexto, valor);
    }

    // =========================
    // VISIBILIDADE
    // =========================

    @Test
    void deveSerVisivelParaEntregador() {
        Entregador entregador = mock(Entregador.class);
        assertTrue(comando.visivelPara(entregador));
    }

    @Test
    void naoDeveSerVisivelParaOutroUsuario() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    // =========================
    // USUÁRIO NÃO É ENTREGADOR
    // =========================

    @Test
    void naoDeveExecutarSeNaoForEntregador() {
        Usuario usuario = mock(Usuario.class);
        sessao.logar(usuario);

        Scanner scanner = new Scanner("s\n");

        comando.executar(contexto, scanner);

        verifyNoInteractions(repositorioUsuario);
    }

    // =========================
    // CONTA NÃO ATIVA
    // =========================

    @Test
    void naoDevePermitirSeContaInativa() {
        Entregador entregador = mock(Entregador.class);

        when(entregador.isContaAtiva()).thenReturn(false);

        sessao.logar(entregador);

        Scanner scanner = new Scanner("s\n");

        comando.executar(contexto, scanner);

        verifyNoInteractions(repositorioUsuario);
    }

    // =========================
    // USUÁRIO NÃO QUER ALTERAR
    // =========================

    @Test
    void deveManterStatusSeUsuarioNaoConfirmar() {
        Entregador entregador = mock(Entregador.class);

        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);

        sessao.logar(entregador);

        Scanner scanner = new Scanner("n\n");

        comando.executar(contexto, scanner);

        verify(entregador, never()).setDisponivel(anyBoolean());
        verifyNoInteractions(repositorioUsuario);
    }

    // =========================
    // ALTERAR PARA ONLINE
    // =========================

    @Test
    void deveAlterarParaOnline() {
        Entregador entregador = mock(Entregador.class);

        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(false);

        sessao.logar(entregador);

        Scanner scanner = new Scanner("s\n");

        comando.executar(contexto, scanner);

        verify(entregador).setDisponivel(true);
        verify(repositorioUsuario).atualizar(entregador);
    }

    // =========================
    // ALTERAR PARA OFFLINE
    // =========================

    @Test
    void deveAlterarParaOffline() {
        Entregador entregador = mock(Entregador.class);

        when(entregador.isContaAtiva()).thenReturn(true);
        when(entregador.isDisponivel()).thenReturn(true);

        sessao.logar(entregador);

        Scanner scanner = new Scanner("s\n");

        comando.executar(contexto, scanner);

        verify(entregador).setDisponivel(false);
        verify(repositorioUsuario).atualizar(entregador);
    }
}