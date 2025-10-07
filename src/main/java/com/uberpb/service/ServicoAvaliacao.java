package com.uberpb.service;

import com.uberpb.model.Avaliacao;
import com.uberpb.model.AvaliacaoMotorista;
import com.uberpb.model.AvaliacaoPassageiro;
import com.uberpb.model.Corrida;
import com.uberpb.model.CorridaStatus;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioAvaliacao;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import java.util.ArrayList;
import java.util.List;

public class ServicoAvaliacao {

    private final RepositorioAvaliacao repositorioAvaliacao;
    private final RepositorioCorrida repositorioCorrida;
    private final RepositorioUsuario repositorioUsuario;

    public ServicoAvaliacao(RepositorioAvaliacao repositorioAvaliacao, 
                           RepositorioCorrida repositorioCorrida,
                           RepositorioUsuario repositorioUsuario) {
        this.repositorioAvaliacao = repositorioAvaliacao;
        this.repositorioCorrida = repositorioCorrida;
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Passageiro avalia motorista
     */
    public void avaliarMotorista(String corridaId, String passageiroEmail, int rating, String comentario) {
        validarAvaliacao(corridaId, passageiroEmail, rating);
        
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida == null) {
            throw new IllegalArgumentException("Corrida não encontrada: " + corridaId);
        }

        if (!corrida.getEmailPassageiro().equals(passageiroEmail)) {
            throw new IllegalArgumentException("Apenas o passageiro da corrida pode avaliar o motorista.");
        }

        if (corrida.getMotoristaAlocado() == null) {
            throw new IllegalArgumentException("Corrida não possui motorista alocado para avaliação.");
        }

        // Verificar se já foi avaliada
        if (repositorioAvaliacao.corridaFoiAvaliada(corridaId)) {
            throw new IllegalArgumentException("Esta corrida já foi avaliada.");
        }

        // Criar avaliação
        AvaliacaoPassageiro avaliacao = new AvaliacaoPassageiro(
            corridaId, corrida.getMotoristaAlocado(), passageiroEmail, rating, comentario
        );
        repositorioAvaliacao.salvar(avaliacao);

        // Atualizar rating do motorista
        atualizarRatingMotorista(corrida.getMotoristaAlocado(), rating);

        // Marcar corrida como avaliada
        corrida.setAvaliada(true);
        repositorioCorrida.atualizar(corrida);

        System.out.println("✅ Avaliação do motorista registrada com sucesso!");
    }

    /**
     * Motorista avalia passageiro
     */
    public void avaliarPassageiro(String corridaId, String motoristaEmail, int rating, String comentario) {
        validarAvaliacao(corridaId, motoristaEmail, rating);
        
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida == null) {
            throw new IllegalArgumentException("Corrida não encontrada: " + corridaId);
        }

        if (!corrida.getMotoristaAlocado().equals(motoristaEmail)) {
            throw new IllegalArgumentException("Apenas o motorista da corrida pode avaliar o passageiro.");
        }

        // Verificar se já foi avaliada
        if (repositorioAvaliacao.corridaFoiAvaliada(corridaId)) {
            throw new IllegalArgumentException("Esta corrida já foi avaliada.");
        }

        // Criar avaliação
        AvaliacaoMotorista avaliacao = new AvaliacaoMotorista(
            corridaId, corrida.getEmailPassageiro(), motoristaEmail, rating, comentario
        );
        repositorioAvaliacao.salvar(avaliacao);

        // Atualizar rating do passageiro
        atualizarRatingPassageiro(corrida.getEmailPassageiro(), rating);

        // Marcar corrida como avaliada
        corrida.setAvaliada(true);
        repositorioCorrida.atualizar(corrida);

        System.out.println("✅ Avaliação do passageiro registrada com sucesso!");
    }

    private void validarAvaliacao(String corridaId, String usuarioEmail, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating deve ser entre 1 e 5 estrelas.");
        }

        if (corridaId == null || corridaId.isBlank()) {
            throw new IllegalArgumentException("ID da corrida é obrigatório.");
        }

        if (usuarioEmail == null || usuarioEmail.isBlank()) {
            throw new IllegalArgumentException("Email do usuário é obrigatório.");
        }
    }

    private void atualizarRatingMotorista(String motoristaEmail, int novaAvaliacao) {
        Usuario usuario = repositorioUsuario.buscarPorEmail(motoristaEmail);
        if (usuario instanceof Motorista motorista) {
            motorista.adicionarAvaliacao(novaAvaliacao);
            repositorioUsuario.atualizar(motorista);
        }
    }

    private void atualizarRatingPassageiro(String passageiroEmail, int novaAvaliacao) {
        Usuario usuario = repositorioUsuario.buscarPorEmail(passageiroEmail);
        if (usuario instanceof Passageiro passageiro) {
            passageiro.adicionarAvaliacao(novaAvaliacao);
            repositorioUsuario.atualizar(passageiro);
        }
    }

    /**
     * Obter avaliações de um motorista
     */
    public List<Avaliacao> getAvaliacoesMotorista(String motoristaEmail) {
        return repositorioAvaliacao.buscarPorMotorista(motoristaEmail);
    }

    /**
     * Obter avaliações de um passageiro
     */
    public List<Avaliacao> getAvaliacoesPassageiro(String passageiroEmail) {
        return repositorioAvaliacao.buscarPorPassageiro(passageiroEmail);
    }

    /**
     * Verificar se uma corrida pode ser avaliada
     */
    public boolean podeAvaliarCorrida(String corridaId, String usuarioEmail) {
        Corrida corrida = repositorioCorrida.buscarPorId(corridaId);
        if (corrida == null || corrida.isAvaliada()) {
            return false;
        }

        // Verificar se o usuário é passageiro ou motorista da corrida
        return corrida.getEmailPassageiro().equals(usuarioEmail) || 
               (corrida.getMotoristaAlocado() != null && corrida.getMotoristaAlocado().equals(usuarioEmail));
    }

    /**
     * Obter corridas disponíveis para avaliação
     */
    public List<Corrida> getCorridasParaAvaliar(String usuarioEmail) {
        List<Corrida> todasCorridas = repositorioCorrida.buscarTodas();
        List<Corrida> paraAvaliar = new ArrayList<>(); // ✅ CORRIGIDO: Import ArrayList adicionado

        for (Corrida corrida : todasCorridas) {
            if (corrida.getStatus() == CorridaStatus.CONCLUIDA && 
                !corrida.isAvaliada() &&
                (corrida.getEmailPassageiro().equals(usuarioEmail) || 
                 (corrida.getMotoristaAlocado() != null && corrida.getMotoristaAlocado().equals(usuarioEmail)))) {
                paraAvaliar.add(corrida);
            }
        }

        return paraAvaliar;
    }
}