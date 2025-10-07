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
            Passageiro passageiro = new Passageiro(email, senhaHash);
            
            // ✅ CARREGAR DADOS DE AVALIAÇÃO DO PASSAGEIRO
            if (parts.length > 3 && !parts[3].isEmpty()) {
                try {
                    passageiro.setRatingMedio(Double.parseDouble(parts[3]));
                } catch (NumberFormatException e) {
                    passageiro.setRatingMedio(0.0);
                }
            }
            if (parts.length > 4 && !parts[4].isEmpty()) {
                try {
                    passageiro.setTotalAvaliacoes(Integer.parseInt(parts[4]));
                } catch (NumberFormatException e) {
                    passageiro.setTotalAvaliacoes(0);
                }
            }
            
            cache.add(passageiro);
            return;
        }
        if ("MOTORISTA".equalsIgnoreCase(tipo)) {
            boolean cnhValida  = parts.length > 3 && Boolean.parseBoolean(parts[3]);
            boolean crlvValido = parts.length > 4 && Boolean.parseBoolean(parts[4]);
            boolean contaAtiva = parts.length > 5 && Boolean.parseBoolean(parts[5]);

            Motorista motorista = new Motorista(email, senhaHash);
            motorista.setCnhValida(cnhValida);
            motorista.setCrlvValido(crlvValido);
            motorista.setContaAtiva(contaAtiva);

            // ✅ CARREGAR DADOS DE AVALIAÇÃO DO MOTORISTA
            if (parts.length > 6 && !parts[6].isEmpty()) {
                try {
                    motorista.setRatingMedio(Double.parseDouble(parts[6]));
                } catch (NumberFormatException e) {
                    motorista.setRatingMedio(0.0);
                }
            }
            if (parts.length > 7 && !parts[7].isEmpty()) {
                try {
                    motorista.setTotalAvaliacoes(Integer.parseInt(parts[7]));
                } catch (NumberFormatException e) {
                    motorista.setTotalAvaliacoes(0);
                }
            }

            // ✅ CARREGAR VEÍCULO (agora na posição 8 devido aos novos campos)
            if (parts.length > 8 && !parts[8].isEmpty() && !"null".equals(parts[8])) {
                Veiculo veiculo = Veiculo.fromStringParaPersistencia(parts[8]);
                motorista.setVeiculo(veiculo);
            }
            
            cache.add(motorista);
        }
    }
}