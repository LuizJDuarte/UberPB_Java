package com.uberpb.app;

import com.uberpb.repository.*;
import com.uberpb.service.*;

public class ContextoAplicacao {

    public final Sessao sessao;

    public final RepositorioUsuario repositorioUsuario;
    public final ServicoCadastro servicoCadastro;
    public final ServicoAutenticacao servicoAutenticacao;

    public final RepositorioCorrida repositorioCorrida;
    public final ServicoCorrida servicoCorrida;

    public final RepositorioRestaurante repositorioRestaurante;

    public final RepositorioOferta repositorioOferta;
    public final RepositorioAvaliacao repositorioAvaliacao;
    public final ServicoOferta servicoOferta;

    public final ServicoValidacaoMotorista servicoValidacaoMotorista;
    public final ServicoPagamento servicoPagamento;
    public final ServicoAvaliacao servicoAvaliacao;

    public final ServicoOtimizacaoRota servicoOtimizacaoRota;
    public final ServicoLocalizacao servicoLocalizacao;
    public final ServicoDirecionamentoCorrida servicoDirecionamento;
    public final EstimativaChegada servicoEstimativaChegada;

    public final ServicoAdmin servicoAdmin;
    public final GerenciadorCorridasAtivas gerenciadorCorridas;

    // Carrinho
    public final ServicoCarrinho servicoCarrinho;

    // NOVO — PEDIDOS
    public final RepositorioPedido repositorioPedido;
    public final ServicoPedido servicoPedido;

    // NOVO — RF22: NOTIFICAÇÕES E ENTREGAS
    public final RepositorioNotificacao repositorioNotificacao;
    public final ServicoNotificacao servicoNotificacao;
    public final ServicoEntrega servicoEntrega;

    // CONSTRUTOR VAZIO
    public ContextoAplicacao() {
        this.sessao = null;
        this.repositorioUsuario = null;
        this.servicoCadastro = null;
        this.servicoAutenticacao = null;
        this.repositorioCorrida = null;
        this.servicoCorrida = null;
        this.repositorioRestaurante = null;
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
        this.servicoCarrinho = null;
        this.repositorioPedido = null;
        this.servicoPedido = null;
        this.repositorioNotificacao = null;
        this.servicoNotificacao = null;
        this.servicoEntrega = null;
    }

    // CONSTRUTOR COMPLETO
    public ContextoAplicacao(
            Sessao sessao,
            RepositorioUsuario repositorioUsuario,
            RepositorioRestaurante repositorioRestaurante,
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
            GerenciadorCorridasAtivas gerenciadorCorridas,
            ServicoCarrinho servicoCarrinho,

            // 👇 NOVOS
            RepositorioPedido repositorioPedido,
            ServicoPedido servicoPedido,

            // 👇 RF22: NOTIFICAÇÕES E ENTREGAS
            RepositorioNotificacao repositorioNotificacao,
            ServicoNotificacao servicoNotificacao,
            ServicoEntrega servicoEntrega) {
        this.sessao = sessao;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioRestaurante = repositorioRestaurante;
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
        this.servicoCarrinho = servicoCarrinho;

        // NOVOS
        this.repositorioPedido = repositorioPedido;
        this.servicoPedido = servicoPedido;

        // RF22: NOTIFICAÇÕES E ENTREGAS
        this.repositorioNotificacao = repositorioNotificacao;
        this.servicoNotificacao = servicoNotificacao;
        this.servicoEntrega = servicoEntrega;
    }

    // GETTERS
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

    // NOVOS GETTERS
    public RepositorioPedido getRepositorioPedido() {
        return repositorioPedido;
    }

    public ServicoPedido getServicoPedido() {
        return servicoPedido;
    }

    // RF22 GETTERS
    public RepositorioNotificacao getRepositorioNotificacao() {
        return repositorioNotificacao;
    }

    public ServicoNotificacao getServicoNotificacao() {
        return servicoNotificacao;
    }

    public ServicoEntrega getServicoEntrega() {
        return servicoEntrega;
    }
}
