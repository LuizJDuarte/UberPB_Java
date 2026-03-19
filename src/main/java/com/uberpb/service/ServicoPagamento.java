package com.uberpb.service;

import com.uberpb.model.Corrida;
import com.uberpb.model.MetodoPagamento;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;

import java.util.HashMap;
import java.util.Map;

public class ServicoPagamento {

    private final Map<String, Corrida> transacoesPendentes = new HashMap<>();

    public ServicoPagamento(RepositorioCorrida rc, RepositorioUsuario ru) {
        // Pode usar futuramente
    }

    /**
     * Processa o pagamento de uma corrida
     */
    public boolean processarPagamento(Corrida corrida, MetodoPagamento metodo) {
        if (corrida == null) {
            throw new IllegalArgumentException("Corrida não pode ser nula");
        }

        System.out.println("💳 PROCESSANDO PAGAMENTO:");
        System.out.println("   Corrida: " + corrida.getId().substring(0, 8));
        System.out.println("   Método: " + metodo.name());

        try {
            boolean sucesso = simularProcessamentoPagamento(metodo);

            if (sucesso) {
                System.out.println("   ✅ Pagamento aprovado!");
                transacoesPendentes.remove(corrida.getId());
                return true;
            } else {
                System.out.println("   ❌ Pagamento recusado!");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Erro no processamento: " + e.getMessage());
            return false;
        }
    }

    /**
     * Simula o processamento de diferentes métodos de pagamento
     */
    private boolean simularProcessamentoPagamento(MetodoPagamento metodo) {
        double taxaSucesso;

        switch (metodo) {
            case CARTAO:
                taxaSucesso = 0.95;
                break;
            case PIX:
                taxaSucesso = 0.98;
                break;
            case PAYPAL:
                taxaSucesso = 0.90;
                break;
            case DINHEIRO:
                taxaSucesso = 1.00;
                break;
            default:
                taxaSucesso = 0.85;
        }

        return Math.random() <= taxaSucesso;
    }

    /**
     * Gerar QR Code PIX (simulado)
     */
    public String gerarQrCodePix(Corrida corrida, double valor) {
        String qrCodeSimulado = String.format(
                "00020126580014BR.GOV.BCB.PIX0136%d5204000053039865406%.2f5802BR5900UBER PB6008JOAO PESSOA62070503***6304%s",
                System.currentTimeMillis(), valor, gerarChecksum(corrida.getId())
        );

        System.out.println("📱 QR CODE PIX GERADO:");
        System.out.println("   Valor: R$ " + valor);
        System.out.println("   QR Code: " + qrCodeSimulado.substring(0, 50) + "...");

        return qrCodeSimulado;
    }

    /**
     * Processar pagamento com cartão (VERSÃO SEGURA)
     */
    public boolean processarCartao(String numeroCartao, String validade, String cvv, double valor) {

        // Validação antes de qualquer substring
        if (numeroCartao == null || !numeroCartao.matches("\\d{16}")) {
            throw new IllegalArgumentException("Número do cartão inválido");
        }

        if (cvv == null || !cvv.matches("\\d{3}")) {
            throw new IllegalArgumentException("CVV inválido");
        }

        if (validade == null || !validade.matches("\\d{2}/\\d{2}")) {
            throw new IllegalArgumentException("Validade inválida");
        }

        //  Agora seguro usar substring
        System.out.println("💳 Processando cartão: "
                + numeroCartao.substring(0, 4)
                + "********"
                + numeroCartao.substring(12));

        System.out.println("   Valor: R$ " + valor);

        return simularProcessamentoPagamento(MetodoPagamento.CARTAO);
    }

    /**
     * Processar pagamento com PayPal
     */
    public boolean processarPayPal(String emailPayPal, double valor) {
        System.out.println("🔵 Processando PayPal: " + emailPayPal);
        System.out.println("   Valor: R$ " + valor);

        if (emailPayPal == null || !emailPayPal.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email do PayPal inválido");
        }

        return simularProcessamentoPagamento(MetodoPagamento.PAYPAL);
    }

    private String gerarChecksum(String input) {
        int hash = input.hashCode();
        return String.format("%04X", Math.abs(hash) % 65536);
    }

    /**
     * Obter detalhes do método de pagamento (SEGURA)
     */
    public String getDetalhesMetodoPagamento(MetodoPagamento metodo) {
        if (metodo == null) {
            return "Método de pagamento";
        }

        switch (metodo) {
            case PIX:
                return "Pagamento instantâneo - Disponível 24h";
            case CARTAO:
                return "Cartão de crédito/débito - Parcelamento disponível";
            case PAYPAL:
                return "PayPal - Pagamento internacional";
            case DINHEIRO:
                return "Pagamento em dinheiro - Ao motorista";
            default:
                return "Método de pagamento";
        }
    }
}