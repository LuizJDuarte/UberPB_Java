package com.uberpb.model;

/**
 * Mantém exatamente a assinatura usada no ServicoCadastro:
 *   new Passageiro(email, senhaHash)
 * Persistência: "PASSAGEIRO,email,senhaHash"
 */
public class Passageiro extends Usuario {

    public Passageiro(String email, String senhaHash) {
        super(email, senhaHash);
    }

    @Override
    public String toStringParaPersistencia() {
        return "PASSAGEIRO," + email + "," + senhaHash;
    }

    @Override
    public String toString() {
        return "Passageiro - " + super.toString();
    }
}
