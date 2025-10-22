package com.uberpb.service;

import com.uberpb.model.Passageiro;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicoAutenticacaoTest {

    // Stub simples em memória
    static class RepoInMemory implements RepositorioUsuario {
        private final java.util.Map<String, com.uberpb.model.Usuario> m = new java.util.HashMap<>();
        @Override public synchronized void salvar(com.uberpb.model.Usuario u){ m.put(u.getEmail(), u); }
        @Override public synchronized void atualizar(com.uberpb.model.Usuario u){ m.put(u.getEmail(), u); }
        @Override public synchronized com.uberpb.model.Usuario buscarPorEmail(String e){ return m.get(e); }
        @Override public synchronized java.util.List<com.uberpb.model.Usuario> buscarTodos(){ return new java.util.ArrayList<>(m.values()); }
        @Override public synchronized void remover(String email){ m.remove(email); }
    }

    @Test
    void loginValido() {
        var repo = new RepoInMemory();
        var cadastro = new ServicoCadastro(repo, new ServicoValidacaoMotorista());
        var auth = new ServicoAutenticacao(repo);

        cadastro.cadastrarPassageiro("p@a.com", "123");
        var u = auth.login("p@a.com", "123");

        assertNotNull(u);
        assertEquals("p@a.com", u.getEmail());
    }

    @Test
    void loginInvalido() {
        var repo = new RepoInMemory();
        var auth = new ServicoAutenticacao(repo);
        assertThrows(IllegalArgumentException.class, () -> auth.login("x@y.com", "errada"));
    }
}
