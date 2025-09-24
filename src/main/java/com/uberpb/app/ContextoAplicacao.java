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

    // NOVOS SERVIÇOS PARA DIRECIONAMENTO E LOCALIZAÇÃO
    public final ServicoDirecionamentoCorrida servicoDirecionamentoCorrida;
    public final ServicoLocalizacao servicoLocalizacao;

    public ContextoAplicacao(Sessao sessao,
                             RepositorioUsuario repositorioUsuario,
                             ServicoCadastro servicoCadastro,
                             ServicoAutenticacao servicoAutenticacao,
                             RepositorioCorrida repositorioCorrida,
                             ServicoCorrida servicoCorrida,
                             RepositorioOferta repositorioOferta,
                             ServicoOferta servicoOferta,
                             ServicoValidacaoMotorista servicoValidacaoMotorista,
                             ServicoDirecionamentoCorrida servicoDirecionamentoCorrida, // NOVO
                             ServicoLocalizacao servicoLocalizacao) { // NOVO
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
    }
}