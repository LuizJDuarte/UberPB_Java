package com.uberpb.service;

import com.uberpb.model.Corrida;
import com.uberpb.model.MetodoPagamento;
import java.util.HashMap;
import java.util.Map;

public class ServicoPagamento {
    
    private final Map<String, Corrida> transacoesPendentes = new HashMap<>();
    
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
            // Simular processamento do pagamento
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
        // Simular diferentes taxas de sucesso baseadas no método
        double taxaSucesso;
        
        switch (metodo) {
            case CARTAO:
                taxaSucesso = 0.95; // 95% de sucesso
                break;
            case PIX:
                taxaSucesso = 0.98; // 98% de sucesso
                break;
            case PAYPAL:
                taxaSucesso = 0.90; // 90% de sucesso
                break;
            case DINHEIRO:
                taxaSucesso = 1.00; // 100% de sucesso (pagamento em dinheiro)
                break;
            default:
                taxaSucesso = 0.85;
        }
        
        // Simular processamento com base na taxa de sucesso
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
     * Processar pagamento com cartão
     */
    public boolean processarCartao(String numeroCartao, String validade, String cvv, double valor) {
        System.out.println("💳 Processando cartão: " + numeroCartao.substring(0, 4) + "********" + numeroCartao.substring(12));
        System.out.println("   Valor: R$ " + valor);
        
        // Validações básicas
        if (numeroCartao == null || numeroCartao.length() != 16) {
            throw new IllegalArgumentException("Número do cartão inválido");
        }
        if (cvv == null || cvv.length() != 3) {
            throw new IllegalArgumentException("CVV inválido");
        }
        
        return simularProcessamentoPagamento(MetodoPagamento.CARTAO);
    }
    
    /**
     * Processar pagamento com PayPal
     */
    public boolean processarPayPal(String emailPayPal, double valor) {
        System.out.println("🔵 Processando PayPal: " + emailPayPal);
        System.out.println("   Valor: R$ " + valor);
        
        if (emailPayPal == null || !emailPayPal.contains("@")) {
            throw new IllegalArgumentException("Email do PayPal inválido");
        }
        
        return simularProcessamentoPagamento(MetodoPagamento.PAYPAL);
    }
    
    private String gerarChecksum(String input) {
        int hash = input.hashCode();
        return String.format("%04X", Math.abs(hash) % 65536);
    }
    
    /**
     * Obter detalhes do método de pagamento
     */
    public String getDetalhesMetodoPagamento(MetodoPagamento metodo) {
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