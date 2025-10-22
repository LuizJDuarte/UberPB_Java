package com.uberpb.service;

import com.uberpb.model.*;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

public class ServicoCorrida {

    private final RepositorioCorrida repositorioCorrida;
    private final RepositorioUsuario repositorioUsuario;
    private final EstimativaCorrida estimativaCorrida;
    private final ServicoLocalizacao servicoLocalizacao;

    public ServicoCorrida(RepositorioCorrida rc,
                          RepositorioUsuario ru,
                          EstimativaCorrida estimativaCorrida,
                          ServicoLocalizacao servicoLocalizacao) {
        this.repositorioCorrida = rc;
        this.repositorioUsuario = ru;
        this.estimativaCorrida = estimativaCorrida;
        this.servicoLocalizacao = servicoLocalizacao;
    }

    // CLI usa antes de confirmar a solicitação
    public EstimativaCorrida estimarPorEnderecos(String origem, String destino, CategoriaVeiculo cat) {
        return estimativaCorrida.calcularPorEnderecos(origem, destino, cat);
    }

    public Corrida solicitarCorrida(String emailPassageiro,
                                    String origemEndereco,
                                    String destinoEndereco,
                                    CategoriaVeiculo cat,
                                    MetodoPagamento mp) {
        if (emailPassageiro == null || emailPassageiro.isBlank())
            throw new IllegalArgumentException("Passageiro inválido.");
        if (origemEndereco == null || origemEndereco.isBlank() ||
            destinoEndereco == null || destinoEndereco.isBlank())
            throw new IllegalArgumentException("Origem e destino são obrigatórios.");

        Usuario u = repositorioUsuario.buscarPorEmail(emailPassageiro);
        if (!(u instanceof Passageiro))
            throw new IllegalArgumentException("Apenas passageiros podem solicitar corridas.");

        Corrida c = Corrida.novaComEnderecos(emailPassageiro, origemEndereco.trim(), destinoEndereco.trim(), cat, mp);

        // Tenta coordenadas para habilitar mapa/progresso
        if (c.getOrigem() == null && c.getOrigemEndereco() != null) {
            try {
                Localizacao o = servicoLocalizacao.geocodificar(c.getOrigemEndereco());
                Localizacao d = servicoLocalizacao.geocodificar(c.getDestinoEndereco());
                c = Corrida.novaComCoordenadas(emailPassageiro, o, d, cat, mp);
            } catch (Exception ignored) {}
        }

        repositorioCorrida.salvar(c);
        return c;
    }

    // ===== Progresso “tempo real” =====

    public void iniciarSeAceita(Corrida corrida, GerenciadorCorridasAtivas gerenciador) {
        if (corrida == null) return;
        if (corrida.getStatus() != CorridaStatus.ACEITA && corrida.getStatus() != CorridaStatus.EM_ANDAMENTO) return;

        var est = estimativa(corrida);
        if (!gerenciador.isAtiva(corrida.getId())) {
            gerenciador.iniciar(corrida.getId(), est.getMinutos(), est.getDistanciaKm());
        }
    }

    public com.uberpb.service.GerenciadorCorridasAtivas.Progresso progresso(String corridaId, GerenciadorCorridasAtivas gerenciador) {
        return gerenciador.progresso(corridaId);
    }

    public void encerrarSeConcluida(String corridaId, GerenciadorCorridasAtivas gerenciador) {
        if (gerenciador.isConcluida(corridaId)) {
            Corrida c = repositorioCorrida.buscarPorId(corridaId);
            if (c != null) {
                c.setStatus(CorridaStatus.CONCLUIDA);
                repositorioCorrida.atualizar(c);
            }
            gerenciador.encerrar(corridaId);
        }
    }

    // usado no ConcluirCorridaComando
    public void concluirCorrida(String corridaId, String emailSolicitante, GerenciadorCorridasAtivas gerenciador) {
        Corrida c = repositorioCorrida.buscarPorId(corridaId);
        if (c == null) throw new IllegalArgumentException("Corrida não encontrada.");

        boolean ehPass = emailSolicitante.equalsIgnoreCase(c.getEmailPassageiro());
        boolean ehMot  = emailSolicitante.equalsIgnoreCase(c.getMotoristaAlocado());
        if (!ehPass && !ehMot && !(repositorioUsuario.buscarPorEmail(emailSolicitante) instanceof Administrador)) {
            throw new SecurityException("Sem permissão para concluir esta corrida.");
        }

        gerenciador.encerrar(corridaId);
        c.setStatus(CorridaStatus.CONCLUIDA);
        repositorioCorrida.atualizar(c);
    }

    private EstimativaCorrida estimativa(Corrida c) {
        if (c.getOrigem() != null && c.getDestino() != null) {
            return estimativaCorrida.calcularPorCoordenadas(c.getOrigem(), c.getDestino(), c.getCategoriaEscolhida());
        }
        return estimativaCorrida.calcularPorEnderecos(c.getOrigemEndereco(), c.getDestinoEndereco(), c.getCategoriaEscolhida());
    }
}
