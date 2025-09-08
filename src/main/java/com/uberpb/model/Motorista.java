package com.uberpb.model;

/**
 * Motorista conforme o projeto:
 *   - Construtor: Motorista(String email, String senhaHash)
 *   - Campos: veiculo, cnhValida, crlvValido, contaAtiva
 * Persistência:
 *   "MOTORISTA,email,senhaHash,cnhValida,crlvValido,contaAtiva,veiculoDataOuNull"
 */
public class Motorista extends Usuario {

    private Veiculo veiculo;     // 1 veículo (por enquanto)
    private boolean cnhValida;   // validação de CNH
    private boolean crlvValido;  // validação de CRLV
    private boolean contaAtiva;  // ativado após validações (RF02)

    public Motorista(String email, String senhaHash) {
        super(email, senhaHash);
        this.contaAtiva = false;
        this.cnhValida = false;
        this.crlvValido = false;
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

    @Override
    public String toStringParaPersistencia() {
        StringBuilder sb = new StringBuilder();
        sb.append("MOTORISTA").append(",")
          .append(email).append(",")
          .append(senhaHash).append(",")
          .append(cnhValida).append(",")
          .append(crlvValido).append(",")
          .append(contaAtiva).append(",");
        if (veiculo != null) {
            sb.append(veiculo.toStringParaPersistencia());
        } else {
            sb.append("null");
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
