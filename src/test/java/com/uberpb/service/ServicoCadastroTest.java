package com.uberpb.service;

import com.uberpb.exceptions.EmailJaExistenteException;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoCadastroTest {

    private ServicoCadastro servicoCadastro;
    private RepositorioUsuario repositorioUsuario;

    // Test-specific implementation of the repository that stores data in memory.
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

        @Override
        public void remover(String email) {
            database.remove(email);
        }

        @Override
        public void limpar() {
            database.clear();
        }
    }

    @BeforeEach
    public void setUp() {
        // Use the in-memory repository for tests
        repositorioUsuario = new InMemoryRepositorioUsuario();
        servicoCadastro = new ServicoCadastro(repositorioUsuario);
    }

    @Test
    public void testCadastrarPassageiroComSucesso() {
        String email = "passageiro@teste.com";
        String senha = "senha123";

        Passageiro passageiro = servicoCadastro.cadastrarPassageiro(email, senha);

        assertNotNull(passageiro);
        assertEquals(email, passageiro.getEmail());

        Usuario usuarioSalvo = repositorioUsuario.buscarPorEmail(email);
        assertNotNull(usuarioSalvo);
        assertEquals(email, usuarioSalvo.getEmail());
    }

    @Test
    public void testCadastrarMotoristaComSucesso() {
        String email = "motorista@teste.com";
        String senha = "senha456";

        Motorista motorista = servicoCadastro.cadastrarMotorista(email, senha);

        assertNotNull(motorista);
        assertEquals(email, motorista.getEmail());
        assertFalse(motorista.isContaAtiva()); // Should be inactive by default

        Usuario usuarioSalvo = repositorioUsuario.buscarPorEmail(email);
        assertNotNull(usuarioSalvo);
        assertEquals(email, usuarioSalvo.getEmail());
    }

    @Test
    public void testNaoDeveCadastrarEmailJaExistente() {
        String email = "existente@teste.com";
        String senha = "senha123";

        // Arrange: Register a user first
        servicoCadastro.cadastrarPassageiro(email, senha);

        // Act & Assert: Try to register the same email again and expect an exception
        assertThrows(EmailJaExistenteException.class, () -> {
            servicoCadastro.cadastrarMotorista(email, "outrasenha");
        });
    }

    @Test
    public void testNaoDeveCadastrarComEmailInvalido() {
        String emailInvalido = "email-invalido";
        String senha = "senha123";

        // Expect an IllegalArgumentException for invalid email format
        assertThrows(IllegalArgumentException.class, () -> {
            servicoCadastro.cadastrarPassageiro(emailInvalido, senha);
        });
    }
}
