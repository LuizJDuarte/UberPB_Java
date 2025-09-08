package com.uberpb.model;

public abstract class Usuario {
    protected String email;
    protected String senhaHash; // Senha já hashed

    // Construtor
    public Usuario(String email, String senhaHash) {
        this.email = email;
        this.senhaHash = senhaHash;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    // Setters (se necessário, mas para email/senha pode ser só no construtor)
    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    // Métodos abstratos ou comuns
    public abstract String toStringParaPersistencia(); // Método para formatar para arquivo

    @Override
    public String toString() {
        return "Email: " + email;
    }
}
