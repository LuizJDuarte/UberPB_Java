package com.uberpb.model;

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
