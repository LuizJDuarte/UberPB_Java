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

        if (corrida.getStatus() != CorridaStatus.CONCLUIDA) {
            throw new IllegalArgumentException("A corrida precisa estar concluída para ser avaliada.");
        }

        if (!corrida.getEmailPassageiro().equals(passageiroEmail)) {
            throw new IllegalArgumentException("Apenas o passageiro da corrida pode avaliar o motorista.");
        }

        if (corrida.getMotoristaAlocado() == null) {
            throw new IllegalArgumentException("Corrida não possui motorista alocado para avaliação.");
        }

        if (passageiroJaAvaliouMotorista(corridaId, passageiroEmail)) {
            throw new IllegalArgumentException("O passageiro já avaliou esta corrida.");
        }

        // Criar avaliação
        AvaliacaoPassageiro avaliacao = new AvaliacaoPassageiro(
                corridaId, corrida.getMotoristaAlocado(), passageiroEmail, rating, comentario);
        repositorioAvaliacao.salvar(avaliacao);

        // Atualizar rating do motorista
        atualizarRatingMotorista(corrida.getMotoristaAlocado(), rating);

        atualizarStatusAvaliacaoCorrida(corrida);

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

        if (corrida.getStatus() != CorridaStatus.CONCLUIDA) {
            throw new IllegalArgumentException("A corrida precisa estar concluída para ser avaliada.");
        }

        if (corrida.getMotoristaAlocado() == null) {
            throw new IllegalArgumentException("Corrida não possui motorista alocado para avaliação.");
        }

        if (!corrida.getMotoristaAlocado().equals(motoristaEmail)) {
            throw new IllegalArgumentException("Apenas o motorista da corrida pode avaliar o passageiro.");
        }

        if (motoristaJaAvaliouPassageiro(corridaId, motoristaEmail)) {
            throw new IllegalArgumentException("O motorista já avaliou esta corrida.");
        }

        // Criar avaliação
        AvaliacaoMotorista avaliacao = new AvaliacaoMotorista(
                corridaId, corrida.getEmailPassageiro(), motoristaEmail, rating, comentario);
        repositorioAvaliacao.salvar(avaliacao);

        // Atualizar rating do passageiro
        atualizarRatingPassageiro(corrida.getEmailPassageiro(), rating);

        atualizarStatusAvaliacaoCorrida(corrida);

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
        if (corrida == null || corrida.getStatus() != CorridaStatus.CONCLUIDA) {
            return false;
        }

        if (corrida.getEmailPassageiro().equals(usuarioEmail)) {
            return !passageiroJaAvaliouMotorista(corridaId, usuarioEmail);
        }

        if (corrida.getMotoristaAlocado() != null && corrida.getMotoristaAlocado().equals(usuarioEmail)) {
            return !motoristaJaAvaliouPassageiro(corridaId, usuarioEmail);
        }

        return false;
    }

    /**
     * Obter corridas disponíveis para avaliação
     */
    public List<Corrida> getCorridasParaAvaliar(String usuarioEmail) {
        List<Corrida> todasCorridas = repositorioCorrida.buscarTodas();
        List<Corrida> paraAvaliar = new ArrayList<>();

        for (Corrida corrida : todasCorridas) {
            if (corrida.getStatus() == CorridaStatus.CONCLUIDA &&
                    usuarioPodeAvaliarCorrida(corrida, usuarioEmail)) {
                paraAvaliar.add(corrida);
            }
        }

        return paraAvaliar;
    }

    private boolean usuarioPodeAvaliarCorrida(Corrida corrida, String usuarioEmail) {
        if (corrida.getEmailPassageiro().equals(usuarioEmail)) {
            return !passageiroJaAvaliouMotorista(corrida.getId(), usuarioEmail);
        }

        if (corrida.getMotoristaAlocado() != null && corrida.getMotoristaAlocado().equals(usuarioEmail)) {
            return !motoristaJaAvaliouPassageiro(corrida.getId(), usuarioEmail);
        }

        return false;
    }

    private boolean passageiroJaAvaliouMotorista(String corridaId, String passageiroEmail) {
        return repositorioAvaliacao.buscarPorCorrida(corridaId).stream()
                .filter(AvaliacaoPassageiro.class::isInstance)
                .map(AvaliacaoPassageiro.class::cast)
                .anyMatch(av -> av.getPassageiroEmail().equalsIgnoreCase(passageiroEmail));
    }

    private boolean motoristaJaAvaliouPassageiro(String corridaId, String motoristaEmail) {
        return repositorioAvaliacao.buscarPorCorrida(corridaId).stream()
                .filter(AvaliacaoMotorista.class::isInstance)
                .map(AvaliacaoMotorista.class::cast)
                .anyMatch(av -> av.getMotoristaEmail().equalsIgnoreCase(motoristaEmail));
    }

    private void atualizarStatusAvaliacaoCorrida(Corrida corrida) {
        boolean temAvaliacaoPassageiro = repositorioAvaliacao.buscarPorCorrida(corrida.getId()).stream()
                .anyMatch(AvaliacaoPassageiro.class::isInstance);
        boolean temAvaliacaoMotorista = repositorioAvaliacao.buscarPorCorrida(corrida.getId()).stream()
                .anyMatch(AvaliacaoMotorista.class::isInstance);

        corrida.setAvaliada(temAvaliacaoPassageiro && temAvaliacaoMotorista);
        repositorioCorrida.atualizar(corrida);
    }
}