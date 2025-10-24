package com.uberpb.service;

import com.uberpb.exceptions.CredenciaisInvalidasException;
import com.uberpb.exceptions.UsuarioNaoEncontradoException;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.util.PasswordUtil;

public class ServicoAutenticacao {

    private RepositorioUsuario repositorioUsuario;

    public ServicoAutenticacao(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Autentica um usuário no sistema.
     * @param email O email do usuário.
     * @param senha A senha em texto claro.
     * @return O objeto Usuario autenticado (Passageiro ou Motorista).
     * @throws UsuarioNaoEncontradoException Se o e-mail não estiver cadastrado.
     * @throws CredenciaisInvalidasException Se a senha estiver incorreta.
     */
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = repositorioUsuario.buscarPorEmail(email);

        if (usuario == null) {
            throw new UsuarioNaoEncontradoException("E-mail não cadastrado.");
        }

        String senhaHashFornecida = PasswordUtil.hashPassword(senha);
        if (!usuario.getSenhaHash().equals(senhaHashFornecida)) {
            throw new CredenciaisInvalidasException("Senha incorreta.");
        }

        // RF02 & RF-Admin: Verifica se a conta do usuário está ativa antes de permitir o login.
        boolean isAtivo = true;
        if (usuario instanceof com.uberpb.model.Motorista motorista) {
            isAtivo = motorista.isContaAtiva();
        } else if (usuario instanceof com.uberpb.model.Passageiro passageiro) {
            isAtivo = passageiro.isContaAtiva();
        }

        if (!isAtivo) {
            throw new CredenciaisInvalidasException("Esta conta está desativada ou pendente de aprovação.");
        }

        return usuario; // Retorna o usuário autenticado
    }
}
