package com.uberpb.model;

/**
 * Mantém exatamente a assinatura usada no ServicoCadastro:
 *   new Passageiro(email, senhaHash)
 * Persistência: "PASSAGEIRO,email,senhaHash,ratingMedio,totalAvaliacoes"
 */
public class Passageiro extends Usuario {

    private double ratingMedio = 0.0;
    private int totalAvaliacoes = 0;

    public Passageiro(String email, String senhaHash) {
        super(email, senhaHash);
    }
    
    public double getRatingMedio() { return ratingMedio; }
    public void setRatingMedio(double ratingMedio) { 
        this.ratingMedio = Math.round(ratingMedio * 10.0) / 10.0;
    }
    
    public int getTotalAvaliacoes() { return totalAvaliacoes; }
    public void setTotalAvaliacoes(int totalAvaliacoes) { 
        this.totalAvaliacoes = totalAvaliacoes; 
    }

    public void adicionarAvaliacao(int novaAvaliacao) {
        double somaTotal = this.ratingMedio * this.totalAvaliacoes + novaAvaliacao;
        this.totalAvaliacoes++;
        this.ratingMedio = Math.round((somaTotal / this.totalAvaliacoes) * 10.0) / 10.0;
    }

    @Override
    public String toStringParaPersistencia() {
        return "PASSAGEIRO," + email + "," + senhaHash + "," + ratingMedio + "," + totalAvaliacoes;
    }

    @Override
    public String toString() {
        String ratingInfo = totalAvaliacoes > 0 ? 
            String.format("⭐ %.1f (%d avaliações)", ratingMedio, totalAvaliacoes) : 
            "⭐ Sem avaliações";
            
        return "Passageiro - " + super.toString() + ", " + ratingInfo;
    }
}