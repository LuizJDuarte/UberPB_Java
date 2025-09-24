package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

import java.util.List;
import java.util.Optional;

public class ServicoDirecionamentoCorrida {
    
    private final RepositorioCorrida repositorioCorrida;
    private final RepositorioUsuario repositorioUsuario;
    
    public ServicoDirecionamentoCorrida(RepositorioCorrida repositorioCorrida, 
                                      RepositorioUsuario repositorioUsuario) {
        this.repositorioCorrida = repositorioCorrida;
        this.repositorioUsuario = repositorioUsuario;
    }
    
    /**
     * Direciona automaticamente uma corrida para um motorista disponível
     */
    public Optional<Motorista> direcionarCorridaAutomaticamente(Corrida corrida) {
        List<Usuario> usuarios = repositorioUsuario.buscarTodos();
        
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Motorista motorista) {
                if (isMotoristaDisponivel(motorista) && 
                    motoristaAceitaCategoria(motorista, corrida.getCategoriaEscolhida())) {
                    
                    // Atribuir corrida ao motorista
                    corrida.setMotoristaAlocado(motorista.getEmail());
                    corrida.setStatus(CorridaStatus.EM_ANDAMENTO);
                    repositorioCorrida.atualizar(corrida);
                    
                    return Optional.of(motorista);
                }
            }
        }
        return Optional.empty();
    }
    
    private boolean isMotoristaDisponivel(Motorista motorista) {
        return motorista.isContaAtiva() && 
               motorista.isCnhValida() && 
               motorista.isCrlvValido();
    }
    
    private boolean motoristaAceitaCategoria(Motorista motorista, CategoriaVeiculo categoria) {
        if (categoria == null) return true;
        
        Veiculo veiculo = motorista.getVeiculo();
        if (veiculo == null) return false;
        
        return veiculo.getCategoriasDisponiveis().contains(categoria);
    }
    
    /**
     * Inicia uma corrida (motorista a caminho)
     */
    public void iniciarCorrida(String corridaId, String motoristaEmail) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida != null) {
            corrida.setMotoristaAlocado(motoristaEmail);
            corrida.setStatus(CorridaStatus.EM_ANDAMENTO);
            repositorioCorrida.atualizar(corrida);
        }
    }
    
    /**
     * Conclui uma corrida
     */
    public void concluirCorrida(String corridaId) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida != null) {
            corrida.setStatus(CorridaStatus.CONCLUIDA);
            repositorioCorrida.atualizar(corrida);
        }
    }
}