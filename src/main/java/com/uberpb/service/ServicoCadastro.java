package com.uberpb.service;

import com.uberpb.exceptions.EmailJaExistenteException;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.util.PasswordUtil;

public class ServicoCadastro {

    private RepositorioUsuario repositorioUsuario;

    public ServicoCadastro(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Cadastra um novo passageiro no sistema.
     * @param email O email do passageiro.
     * @param senha A senha em texto claro.
     * @return O objeto Passageiro recém-cadastrado.
     * @throws EmailJaExistenteException Se o email já estiver em uso.
     */
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

    /**
     * Cadastra um novo motorista no sistema. A conta inicial do motorista estará inativa
     * até que seus documentos e veículo sejam validados (RF02).
     * @param email O email do motorista.
     * @param senha A senha em texto claro.
     * @return O objeto Motorista recém-cadastrado.
     * @throws EmailJaExistenteException Se o email já estiver em uso.
     */
    public Motorista cadastrarMotorista(String email, String senha) {
        if (!PasswordUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
        if (repositorioUsuario.buscarPorEmail(email) != null) {
            throw new EmailJaExistenteException("Este e-mail já está cadastrado.");
        }

        String senhaHash = PasswordUtil.hashPassword(senha);
        Motorista motorista = new Motorista(email, senhaHash);
        // Por padrão, a conta do motorista é inativa até que a validação de documentos seja completa.
        repositorioUsuario.salvar(motorista);
        return motorista;
    }
}
