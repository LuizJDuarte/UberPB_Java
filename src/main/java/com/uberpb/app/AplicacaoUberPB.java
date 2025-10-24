package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.util.PasswordUtil;

/** Ponto de entrada. Sem 'new' aqui — só delega. */
public class AplicacaoUberPB {

    public static void main(String[] args) {
        inicializarAdmin();
        AplicacaoCLI.executar();
    }
     private static void inicializarAdmin() {
        RepositorioUsuario repositorioUsuario = ImplRepositorioUsuarioArquivo.getInstance();
        if (repositorioUsuario.buscarPorEmail("admin@uberpb.com") == null) {
            String senhaHasheada = PasswordUtil.hashPassword("admin123");
            Administrador admin = new Administrador("admin@uberpb.com", senhaHasheada);
            repositorioUsuario.salvar(admin);
            System.out.println("Usuário administrador padrão criado.");
        }
    }
}
