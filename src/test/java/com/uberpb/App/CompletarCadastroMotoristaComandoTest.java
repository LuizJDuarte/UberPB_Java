package com.uberpb.app;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioUsuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompletarCadastroMotoristaComandoTest {

    private CompletarCadastroMotoristaComando comando;
    private ContextoAplicacao contexto;

    private Sessao sessao;
    private RepositorioUsuario repositorioUsuario;

    @BeforeEach
    void setup() {
        comando = new CompletarCadastroMotoristaComando();

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
    void deveSerVisivelParaMotoristaSemVeiculo() {
        Motorista motorista = mock(Motorista.class);
        when(motorista.getVeiculo()).thenReturn(null);

        assertTrue(comando.visivelPara(motorista));
    }

    @Test
    void naoDeveSerVisivelParaMotoristaComVeiculo() {
        Motorista motorista = mock(Motorista.class);
        when(motorista.getVeiculo()).thenReturn(mock(Veiculo.class));

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
    void deveCadastrarVeiculoComSucesso() {

        Motorista motorista = mock(Motorista.class);
        when(motorista.getVeiculo()).thenReturn(null);
        when(motorista.getTipo()).thenReturn(TipoUsuario.MOTORISTA); // 🔥 FIX

        sessao.logar(motorista);

        Scanner scanner = new Scanner(String.join("\n",
                "Civic",
                "ABC-1234",
                "Preto",
                "2022",
                "5",
                "M"
        ) + "\n");

        comando.executar(contexto, scanner);

        verify(motorista).setVeiculo(any(Veiculo.class));
        verify(repositorioUsuario).salvar(motorista);
    }

    @Test
    void naoDeveQuebrarComEntradaValida() {

        Motorista motorista = mock(Motorista.class);
        when(motorista.getVeiculo()).thenReturn(null);
        when(motorista.getTipo()).thenReturn(TipoUsuario.MOTORISTA); // 🔥 FIX

        sessao.logar(motorista);

        Scanner scanner = new Scanner(String.join("\n",
                "Gol",
                "XYZ-9999",
                "Branco",
                "2018",
                "4",
                "P"
        ) + "\n");

        assertDoesNotThrow(() ->
                comando.executar(contexto, scanner)
        );
    }
}