package com.uberpb.model;

/**
 * Modelo simples para Restaurante - estabelecimento que recebe pedidos.
 */
public class Restaurante extends Usuario {

    private String nomeFantasia;
    private String cnpj;
    private boolean contaAtiva;

    public Restaurante(String email, String senhaHash) {
        super(email, senhaHash);
        this.nomeFantasia = "";
        this.cnpj = "";
        this.contaAtiva = false;
    }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public boolean isContaAtiva() { return contaAtiva; }
    public void setContaAtiva(boolean contaAtiva) { this.contaAtiva = contaAtiva; }

    @Override
    public String toStringParaPersistencia() {
        StringBuilder sb = new StringBuilder();
        sb.append("RESTAURANTE").append(",").append(email).append(",").append(senhaHash)
          .append(",").append(nomeFantasia).append(",").append(cnpj).append(",").append(contaAtiva);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Restaurante - " + super.toString() + ", Nome: " + nomeFantasia + ", CNPJ: " + cnpj + " (" + (contaAtiva?"Ativa":"Inativa") + ")";
    }
}
