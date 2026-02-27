package com.uberpb.model;

import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private String emailCliente;
    private String emailRestaurante;
    private String entregadorAlocado; // Email do entregador responsável pela entrega (RF22)
    private List<ItemCarrinho> itens;
    private double total;
    private String formaPagamento;
    private String status;
    private TipoPedido tipoPedido;
    private AgendamentoPedido agendamento;

    public Pedido(String emailCliente,
            String emailRestaurante,
            List<ItemCarrinho> itens,
            double total,
            String formaPagamento) {

        this.emailCliente = emailCliente;
        this.emailRestaurante = emailRestaurante;
        this.entregadorAlocado = null;
        this.itens = itens;
        this.total = total;
        this.formaPagamento = formaPagamento;
        this.status = "CRIADO";
        this.tipoPedido = TipoPedido.IMEDIATO;
        this.agendamento = null;
    }

    public Pedido(String emailCliente,
            String emailRestaurante,
            List<ItemCarrinho> itens,
            double total,
            String formaPagamento,
            TipoPedido tipoPedido,
            AgendamentoPedido agendamento) {

        this.emailCliente = emailCliente;
        this.emailRestaurante = emailRestaurante;
        this.itens = itens;
        this.total = total;
        this.formaPagamento = formaPagamento;
        this.status = "CRIADO";
        this.tipoPedido = tipoPedido;
        this.agendamento = agendamento;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public String getEmailRestaurante() {
        return emailRestaurante;
    }

    public String getEntregadorAlocado() {
        return entregadorAlocado;
    }

    public void setEntregadorAlocado(String entregadorAlocado) {
        this.entregadorAlocado = entregadorAlocado;
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

    public TipoPedido getTipoPedido() {
        return tipoPedido;
    }

    public void setTipoPedido(TipoPedido tipoPedido) {
        this.tipoPedido = tipoPedido;
    }

    public AgendamentoPedido getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(AgendamentoPedido agendamento) {
        this.agendamento = agendamento;
    }

    public String toStringParaPersistencia() {
        StringBuilder itensStr = new StringBuilder();

        for (ItemCarrinho ic : itens) {
            itensStr.append(ic.getItem().getNome())
                    .append(":")
                    .append(ic.getQuantidade())
                    .append(";");
        }

        String agendamentoStr = agendamento != null ? agendamento.formatarParaPersistencia() : "";
        String entregadorStr = entregadorAlocado != null ? entregadorAlocado : "";

        return emailCliente + "," +
                emailRestaurante + "," +
                itensStr + "," +
                total + "," +
                formaPagamento + "," +
                status + "," +
                tipoPedido.name() + "," +
                agendamentoStr + "," +
                entregadorStr;
    }

    public static Pedido fromString(String linha) {
        try {
            String[] parts = linha.split(",", -1);
            if (parts.length < 6)
                return null;

            String emailCliente = parts[0];
            String emailRestaurante = parts[1];
            double total = Double.parseDouble(parts[3]);
            String formaPagamento = parts[4];
            String status = parts[5];

            TipoPedido tipoPedido = TipoPedido.IMEDIATO;
            AgendamentoPedido agendamento = null;
            String entregadorAlocado = null;

            // Se houver dados antigos (apenas 6 campos), usar defaults
            if (parts.length >= 7) {
                try {
                    tipoPedido = TipoPedido.valueOf(parts[6]);
                } catch (IllegalArgumentException e) {
                    tipoPedido = TipoPedido.IMEDIATO;
                }
            }

            if (parts.length >= 8 && !parts[7].isEmpty()) {
                agendamento = AgendamentoPedido.fromString(parts[7]);
            }

            if (parts.length >= 9 && !parts[8].isEmpty()) {
                entregadorAlocado = parts[8];
            }

            Pedido p = new Pedido(
                    emailCliente,
                    emailRestaurante,
                    new ArrayList<>(),
                    total,
                    formaPagamento,
                    tipoPedido,
                    agendamento);

            p.setStatus(status);
            p.setEntregadorAlocado(entregadorAlocado);
            return p;

        } catch (Exception e) {
            return null;
        }
    }

}
