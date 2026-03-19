package com.uberpb.app;

import com.uberpb.model.Motorista;
import com.uberpb.model.TipoUsuario;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;
import com.uberpb.repository.RepositorioUsuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FicarOnlineOfflineComandoTest {

    private FicarOnlineOfflineComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private RepositorioUsuario repositorioUsuario;

    @BeforeEach
    void setup() {
        comando = new FicarOnlineOfflineComando();

        sessao = new Sessao();
        repositorioUsuario = mock(RepositorioUsuario.class);

        contexto = mock(ContextoAplicacao.class);
        when(contexto.getSessao()).thenReturn(sessao);
        when(contexto.getRepositorioUsuario()).thenReturn(repositorioUsuario);
    }

    // =========================
    // VISIBILIDADE
    // =========================

    @Test
    void deveSerVisivelParaMotoristaValido() {
        Motorista motorista = mock(Motorista.class);

        when(motorista.getVeiculo()).thenReturn(mock(Veiculo.class));
        when(motorista.isCnhValida()).thenReturn(true);
        when(motorista.isCrlvValido()).thenReturn(true);
        when(motorista.isContaAtiva()).thenReturn(true);

        assertTrue(comando.visivelPara(motorista));
    }

    @Test
    void naoDeveSerVisivelSemVeiculo() {
        Motorista motorista = mock(Motorista.class);

        when(motorista.getVeiculo()).thenReturn(null);

        assertFalse(comando.visivelPara(motorista));
    }

    @Test
    void naoDeveSerVisivelComCnhInvalida() {
        Motorista motorista = mock(Motorista.class);

        when(motorista.getVeiculo()).thenReturn(mock(Veiculo.class));
        when(motorista.isCnhValida()).thenReturn(false);

        assertFalse(comando.visivelPara(motorista));
    }

    @Test
    void naoDeveSerVisivelComCrlvInvalido() {
        Motorista motorista = mock(Motorista.class);

        when(motorista.getVeiculo()).thenReturn(mock(Veiculo.class));
        when(motorista.isCnhValida()).thenReturn(true);
        when(motorista.isCrlvValido()).thenReturn(false);

        assertFalse(comando.visivelPara(motorista));
    }

    @Test
    void naoDeveSerVisivelComContaInativa() {
        Motorista motorista = mock(Motorista.class);

        when(motorista.getVeiculo()).thenReturn(mock(Veiculo.class));
        when(motorista.isCnhValida()).thenReturn(true);
        when(motorista.isCrlvValido()).thenReturn(true);
        when(motorista.isContaAtiva()).thenReturn(false);

        assertFalse(comando.visivelPara(motorista));
    }

    @Test
    void naoDeveSerVisivelParaUsuarioComum() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    // =========================
    // EXECUÇÃO
    // =========================

    @Test
    void deveFicarOnline() {
        Motorista motorista = mock(Motorista.class);

        when(motorista.isDisponivel()).thenReturn(false);
        when(motorista.getTipo()).thenReturn(TipoUsuario.MOTORISTA); // evita NPE

        sessao.logar(motorista);

        comando.executar(contexto, new Scanner("\n"));

        verify(motorista).setDisponivel(true);
        verify(repositorioUsuario).atualizar(motorista);
    }

    @Test
    void deveFicarOffline() {
        Motorista motorista = mock(Motorista.class);

        when(motorista.isDisponivel()).thenReturn(true);
        when(motorista.getTipo()).thenReturn(TipoUsuario.MOTORISTA); // evita NPE

        sessao.logar(motorista);

        comando.executar(contexto, new Scanner("\n"));

        verify(motorista).setDisponivel(false);
        verify(repositorioUsuario).atualizar(motorista);
    }

    @Test
    void naoDeveQuebrarSemMotoristaLogado() {
        // sessão sem usuário
        assertDoesNotThrow(() ->
                comando.executar(contexto, new Scanner("\n"))
        );
    }

    // =========================
    // NOME DINÂMICO
    // =========================

    @Test
    void deveExibirFicarOfflineQuandoJaOnline() {
        Motorista motorista = mock(Motorista.class);
        when(motorista.isDisponivel()).thenReturn(true);

        String nome = comando.nomeParaExibicao(motorista);

        assertEquals("Ficar Offline", nome);
    }

    @Test
    void deveExibirFicarOnlineQuandoOffline() {
        Motorista motorista = mock(Motorista.class);
        when(motorista.isDisponivel()).thenReturn(false);

        String nome = comando.nomeParaExibicao(motorista);

        assertEquals("Ficar Online", nome);
    }

    @Test
    void deveExibirFicarOnlineParaUsuarioComum() {
        Usuario usuario = mock(Usuario.class);

        String nome = comando.nomeParaExibicao(usuario);

        assertEquals("Ficar Online", nome);
    }
}