package com.uberpb.service;

import com.uberpb.exceptions.CredenciaisInvalidasException;
import com.uberpb.exceptions.UsuarioNaoEncontradoException;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoAutenticacaoTest {

    private ServicoAutenticacao servicoAutenticacao;
    private RepositorioUsuario repositorioUsuario;

    // Reusing the in-memory repository implementation for clean tests
    static class InMemoryRepositorioUsuario implements RepositorioUsuario {
        private final Map<String, Usuario> database = new HashMap<>();

        @Override
        public void salvar(Usuario usuario) {
            database.put(usuario.getEmail(), usuario);
        }

        @Override
        public void atualizar(Usuario usuario) {
            database.put(usuario.getEmail(), usuario);
        }

        @Override
        public Usuario buscarPorEmail(String email) {
            return database.get(email);
        }

        @Override
        public List<Usuario> buscarTodos() {
            return List.copyOf(database.values());
        }
    }

    @BeforeEach
    public void setUp() {
        repositorioUsuario = new InMemoryRepositorioUsuario();
        servicoAutenticacao = new ServicoAutenticacao(repositorioUsuario);

        // Arrange: Create a user for authentication tests
        String email = "usuario@teste.com";
        String senha = "senhaCorreta";
        String senhaHash = PasswordUtil.hashPassword(senha);
        Passageiro passageiro = new Passageiro(email, senhaHash);
        repositorioUsuario.salvar(passageiro);
    }

    @Test
    public void testAutenticarComSucesso() {
        String email = "usuario@teste.com";
        String senhaCorreta = "senhaCorreta";

        Usuario usuarioAutenticado = servicoAutenticacao.autenticar(email, senhaCorreta);

        assertNotNull(usuarioAutenticado);
        assertEquals(email, usuarioAutenticado.getEmail());
    }

    @Test
    public void testDeveLancarExcecaoParaSenhaIncorreta() {
        String email = "usuario@teste.com";
        String senhaIncorreta = "senhaErrada";

        assertThrows(CredenciaisInvalidasException.class, () -> {
            servicoAutenticacao.autenticar(email, senhaIncorreta);
        });
    }

    @Test
    public void testDeveLancarExcecaoParaUsuarioNaoEncontrado() {
        String emailNaoExistente = "naoexiste@teste.com";
        String senha = "qualquerSenha";

        assertThrows(UsuarioNaoEncontradoException.class, () -> {
            servicoAutenticacao.autenticar(emailNaoExistente, senha);
        });
    }
}
