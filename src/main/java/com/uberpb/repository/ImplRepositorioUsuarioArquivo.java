package com.uberpb.repository;

import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;
import com.uberpb.model.CategoriaVeiculo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Usado para operações com listas

public class ImplRepositorioUsuarioArquivo implements RepositorioUsuario {

    private static final String NOME_ARQUIVO = "usuarios.txt"; // Nome do arquivo de persistência
    private List<Usuario> usuarios; // Cache em memória dos usuários

    public ImplRepositorioUsuarioArquivo() {
        this.usuarios = carregarUsuarios(); // Carrega os usuários ao inicializar o repositório
    }

    @Override
    public void salvar(Usuario usuario) {
        // Verifica se o usuário já existe no cache (email é a chave primária)
        Optional<Usuario> existingUser = usuarios.stream()
                .filter(u -> u.getEmail().equals(usuario.getEmail()))
                .findFirst();

        if (existingUser.isPresent()) {
            // Se existir, remove o antigo para adicionar o novo (atualização)
            usuarios.remove(existingUser.get());
        }
        usuarios.add(usuario); // Adiciona o novo ou o atualizado
        gravarUsuarios(); // Persiste no arquivo
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Usuario> buscarTodos() {
        return new ArrayList<>(usuarios); // Retorna uma cópia para evitar modificações externas
    }

    @Override
    public void atualizar(Usuario usuario) {
        salvar(usuario); // O método salvar já trata como atualização se o email existir
    }

    /**
     * Carrega os usuários do arquivo de texto.
     * Cada linha representa um usuário.
     * Formato para Passageiro: PASSAGEIRO,email,senhaHash
     * Formato para Motorista: MOTORISTA,email,senhaHash,cnhValida,crlvValido,contaAtiva,veiculoDataOuNull
     * veiculoData: modelo|ano|placa|cor|capacidadePassageiros|tamanhoPortaMalas|cat1;cat2;...
     */
    private List<Usuario> carregarUsuarios() {
        List<Usuario> listaCarregada = new ArrayList<>();
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) {
            return listaCarregada; // Retorna lista vazia se o arquivo não existe
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] parts = linha.split(",", -1); // -1 para manter strings vazias no final
                String tipoUsuario = parts[0];
                String email = parts[1];
                String senhaHash = parts[2];

                if ("PASSAGEIRO".equals(tipoUsuario)) {
                    listaCarregada.add(new Passageiro(email, senhaHash));
                } else if ("MOTORISTA".equals(tipoUsuario)) {
                    boolean cnhValida = Boolean.parseBoolean(parts[3]);
                    boolean crlvValido = Boolean.parseBoolean(parts[4]);
                    boolean contaAtiva = Boolean.parseBoolean(parts[5]);
                    
                    Motorista motorista = new Motorista(email, senhaHash);
                    motorista.setCnhValida(cnhValida);
                    motorista.setCrlvValido(crlvValido);
                    motorista.setContaAtiva(contaAtiva);

                    if (parts.length > 6 && !"null".equals(parts[6])) {
                        String veiculoData = parts[6];
                        Veiculo veiculo = Veiculo.fromStringParaPersistencia(veiculoData);
                        motorista.setVeiculo(veiculo);
                    }
                    listaCarregada.add(motorista);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários do arquivo: " + e.getMessage());
            // Em um ambiente real, você logaria isso de forma mais robusta.
        }
        return listaCarregada;
    }

    /**
     * Grava todos os usuários atualmente no cache em memória para o arquivo de texto.
     */
    private void gravarUsuarios() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO))) {
            for (Usuario usuario : usuarios) {
                writer.write(usuario.toStringParaPersistencia());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao gravar usuários no arquivo: " + e.getMessage());
        }
    }
}
