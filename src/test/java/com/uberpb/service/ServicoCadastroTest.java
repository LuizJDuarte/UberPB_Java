package com.uberpb.service;

import com.uberpb.exceptions.EmailJaExistenteException;
import com.uberpb.model.*;
import com.uberpb.repository.RepositorioRestaurante;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoCadastroTest {

    private ServicoCadastro servicoCadastro;
    private RepositorioUsuario repositorioUsuario;
    private RepositorioRestaurante repositorioRestaurante;

    // ==========================
    // REPOSITÓRIO EM MEMÓRIA
    // ==========================

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
    void setup() {

        repositorioUsuario = new InMemoryRepositorioUsuario();
        repositorioRestaurante = mock(RepositorioRestaurante.class);

        servicoCadastro = new ServicoCadastro(repositorioUsuario);
        servicoCadastro.setRepositorioRestaurante(repositorioRestaurante);
    }

    // ==========================
    // PASSAGEIRO
    // ==========================

    @Test
    void deveCadastrarPassageiro() {

        Passageiro passageiro =
                servicoCadastro.cadastrarPassageiro("passageiro@email.com", "123");

        assertNotNull(passageiro);
        assertEquals("passageiro@email.com", passageiro.getEmail());

        Usuario salvo = repositorioUsuario.buscarPorEmail("passageiro@email.com");

        assertNotNull(salvo);
        assertTrue(salvo instanceof Passageiro);
    }

    @Test
    void naoDeveCadastrarPassageiroEmailInvalido() {

        assertThrows(IllegalArgumentException.class, () ->
                servicoCadastro.cadastrarPassageiro("email-invalido", "123"));
    }

    // ==========================
    // MOTORISTA
    // ==========================

    @Test
    void deveCadastrarMotorista() {

        Motorista motorista =
                servicoCadastro.cadastrarMotorista("motorista@email.com", "123");

        assertNotNull(motorista);
        assertEquals("motorista@email.com", motorista.getEmail());

        Usuario salvo = repositorioUsuario.buscarPorEmail("motorista@email.com");

        assertTrue(salvo instanceof Motorista);
    }

    @Test
    void motoristaNaoPodeTerEmailDuplicado() {

        servicoCadastro.cadastrarMotorista("motorista@email.com", "123");

        assertThrows(EmailJaExistenteException.class, () ->
                servicoCadastro.cadastrarMotorista("motorista@email.com", "456"));
    }

    // ==========================
    // ENTREGADOR
    // ==========================

    @Test
    void deveCadastrarEntregador() {

        Entregador entregador = servicoCadastro.cadastrarEntregador(
                "entregador@email.com",
                "123",
                "123456",
                "99999999999"
        );

        assertNotNull(entregador);
        assertEquals("123456", entregador.getCnhNumero());
        assertEquals("99999999999", entregador.getCpfNumero());

        assertTrue(entregador.isContaAtiva());
        assertFalse(entregador.isCnhValida());
        assertFalse(entregador.isDocIdentidadeValido());
    }

    @Test
    public void deveCadastrarEntregadorComLocalizacao() {
        var repo = ImplRepositorioUsuarioArquivo.getInstance();
        ServicoCadastro servico = new ServicoCadastro(repo);
        
        var e = servico.cadastrarEntregador("teste@entregador.com", "123", "CNH123", "CPF123");
        e.setLocalizacao(new com.uberpb.model.Localizacao(-7.0, -35.0));
        
        assertNotNull(e.getLocalizacao());
        assertEquals(-7.0, e.getLocalizacao().latitude());
    }

    @Test
    void deveCadastrarEntregadorComCamposNull() {

        Entregador entregador = servicoCadastro.cadastrarEntregador(
                "entregador2@email.com",
                "123",
                null,
                null
        );

        assertEquals("", entregador.getCnhNumero());
        assertEquals("", entregador.getCpfNumero());
    }

    @Test
    void entregadorNaoPodeTerEmailDuplicado() {

        servicoCadastro.cadastrarEntregador(
                "entregador@email.com", "123", "1", "1");

        assertThrows(EmailJaExistenteException.class, () ->
                servicoCadastro.cadastrarEntregador(
                        "entregador@email.com", "123", "2", "2"));
    }

    // ==========================
    // RESTAURANTE
    // ==========================

    @Test
    void deveCadastrarRestaurante() {

        Restaurante restaurante =
                servicoCadastro.cadastrarRestaurante(
                        "restaurante@email.com",
                        "123",
                        "Pizza Top",
                        "12345678000199"
                );

        assertNotNull(restaurante);

        assertEquals("Pizza Top", restaurante.getNomeFantasia());
        assertEquals("12345678000199", restaurante.getCnpj());

        verify(repositorioRestaurante).salvar(restaurante);
    }

    @Test
    void restauranteComCamposNull() {

        Restaurante restaurante =
                servicoCadastro.cadastrarRestaurante(
                        "rest2@email.com",
                        "123",
                        null,
                        null
                );

        assertEquals("", restaurante.getNomeFantasia());
        assertEquals("", restaurante.getCnpj());
    }

    @Test
    void restauranteNaoPodeTerEmailDuplicado() {

        servicoCadastro.cadastrarRestaurante(
                "rest@email.com",
                "123",
                "R1",
                "1"
        );

        assertThrows(EmailJaExistenteException.class, () ->
                servicoCadastro.cadastrarRestaurante(
                        "rest@email.com",
                        "123",
                        "R2",
                        "2"
                ));
    }

}