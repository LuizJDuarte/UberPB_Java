package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoCorridaTest {

    private ServicoCorrida servicoCorrida;
    private RepositorioUsuario repositorioUsuario;
    private RepositorioCorrida repositorioCorrida;
    private Passageiro passageiro;

    // In-memory repository for Users
    static class InMemoryRepositorioUsuario implements RepositorioUsuario {
        private final Map<String, Usuario> database = new HashMap<>();
        @Override public void salvar(Usuario usuario) { database.put(usuario.getEmail(), usuario); }
        @Override public void atualizar(Usuario usuario) { database.put(usuario.getEmail(), usuario); }
        @Override public Usuario buscarPorEmail(String email) { return database.get(email); }
        @Override public List<Usuario> buscarTodos() { return List.copyOf(database.values()); }
    }

    // In-memory repository for Corridas
    static class InMemoryRepositorioCorrida implements RepositorioCorrida {
        private final Map<String, Corrida> database = new HashMap<>();

        @Override
        public void salvar(Corrida corrida) {
            database.put(corrida.getId(), corrida);
        }

        @Override
        public void atualizar(Corrida corrida) {
            database.put(corrida.getId(), corrida);
        }

        @Override
        public Corrida buscarPorId(String id) {
            return database.get(id);
        }

        @Override
        public List<Corrida> buscarPorPassageiro(String email) {
            return database.values().stream()
                    .filter(c -> c.getEmailPassageiro().equals(email))
                    .collect(Collectors.toList());
        }

        @Override
        public List<Corrida> buscarTodas() {
            return List.copyOf(database.values());
        }
    }

    @BeforeEach
    public void setUp() {
        repositorioUsuario = new InMemoryRepositorioUsuario();
        repositorioCorrida = new InMemoryRepositorioCorrida();
        servicoCorrida = new ServicoCorrida(repositorioCorrida, repositorioUsuario);

        passageiro = new Passageiro("passageiro@teste.com", "senhaHash");
        repositorioUsuario.salvar(passageiro);
    }

    @Test
    public void testSolicitarCorridaComCategoriaComSucesso() {
        String origem = "Rua A, 123";
        String destino = "Rua B, 456";
        CategoriaVeiculo categoria = CategoriaVeiculo.UBERX;
        double preco = 15.0;

        Corrida corrida = servicoCorrida.solicitarCorridaComCategoria(passageiro.getEmail(), origem, destino, categoria, preco);

        assertNotNull(corrida);
        assertEquals(origem, corrida.getOrigemEndereco());
        assertEquals(destino, corrida.getDestinoEndereco());
        assertEquals(passageiro.getEmail(), corrida.getEmailPassageiro());
        assertEquals(CorridaStatus.SOLICITADA, corrida.getStatus());
        assertEquals(categoria, corrida.getCategoria());
        assertEquals(preco, corrida.getPrecoEstimado());

        Corrida corridaSalva = repositorioCorrida.buscarPorId(corrida.getId());
        assertNotNull(corridaSalva);
    }

    @Test
    public void testNaoDeveSolicitarCorridaComOrigemIgualDestino() {
        String endereco = "Rua C, 789";

        assertThrows(IllegalArgumentException.class, () -> {
            servicoCorrida.solicitarCorridaComCategoria(passageiro.getEmail(), endereco, endereco, CategoriaVeiculo.COMFORT, 20.0);
        });
    }

    @Test
    public void testNaoDeveSolicitarCorridaParaPassageiroInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            servicoCorrida.solicitarCorridaComCategoria("naoexiste@email.com", "Origem", "Destino", CategoriaVeiculo.BLACK, 50.0);
        });
    }

    @Test
    public void testNaoDeveSolicitarCorridaComPrecoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            servicoCorrida.solicitarCorridaComCategoria(passageiro.getEmail(), "Origem", "Destino", CategoriaVeiculo.UBERX, -10.0);
        });
    }
}