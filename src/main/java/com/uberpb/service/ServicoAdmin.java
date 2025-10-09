package com.uberpb.service;

import com.uberpb.exceptions.OperacaoNaoPermitidaException;
import com.uberpb.model.Administrador;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

import java.util.List;

public class ServicoAdmin {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioCorrida repositorioCorrida;

    public ServicoAdmin(RepositorioUsuario rUsuario, RepositorioCorrida rCorrida) {
        this.repositorioUsuario = rUsuario;
        this.repositorioCorrida = rCorrida;
    }

    public boolean ehAdmin(Usuario u) {
        return u instanceof Administrador;
    }

    public List<Usuario> listarUsuarios() {
        return repositorioUsuario.buscarTodos();
    }

    public void removerUsuario(String email, Usuario solicitante) {
        if (!ehAdmin(solicitante)) throw new OperacaoNaoPermitidaException("Apenas administrador pode remover usuário.");
        repositorioUsuario.remover(email);
    }

    public void removerCorrida(String corridaId, Usuario solicitante) {
        if (!ehAdmin(solicitante)) throw new OperacaoNaoPermitidaException("Apenas administrador pode remover corrida.");
        var c = repositorioCorrida.buscarPorId(corridaId);
        if (c != null) {
            repositorioCorrida.remover(corridaId);
        }
    }
}
