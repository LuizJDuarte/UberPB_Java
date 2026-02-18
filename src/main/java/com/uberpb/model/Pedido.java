package com.uberpb.model;

import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private String emailCliente;
    private String emailRestaurante;
    private List<ItemCarrinho> itens;
    private double total;
    private String formaPagamento;
    private String status;

    public Pedido(String emailCliente,
            String emailRestaurante,
            List<ItemCarrinho> itens,
            double total,
            String formaPagamento) {

        this.emailCliente = emailCliente;
        this.emailRestaurante = emailRestaurante;
        this.itens = itens;
        this.total = total;
        this.formaPagamento = formaPagamento;
        this.status = "CRIADO";
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public String getEmailRestaurante() {
        return emailRestaurante;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public double getTotal() {
        return total;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toStringParaPersistencia() {
        StringBuilder itensStr = new StringBuilder();

        for (ItemCarrinho ic : itens) {
            itensStr.append(ic.getItem().getNome())
                    .append(":")
                    .append(ic.getQuantidade())
                    .append(";");
        }

        return emailCliente + "," +
                emailRestaurante + "," +
                itensStr + "," +
                total + "," +
                formaPagamento + "," +
                status;
    }

   public static Pedido fromString(String linha) {
    try {
        String[] parts = linha.split(",", -1);
        if (parts.length < 6) return null;

        String emailCliente = parts[0];
        String emailRestaurante = parts[1];
        double total = Double.parseDouble(parts[3]);
        String formaPagamento = parts[4];
        String status = parts[5];

        Pedido p = new Pedido(
            emailCliente,
            emailRestaurante,
            new ArrayList<>(),
            total,
            formaPagamento
        );

        p.setStatus(status);
        return p;

    } catch (Exception e) {
        return null;
    }
}


}
