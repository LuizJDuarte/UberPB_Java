package com.uberpb.app;

import com.uberpb.repository.RepositorioAvaliacao;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioOferta;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.*;

public class ContextoAplicacao {
    public final Sessao sessao;

    public final RepositorioUsuario repositorioUsuario;
    public final ServicoCadastro servicoCadastro;
    public final ServicoAutenticacao servicoAutenticacao;

    public final RepositorioCorrida repositorioCorrida;
    public final ServicoCorrida servicoCorrida;

    public final RepositorioOferta repositorioOferta;
    public final RepositorioAvaliacao repositorioAvaliacao;
    public final ServicoOferta servicoOferta;

    public final ServicoValidacaoMotorista servicoValidacaoMotorista;
    public final ServicoPagamento servicoPagamento;
    public final ServicoAvaliacao servicoAvaliacao;

    public final ServicoOtimizacaoRota servicoOtimizacaoRota; // nome usado pelos comandos
    public final ServicoLocalizacao servicoLocalizacao;
    public final ServicoDirecionamentoCorrida servicoDirecionamento;
    public final EstimativaChegada servicoEstimativaChegada;

    public final ServicoAdmin servicoAdmin;
    public final GerenciadorCorridasAtivas gerenciadorCorridas;

    // Construtor vazio: inicializa todos os 'final' (pode ficar nulo; o provedor usa o construtor completo)
    public ContextoAplicacao() {
        this.sessao = null;
        this.repositorioUsuario = null;
        this.servicoCadastro = null;
        this.servicoAutenticacao = null;
        this.repositorioCorrida = null;
        this.servicoCorrida = null;
        this.repositorioOferta = null;
        this.repositorioAvaliacao = null;
        this.servicoOferta = null;
        this.servicoValidacaoMotorista = null;
        this.servicoPagamento = null;
        this.servicoAvaliacao = null;
        this.servicoOtimizacaoRota = null;
        this.servicoLocalizacao = null;
        this.servicoDirecionamento = null;
        this.servicoEstimativaChegada = null;
        this.servicoAdmin = null;
        this.gerenciadorCorridas = null;
    }

    public ContextoAplicacao(Sessao sessao,
                             RepositorioUsuario repositorioUsuario,
                             ServicoCadastro servicoCadastro,
                             ServicoAutenticacao servicoAutenticacao,
                             RepositorioCorrida repositorioCorrida,
                             ServicoCorrida servicoCorrida,
                             RepositorioOferta repositorioOferta,
                             RepositorioAvaliacao repositorioAvaliacao,
                             ServicoOferta servicoOferta,
                             ServicoValidacaoMotorista servicoValidacaoMotorista,
                             ServicoPagamento servicoPagamento,
                             ServicoAvaliacao servicoAvaliacao,
                             ServicoOtimizacaoRota servicoOtimizacaoRota,
                             ServicoLocalizacao servicoLocalizacao,
                             ServicoDirecionamentoCorrida servicoDirecionamento,
                             EstimativaChegada servicoEstimativaChegada,
                             ServicoAdmin servicoAdmin,
                             GerenciadorCorridasAtivas gerenciadorCorridas) {
        this.sessao = sessao;
        this.repositorioUsuario = repositorioUsuario;
        this.servicoCadastro = servicoCadastro;
        this.servicoAutenticacao = servicoAutenticacao;
        this.repositorioCorrida = repositorioCorrida;
        this.servicoCorrida = servicoCorrida;
        this.repositorioOferta = repositorioOferta;
        this.repositorioAvaliacao = repositorioAvaliacao;
        this.servicoOferta = servicoOferta;
        this.servicoValidacaoMotorista = servicoValidacaoMotorista;
        this.servicoPagamento = servicoPagamento;
        this.servicoAvaliacao = servicoAvaliacao;
        this.servicoOtimizacaoRota = servicoOtimizacaoRota;
        this.servicoLocalizacao = servicoLocalizacao;
        this.servicoDirecionamento = servicoDirecionamento;
        this.servicoEstimativaChegada = servicoEstimativaChegada;
        this.servicoAdmin = servicoAdmin;
        this.gerenciadorCorridas = gerenciadorCorridas;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public RepositorioUsuario getRepositorioUsuario() {
        return repositorioUsuario;
    }

    public RepositorioCorrida getRepositorioCorrida() {
        return repositorioCorrida;
    }

    public ServicoAvaliacao getServicoAvaliacao() {
        return servicoAvaliacao;
    }

    public RepositorioAvaliacao getRepositorioAvaliacao() {
        return repositorioAvaliacao;
    }
}
