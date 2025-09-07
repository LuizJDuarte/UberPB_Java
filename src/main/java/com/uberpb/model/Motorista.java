package com.uberpb.model;

import java.util.ArrayList;
import java.util.List;

public class Motorista extends Usuario {
    private Veiculo veiculo; // Um motorista tem 1 veículo no momento
    private boolean cnhValida; // Simula a validação da CNH
    private boolean crlvValido; // Simula a validação do CRLV
    private boolean contaAtiva; // Se passou por toda a validação RF02

    public Motorista(String email, String senhaHash) {
        super(email, senhaHash);
        this.contaAtiva = false; // Por padrão, a conta do motorista não está ativa até ser validada
        this.cnhValida = false;
        this.crlvValido = false;
    }

    // Getters e Setters
    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public boolean isCnhValida() {
        return cnhValida;
    }

    public void setCnhValida(boolean cnhValida) {
        this.cnhValida = cnhValida;
    }

    public boolean isCrlvValido() {
        return crlvValido;
    }

    public void setCrlvValido(boolean crlvValido) {
        this.crlvValido = crlvValido;
    }

    public boolean isContaAtiva() {
        return contaAtiva;
    }

    public void setContaAtiva(boolean contaAtiva) {
        this.contaAtiva = contaAtiva;
    }

    @Override
    public String toStringParaPersistencia() {
        // Formato: MOTORISTA,email,senhaHash,cnhValida,crlvValido,contaAtiva,veiculoData
        StringBuilder sb = new StringBuilder();
        sb.append("MOTORISTA,").append(email).append(",")
          .append(senhaHash).append(",")
          .append(cnhValida).append(",")
          .append(crlvValido).append(",")
          .append(contaAtiva).append(",");
        
        if (veiculo != null) {
            sb.append(veiculo.toStringParaPersistencia());
        } else {
            sb.append("null"); // Indica que não há veículo cadastrado
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String status = contaAtiva ? "Ativa" : "Inativa";
        return "Motorista - " + super.toString() +
               ", CNH Válida: " + cnhValida +
               ", CRLV Válido: " + crlvValido +
               ", Status: " + status +
               (veiculo != null ? ", " + veiculo.toString() : ", Sem veículo cadastrado");
    }
}