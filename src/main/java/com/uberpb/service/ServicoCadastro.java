package com.uberpb.service;

import com.uberpb.exceptions.EmailJaExistenteException;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Entregador;
import com.uberpb.model.Restaurante;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.repository.RepositorioRestaurante;
import com.uberpb.util.PasswordUtil;

public class ServicoCadastro {

    private RepositorioUsuario repositorioUsuario;
    private RepositorioRestaurante repositorioRestaurante; //

    public ServicoCadastro(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    // Novo construtor ou setter para injetar o repositorio de restaurante
    public void setRepositorioRestaurante(RepositorioRestaurante repositorioRestaurante) {
        this.repositorioRestaurante = repositorioRestaurante;
    }

    public Passageiro cadastrarPassageiro(String email, String senha) {
        if (!PasswordUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
        if (repositorioUsuario.buscarPorEmail(email) != null) {
            throw new EmailJaExistenteException("Este e-mail já está cadastrado.");
        }

        String senhaHash = PasswordUtil.hashPassword(senha);
        Passageiro passageiro = new Passageiro(email, senhaHash);
        repositorioUsuario.salvar(passageiro);
        return passageiro;
    }

    public Motorista cadastrarMotorista(String email, String senha) {
        if (!PasswordUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
        if (repositorioUsuario.buscarPorEmail(email) != null) {
            throw new EmailJaExistenteException("Este e-mail já está cadastrado.");
        }

        String senhaHash = PasswordUtil.hashPassword(senha);
        Motorista motorista = new Motorista(email, senhaHash);
        repositorioUsuario.salvar(motorista);
        return motorista;
    }

    public Entregador cadastrarEntregador(String email, String senha, String cnh, String cpf) {
        if (!PasswordUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
        if (repositorioUsuario.buscarPorEmail(email) != null) {
            throw new com.uberpb.exceptions.EmailJaExistenteException("Este e-mail já está cadastrado.");
        }

        String senhaHash = PasswordUtil.hashPassword(senha);
        Entregador entregador = new Entregador(email, senhaHash);
        entregador.setCnhNumero(cnh != null ? cnh : "");
        entregador.setCpfNumero(cpf != null ? cpf : "");
        entregador.setCnhValida(false);
        entregador.setDocIdentidadeValido(false);
        entregador.setContaAtiva(true); // Ativo por padrão (similar ao restaurante)

        repositorioUsuario.salvar(entregador);
        return entregador;
    }

    public Restaurante cadastrarRestaurante(String email, String senha, String nomeFantasia, String cnpj) {
        if (!PasswordUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
        if (repositorioUsuario.buscarPorEmail(email) != null) {
            throw new com.uberpb.exceptions.EmailJaExistenteException("Este e-mail já está cadastrado.");
        }

        String senhaHash = PasswordUtil.hashPassword(senha);
        Restaurante restaurante = new Restaurante(email, senhaHash);
        restaurante.setNomeFantasia(nomeFantasia != null ? nomeFantasia : "");
        restaurante.setCnpj(cnpj != null ? cnpj : "");
        restaurante.setContaAtiva(true);

        // SALVA EM AMBOS OS REPOSITÓRIOS
        repositorioUsuario.salvar(restaurante);
        if (repositorioRestaurante != null) {
            repositorioRestaurante.salvar(restaurante);
        }

        return restaurante;
    }
}