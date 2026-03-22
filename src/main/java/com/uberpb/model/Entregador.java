package com.uberpb.model;

/**
 * Entregador: usuário que faz entregas (ex.: motoboy). Similar ao Motorista,
 * mas sem veículo obrigatório aqui; possui validação de documentos.
 */
public class Entregador extends Usuario {

    private String cnhNumero;     // número da CNH
    private String cpfNumero;     // número do CPF
    private boolean cnhValida;    // caso aplicável
    private boolean docIdentidadeValido;
    private boolean contaAtiva;

    public Entregador(String email, String senhaHash) {
        super(email, senhaHash);
        this.cnhNumero = "";
        this.cpfNumero = "";
        this.cnhValida = false;
        this.docIdentidadeValido = false;
        this.contaAtiva = false;
    }

    public String getCnhNumero() { return cnhNumero; }
    public void setCnhNumero(String cnhNumero) { this.cnhNumero = cnhNumero; }

    public String getCpfNumero() { return cpfNumero; }
    public void setCpfNumero(String cpfNumero) { this.cpfNumero = cpfNumero; }

    public boolean isCnhValida() { return cnhValida; }
    public void setCnhValida(boolean cnhValida) { this.cnhValida = cnhValida; }

    public boolean isDocIdentidadeValido() { return docIdentidadeValido; }
    public void setDocIdentidadeValido(boolean docIdentidadeValido) { this.docIdentidadeValido = docIdentidadeValido; }

    public boolean isContaAtiva() { return contaAtiva; }
    public void setContaAtiva(boolean contaAtiva) { this.contaAtiva = contaAtiva; }

    @Override
    public String toStringParaPersistencia() {
        StringBuilder sb = new StringBuilder();
        sb.append("ENTREGADOR").append(",").append(email).append(",").append(senhaHash)
          .append(",").append(cnhNumero).append(",").append(cpfNumero)
          .append(",").append(cnhValida).append(",").append(docIdentidadeValido).append(",").append(contaAtiva);
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Entregador - " + super.toString() + ", Conta: " + (contaAtiva?"Ativa":"Inativa") +
               ", CNH: " + cnhNumero + " (" + (cnhValida?"OK":"Pendente") + ")" +
               ", CPF: " + cpfNumero + " (" + (docIdentidadeValido?"OK":"Pendente") + ")";
    }
}
