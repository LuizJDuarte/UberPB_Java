package com.uberpb.repository;

import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImplRepositorioUsuarioArquivo extends BaseRepositorioArquivo implements RepositorioUsuario {

    private static final String ARQUIVO = "usuarios.txt";
    private final Path caminho = prepararArquivoEmData(ARQUIVO);
    private final List<Usuario> cache = new ArrayList<>();

    public ImplRepositorioUsuarioArquivo() {
        carregar();
    }

    @Override
    public synchronized void salvar(Usuario usuario) {
        Optional<Usuario> existente = cache.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(usuario.getEmail()))
                .findFirst();
        existente.ifPresent(cache::remove);
        cache.add(usuario);
        gravar();
    }

    @Override
    public synchronized Usuario buscarPorEmail(String email) {
        return cache.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public synchronized List<Usuario> buscarTodos() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized void atualizar(Usuario usuario) {
        salvar(usuario);
    }

    // ===== IO =====

    private void carregar() {
        lerLinhas(caminho, this::parseLinhaUsuario);
    }

    private void gravar() {
        gravarAtomico(caminho, cache, Usuario::toStringParaPersistencia);
    }

    private void parseLinhaUsuario(String linha) {
        String[] parts = linha.split(",", -1); // formato legado já usado no projeto
        String tipo = parts[0];
        String email = parts.length > 1 ? parts[1] : "";
        String senhaHash = parts.length > 2 ? parts[2] : "";

        if ("PASSAGEIRO".equalsIgnoreCase(tipo)) {
            cache.add(new Passageiro(email, senhaHash));
            return;
        }
        if ("MOTORISTA".equalsIgnoreCase(tipo)) {
            boolean cnhValida  = parts.length > 3 && Boolean.parseBoolean(parts[3]);
            boolean crlvValido = parts.length > 4 && Boolean.parseBoolean(parts[4]);
            boolean contaAtiva = parts.length > 5 && Boolean.parseBoolean(parts[5]);

            Motorista m = new Motorista(email, senhaHash);
            m.setCnhValida(cnhValida);
            m.setCrlvValido(crlvValido);
            m.setContaAtiva(contaAtiva);

            if (parts.length > 6) {
                Veiculo v = Veiculo.fromStringParaPersistencia(parts[6]);
                m.setVeiculo(v);
            }
            cache.add(m);
        }
    }
}
