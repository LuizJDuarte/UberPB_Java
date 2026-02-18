package com.uberpb.repository;

import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Entregador;
import com.uberpb.model.Restaurante;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;
import com.uberpb.model.Administrador;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImplRepositorioUsuarioArquivo extends BaseRepositorioArquivo implements RepositorioUsuario {

    private static final String ARQUIVO = "usuarios.txt";
    private final Path caminho = prepararArquivoEmData(ARQUIVO);
    private final List<Usuario> cache = new ArrayList<>();

    private static final ImplRepositorioUsuarioArquivo INSTANCE = new ImplRepositorioUsuarioArquivo();

    private ImplRepositorioUsuarioArquivo() {
        carregar();
    }

    public static ImplRepositorioUsuarioArquivo getInstance() {
        return INSTANCE;
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
    public synchronized void remover(String email) {
        cache.removeIf(u -> u.getEmail().equalsIgnoreCase(email));
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

    @Override
    public synchronized void limpar() {
        cache.clear();
        gravar();
    }

    private void carregar() {
        lerLinhas(caminho, this::parseLinhaUsuario);
    }

    private void gravar() {
        gravarAtomico(caminho, cache, Usuario::toStringParaPersistencia);
    }

    private void parseLinhaUsuario(String linha) {
        if (linha == null || linha.isBlank()) return;
        
        // ✅ CORREÇÃO: Para o Restaurante, NÃO usamos o split(",") geral primeiro,
        // pois a string do cardápio pode conter muitos dados. 
        // Deixamos a própria classe Restaurante resolver sua linha.
        if (linha.startsWith("RESTAURANTE")) {
            Restaurante restaurante = Restaurante.fromString(linha);
            if (restaurante != null) cache.add(restaurante);
            return;
        }

        String[] parts = linha.split(",", -1);
        if (parts.length < 3) return;

        String tipo = parts[0];
        String email = parts[1];
        String senhaHash = parts[2];

        if ("PASSAGEIRO".equalsIgnoreCase(tipo)) {
            Passageiro passageiro = new Passageiro(email, senhaHash);
            if (parts.length > 3 && !parts[3].isEmpty()) {
                try { passageiro.setRatingMedio(Double.parseDouble(parts[3])); } catch (Exception ignored) {}
            }
            if (parts.length > 4 && !parts[4].isEmpty()) {
                try { passageiro.setTotalAvaliacoes(Integer.parseInt(parts[4])); } catch (Exception ignored) {}
            }
            if (parts.length > 5) passageiro.setContaAtiva(Boolean.parseBoolean(parts[5]));
            cache.add(passageiro);
        } else if ("MOTORISTA".equalsIgnoreCase(tipo)) {
            Motorista motorista = new Motorista(email, senhaHash);
            if (parts.length > 3) motorista.setCnhValida(Boolean.parseBoolean(parts[3]));
            if (parts.length > 4) motorista.setCrlvValido(Boolean.parseBoolean(parts[4]));
            if (parts.length > 5) motorista.setContaAtiva(Boolean.parseBoolean(parts[5]));
            if (parts.length > 6 && !parts[6].isEmpty()) {
                try { motorista.setRatingMedio(Double.parseDouble(parts[6])); } catch (Exception ignored) {}
            }
            if (parts.length > 7 && !parts[7].isEmpty()) {
                try { motorista.setTotalAvaliacoes(Integer.parseInt(parts[7])); } catch (Exception ignored) {}
            }
            if (parts.length > 8) motorista.setDisponivel(Boolean.parseBoolean(parts[8]));
            if (parts.length > 9 && !parts[9].isEmpty() && !"null".equals(parts[9])) {
                motorista.setVeiculo(Veiculo.fromStringParaPersistencia(parts[9]));
            }
            cache.add(motorista);
        } else if ("ENTREGADOR".equalsIgnoreCase(tipo)) {
            Entregador entregador = new Entregador(email, senhaHash);
            if (parts.length > 3) entregador.setCnhNumero(parts[3]);
            if (parts.length > 4) entregador.setCpfNumero(parts[4]);
            if (parts.length > 5) entregador.setCnhValida(Boolean.parseBoolean(parts[5]));
            if (parts.length > 6) entregador.setDocIdentidadeValido(Boolean.parseBoolean(parts[6]));
            if (parts.length > 7) entregador.setContaAtiva(Boolean.parseBoolean(parts[7]));
            cache.add(entregador);
        } else if ("ADMIN".equalsIgnoreCase(tipo)) {
            cache.add(new Administrador(email, senhaHash));
        }
    }
}