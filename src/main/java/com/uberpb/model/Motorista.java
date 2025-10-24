package com.uberpb.model;

/**
 * Motorista conforme o projeto:
 *   - Construtor: Motorista(String email, String senhaHash)
 *   - Campos: veiculo, cnhValida, crlvValido, contaAtiva
 * Persistência:
 *   "MOTORISTA,email,senhaHash,cnhValida,crlvValido,contaAtiva,ratingMedio,totalAvaliacoes,veiculoDataOuNull"
 */
public class Motorista extends Usuario {

    private Veiculo veiculo;     // 1 veículo (por enquanto)
    private boolean cnhValida;   // validação de CNH
    private boolean crlvValido;  // validação de CRLV
    private boolean contaAtiva;  // ativado após validações (RF02)
    private boolean disponivel;  // se está online para receber corridas

    private double ratingMedio = 0.0;
    private int totalAvaliacoes = 0;

    public Motorista(String email, String senhaHash) {
        super(email, senhaHash);
        this.contaAtiva = false;
        this.cnhValida = false;
        this.crlvValido = false;
        this.disponivel = false; // Começa offline por padrão
    }

    // Getters/Setters usados por serviços e repositório
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public boolean isCnhValida() { return cnhValida; }
    public void setCnhValida(boolean cnhValida) { this.cnhValida = cnhValida; }

    public boolean isCrlvValido() { return crlvValido; }
    public void setCrlvValido(boolean crlvValido) { this.crlvValido = crlvValido; }

    public boolean isContaAtiva() { return contaAtiva; }
    public void setContaAtiva(boolean contaAtiva) { this.contaAtiva = contaAtiva; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

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
        StringBuilder sb = new StringBuilder();
        sb.append("MOTORISTA").append(",")
          .append(email).append(",")
          .append(senhaHash).append(",")
          .append(cnhValida).append(",")
          .append(crlvValido).append(",")
          .append(contaAtiva).append(",")
          .append(ratingMedio).append(",")
          .append(totalAvaliacoes).append(",")
          .append(disponivel).append(","); // Novo campo
        if (veiculo != null) {
            sb.append(veiculo.toStringParaPersistencia());
        } else {
            sb.append("null"); // Apenas 'null', sem vírgula antes
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String statusConta = contaAtiva ? "Ativa" : "Inativa";
        String statusOnline = disponivel ? "Online" : "Offline";
        String ratingInfo = totalAvaliacoes > 0 ?
            String.format("⭐ %.1f (%d avaliações)", ratingMedio, totalAvaliacoes) :
            "⭐ Sem avaliações";

        return "Motorista - " + super.toString() +
               ", Status: " + statusConta + " | " + statusOnline +
               ", CNH: " + (cnhValida ? "OK" : "Pendente") +
               ", CRLV: " + (crlvValido ? "OK" : "Pendente") +
               ", " + ratingInfo +
               (veiculo != null ? ", " + veiculo.toString() : ", Sem veículo");
    }
}