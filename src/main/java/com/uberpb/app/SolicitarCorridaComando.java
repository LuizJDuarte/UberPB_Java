package com.uberpb.app;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.MetodoPagamento;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.service.EstimativaCorrida;

import java.util.Scanner;

public class SolicitarCorridaComando implements Comando {

    @Override public String nome() { return "Solicitar Corrida (informar endereços)"; }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.println("Informe os endereços. Ex.: \"Av. Epitácio Pessoa, 1000 - Tambaú\".");
        System.out.print("Endereço de ORIGEM: ");
        String origem = entrada.nextLine();
        System.out.print("Endereço de DESTINO: ");
        String destino = entrada.nextLine();

        // Categoria (RF06)
        CategoriaVeiculo[] cats = CategoriaVeiculo.values();
        System.out.println("Escolha a categoria (digite o número):");
        for (int i = 0; i < cats.length; i++) System.out.printf("%d) %s%n", i+1, cats[i].name());
        System.out.print("> ");
        String s = entrada.nextLine().trim();
        int idx = 0;
        try { idx = Integer.parseInt(s) - 1; } catch (Exception e) { idx = 0; }
        if (idx < 0 || idx >= cats.length) idx = 0;
        CategoriaVeiculo categoriaEscolhida = cats[idx];

        // Metodo de Pagamento
        System.out.println("Escolha o método de pagamento (digite o número):");
        MetodoPagamento[] metodos = MetodoPagamento.values();
        for (int i = 0; i < metodos.length; i++) {
            System.out.printf("%d) %s%n", i + 1, metodos[i].name());
        }
        System.out.print("> ");
        String sPagamento = entrada.nextLine().trim();
        int idxPagamento;
        try {
            idxPagamento = Integer.parseInt(sPagamento) - 1;
        } catch (Exception e) {
            idxPagamento = 3; // Default DINHEIRO
        }
        if (idxPagamento < 0 || idxPagamento >= metodos.length) {
            idxPagamento = 3;
        }
        MetodoPagamento metodoEscolhido = metodos[idxPagamento];

        // Estimativa (RF05)
        EstimativaCorrida est = contexto.servicoCorrida.estimarPorEnderecos(origem, destino, categoriaEscolhida);
        System.out.printf("Estimativa: %.1f km • %d min • R$ %.2f%n",
                est.getDistanciaKm(), est.getMinutos(), est.getPreco());

        // Mostrar detalhes do cálculo
        String detalhesPreco = com.uberpb.service.CalculadoraPrecoCorrida.gerarDetalhesPreco(
            est.getDistanciaKm(), est.getMinutos(), categoriaEscolhida);
        System.out.println("\n" + detalhesPreco);

        System.out.print("Confirmar solicitação? (s/N): ");
        String conf = entrada.nextLine().trim();
        if (!conf.equalsIgnoreCase("s")) {
            System.out.println("Solicitação cancelada.");
            return;
        }

        // Solicitação (RF04) com categoria (RF06) + notificação (RF07)
        Corrida corrida = contexto.servicoCorrida
                .solicitarCorrida(contexto.sessao.getUsuarioAtual().getEmail(), origem, destino, categoriaEscolhida, metodoEscolhido);

        System.out.println("\n💳 PROCESSANDO PAGAMENTO...");

        // ✅ CORREÇÃO: Usar o serviço de pagamento do contexto
        ServicoPagamento servicoPagamento = contexto.servicoPagamento;

        // Processar pagamento baseado no método escolhido
        boolean pagamentoSucesso = false;

        switch (metodoEscolhido) {
            case PIX:
                System.out.println("📱 Pagamento via PIX selecionado");
                String qrCode = servicoPagamento.gerarQrCodePix(corrida, est.getPreco());
                System.out.println("   QR Code: " + qrCode);
                pagamentoSucesso = servicoPagamento.processarPagamento(corrida, metodoEscolhido);
                break;
                
            case CARTAO:
                System.out.println("💳 Pagamento via Cartão selecionado");
                // Simular dados do cartão (em app real, isso viria de entrada segura)
                System.out.print("Número do cartão (16 dígitos): ");
                String numeroCartao = entrada.nextLine().trim();
                System.out.print("Validade (MM/AA): ");
                String validade = entrada.nextLine().trim();
                System.out.print("CVV: ");
                String cvv = entrada.nextLine().trim();
                
                pagamentoSucesso = servicoPagamento.processarCartao(numeroCartao, validade, cvv, est.getPreco());
                break;
                
            case PAYPAL:
                System.out.println("🔵 Pagamento via PayPal selecionado");
                // Simular email do PayPal
                System.out.print("Email do PayPal: ");
                String emailPayPal = entrada.nextLine().trim();
                pagamentoSucesso = servicoPagamento.processarPayPal(emailPayPal, est.getPreco());
                break;
                
            case DINHEIRO:
                System.out.println("💰 Pagamento em Dinheiro selecionado");
                pagamentoSucesso = servicoPagamento.processarPagamento(corrida, metodoEscolhido);
                break;
        }

        if (pagamentoSucesso) {
            System.out.println("✅ Pagamento confirmado! Corrida criada com sucesso.");
            System.out.println("📋 ID da Corrida: " + corrida.getId().substring(0, 8));
            
            int notificadas = 0;
            try { notificadas = contexto.servicoOferta.criarOfertasParaCorrida(corrida); }
            catch (Exception e) { System.err.println("Erro ao notificar motoristas: " + e.getMessage()); }
            System.out.println("Ofertas enviadas para " + notificadas + " motoristas da categoria " +
                    (categoriaEscolhida != null ? categoriaEscolhida.name() : "(todas)"));
            System.out.println("(Dados salvos em data/corridas.txt e data/ofertas.txt)");
            
        } else {
            System.out.println("❌ Falha no pagamento. Corrida não pode ser criada.");
            // Reverter criação da corrida se pagamento falhar
            contexto.repositorioCorrida.buscarTodas().removeIf(c -> c.getId().equals(corrida.getId()));
            return;
        }
    }
}