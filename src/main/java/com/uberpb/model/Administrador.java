package com.uberpb.model;

public class Administrador extends Usuario {

    public Administrador(String email, String senhaHash) {
        super(email, senhaHash);
    }

    @Override
    public String toStringParaPersistencia() {
        return "ADMIN," + email + "," + senhaHash;
    }

    @Override
    public String toString() {
        return "[ADMIN] " + email;
    }
}
