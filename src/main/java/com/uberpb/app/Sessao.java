package com.uberpb.app;

import com.uberpb.model.Usuario;

/**
 * Mantém o usuário atualmente logado no sistema (ou null se ninguém logado).
 * Não altera suas classes de model; apenas referencia o objeto Usuario.
 */
public class Sessao {

    private Usuario usuarioAtual;

    public boolean estaLogado() {
        return usuarioAtual != null;
    }

    public void logar(Usuario usuario) {
        this.usuarioAtual = usuario;
    }

    public void deslogar() {
        this.usuarioAtual = null;
    }

    public Usuario getUsuarioAtual() {
        return usuarioAtual;
    }
}
