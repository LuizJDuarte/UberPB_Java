package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ServicoCorridaTest {

    // Stubs em memória
    static class RepoUsuarioMem implements RepositorioUsuario {
        private final Map<String, Usuario> m = new HashMap<>();
        @Override public synchronized void salvar(Usuario u){ m.put(u.getEmail(), u); }
        @Override public synchronized void atualizar(Usuario u){ m.put(u.getEmail(), u); }
        @Override public synchronized Usuario buscarPorEmail(String e){ return m.get(e); }
        @Override public synchronized List<Usuario> buscarTodos(){ return new ArrayList<>(m.values()); }
        @Override public synchronized void remover(String email){ m.remove(email); }
    }
    static class RepoCorridaMem implements RepositorioCorrida {
        private final Map<String, Corrida> m = new HashMap<>();
        @Override public synchronized void salvar(Corrida c){ m.put(c.getId(), c); }
        @Override public synchronized void atualizar(Corrida c){ m.put(c.getId(), c); }
        @Override public synchronized Corrida buscarPorId(String id){ return m.get(id); }
        @Override public synchronized List<Corrida> buscarPorPassageiro(String e){
            List<Corrida> l = new ArrayList<>();
            for (Corrida c : m.values()) if (c.getEmailPassageiro().equals(e)) l.add(c);
            return l;
        }
        @Override public synchronized List<Corrida> buscarTodas(){ return new ArrayList<>(m.values()); }
        @Override public synchronized void remover(String id){ m.remove(id); }
    }

    @Test
    void solicitarPorEndereco_ok() {
        var ru = new RepoUsuarioMem();
        var rc = new RepoCorridaMem();
        var sc = new ServicoCorrida(rc, ru);

        new ServicoCadastro(ru, new ServicoValidacaoMotorista()).cadastrarPassageiro("p@a.com","123");

        Corrida c = sc.solicitarCorrida("p@a.com","Rua A, 10","Rua B, 20", CategoriaVeiculo.UBERX, MetodoPagamento.DINHEIRO);
        assertNotNull(c);
        assertEquals("p@a.com", c.getEmailPassageiro());
        assertEquals(CategoriaVeiculo.UBERX, c.getCategoriaEscolhida());
    }

    @Test
    void solicitarPorEndereco_invalido() {
        var ru = new RepoUsuarioMem();
        var rc = new RepoCorridaMem();
        var sc = new ServicoCorrida(rc, ru);

        assertThrows(IllegalArgumentException.class,
                () -> sc.solicitarCorrida("p@a.com","R","R", CategoriaVeiculo.UBERX, MetodoPagamento.CARTAO));
    }
}
