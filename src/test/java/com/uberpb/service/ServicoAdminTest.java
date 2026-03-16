package com.uberpb.service;

import com.uberpb.exceptions.OperacaoNaoPermitidaException;
import com.uberpb.model.Administrador;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoAdminTest {

    private RepositorioUsuario repoUsuario;
    private RepositorioCorrida repoCorrida;
    private ServicoAdmin servico;

    @BeforeEach
    void setUp() {
        repoUsuario = mock(RepositorioUsuario.class);
        repoCorrida = mock(RepositorioCorrida.class);
        servico = new ServicoAdmin(repoUsuario, repoCorrida);
    }

    @Test
    void deveIdentificarAdministrador() {

        Usuario admin = mock(Administrador.class);

        boolean resultado = servico.ehAdmin(admin);

        assertTrue(resultado);
    }

    @Test
    void naoDeveIdentificarUsuarioComumComoAdmin() {

        Usuario usuario = mock(Usuario.class);

        boolean resultado = servico.ehAdmin(usuario);

        assertFalse(resultado);
    }

    @Test
    void deveListarUsuarios() {

        List<Usuario> usuarios = List.of(mock(Usuario.class), mock(Usuario.class));

        when(repoUsuario.buscarTodos()).thenReturn(usuarios);

        List<Usuario> resultado = servico.listarUsuarios();

        assertEquals(2, resultado.size());
        verify(repoUsuario).buscarTodos();
    }

    @Test
    void adminDeveRemoverUsuario() {

        Usuario admin = mock(Administrador.class);

        servico.removerUsuario("user@email.com", admin);

        verify(repoUsuario).remover("user@email.com");
    }

    @Test
    void usuarioComumNaoPodeRemoverUsuario() {

        Usuario usuario = mock(Usuario.class);

        assertThrows(OperacaoNaoPermitidaException.class, () -> {
            servico.removerUsuario("user@email.com", usuario);
        });
    }

    @Test
    void adminDeveRemoverCorridaExistente() {

        Usuario admin = mock(Administrador.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(mock());

        servico.removerCorrida("1", admin);

        verify(repoCorrida).remover("1");
    }

    @Test
    void naoRemoveCorridaSeNaoExistir() {

        Usuario admin = mock(Administrador.class);

        when(repoCorrida.buscarPorId("1")).thenReturn(null);

        servico.removerCorrida("1", admin);

        verify(repoCorrida, never()).remover("1");
    }

    @Test
    void usuarioComumNaoPodeRemoverCorrida() {

        Usuario usuario = mock(Usuario.class);

        assertThrows(OperacaoNaoPermitidaException.class, () -> {
            servico.removerCorrida("1", usuario);
        });
    }

    @Test
    void deveLimparDados() {

        servico.limparDados();

        verify(repoUsuario).limpar();
        verify(repoCorrida).limpar();
    }
}
