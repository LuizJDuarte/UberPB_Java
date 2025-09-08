package com.uberpb.app;

import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.ServicoAutenticacao;
import com.uberpb.service.ServicoCadastro;
import com.uberpb.service.ServicoCorrida;

public class ContextoAplicacao {
    public final Sessao sessao;
    public final RepositorioUsuario repositorioUsuario;
    public final ServicoCadastro servicoCadastro;
    public final ServicoAutenticacao servicoAutenticacao;

    // RF04
    public final RepositorioCorrida repositorioCorrida;
    public final ServicoCorrida servicoCorrida;

    public ContextoAplicacao(Sessao sessao,
                             RepositorioUsuario repositorioUsuario,
                             ServicoCadastro servicoCadastro,
                             ServicoAutenticacao servicoAutenticacao,
                             RepositorioCorrida repositorioCorrida,
                             ServicoCorrida servicoCorrida) {
        this.sessao = sessao;
        this.repositorioUsuario = repositorioUsuario;
        this.servicoCadastro = servicoCadastro;
        this.servicoAutenticacao = servicoAutenticacao;
        this.repositorioCorrida = repositorioCorrida;
        this.servicoCorrida = servicoCorrida;
    }
}
