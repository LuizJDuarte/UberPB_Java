package com.uberpb.app;

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
    public final ServicoOferta servicoOferta;

    public final ServicoValidacaoMotorista servicoValidacaoMotorista;

    // SERVIÇOS PARA DIRECIONAMENTO E LOCALIZAÇÃO
    public final ServicoDirecionamentoCorrida servicoDirecionamentoCorrida;
    public final ServicoLocalizacao servicoLocalizacao;

    // SERVIÇO PARA OTIMIZAÇÃO DE ROTA
    public final ServicoOtimizacaoRota servicoOtimizacaoRota;

    // SERVIÇO DE AVALIAÇÃO
    public final ServicoAvaliacao servicoAvaliacao;

    // ✅ NOVO SERVIÇO DE PAGAMENTO
    public final ServicoPagamento servicoPagamento;

    public ContextoAplicacao(Sessao sessao,
                             RepositorioUsuario repositorioUsuario,
                             ServicoCadastro servicoCadastro,
                             ServicoAutenticacao servicoAutenticacao,
                             RepositorioCorrida repositorioCorrida,
                             ServicoCorrida servicoCorrida,
                             RepositorioOferta repositorioOferta,
                             ServicoOferta servicoOferta,
                             ServicoValidacaoMotorista servicoValidacaoMotorista,
                             ServicoDirecionamentoCorrida servicoDirecionamentoCorrida,
                             ServicoLocalizacao servicoLocalizacao,
                             ServicoOtimizacaoRota servicoOtimizacaoRota,
                             ServicoAvaliacao servicoAvaliacao,
                             ServicoPagamento servicoPagamento) { // ✅ NOVO PARÂMETRO
        this.sessao = sessao;
        this.repositorioUsuario = repositorioUsuario;
        this.servicoCadastro = servicoCadastro;
        this.servicoAutenticacao = servicoAutenticacao;
        this.repositorioCorrida = repositorioCorrida;
        this.servicoCorrida = servicoCorrida;
        this.repositorioOferta = repositorioOferta;
        this.servicoOferta = servicoOferta;
        this.servicoValidacaoMotorista = servicoValidacaoMotorista;
        this.servicoDirecionamentoCorrida = servicoDirecionamentoCorrida;
        this.servicoLocalizacao = servicoLocalizacao;
        this.servicoOtimizacaoRota = servicoOtimizacaoRota;
        this.servicoAvaliacao = servicoAvaliacao;
        this.servicoPagamento = servicoPagamento; // ✅ NOVA INICIALIZAÇÃO
    }
}