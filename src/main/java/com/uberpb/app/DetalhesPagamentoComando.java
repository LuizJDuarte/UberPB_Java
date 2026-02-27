package com.uberpb.app;

import com.uberpb.model.Corrida;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.service.CalculadoraPrecoCorrida;

import java.util.List;
import java.util.Scanner;

import static com.uberpb.app.ConsoleUI.*;

public class DetalhesPagamentoComando implements Comando {

    @Override
    public String nome() {
        return "Ver Detalhes de Pagamento";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        String emailPassageiro = contexto.sessao.getUsuarioAtual().getEmail();
        
        List<Corrida> corridas = contexto.repositorioCorrida.buscarPorPassageiro(emailPassageiro);
        
        if (corridas.isEmpty()) {
            erro("Nenhuma corrida encontrada.");
            return;
        }
        
        System.out.println("=== DETALHES DE PAGAMENTO ===");
        System.out.println("Selecione a corrida:");
        
        for (int i = 0; i < corridas.size(); i++) {
            Corrida c = corridas.get(i);
            System.out.printf("%d) %s - %s → %s%n", 
                i + 1, c.getId().substring(0, 8), 
                c.getOrigemEndereco(), c.getDestinoEndereco());
        }
        
        System.out.print("Escolha: ");
        try {
            int escolha = Integer.parseInt(entrada.nextLine().trim());
            if (escolha < 1 || escolha > corridas.size()) {
                erro("Opção inválida!");
                return;
            }
            
            Corrida corrida = corridas.get(escolha - 1);
            exibirDetalhesPagamento(corrida);
            
        } catch (NumberFormatException e) {
            erro("Por favor, digite um número válido.");
        }
    }
    
    private void exibirDetalhesPagamento(Corrida corrida) {
        System.out.println("\n💵 DETALHES DO PAGAMENTO");
        System.out.println("──────────────────────────────────────────────");
        System.out.printf("🆔 Corrida: %s%n", corrida.getId().substring(0, 8));
        System.out.printf("📍 Origem: %s%n", corrida.getOrigemEndereco());
        System.out.printf("🎯 Destino: %s%n", corrida.getDestinoEndereco());
        
        if (corrida.getCategoriaEscolhida() != null) {
            System.out.printf("🚗 Categoria: %s%n", corrida.getCategoriaEscolhida().getNome());
        }
        
        if (corrida.getMetodoPagamento() != null) {
            System.out.printf("💳 Método: %s%n", corrida.getMetodoPagamento().name());
        }
        
        // Calcular e mostrar estimativa detalhada
        if (corrida.getOrigemEndereco() != null && corrida.getDestinoEndereco() != null) {
            double distancia = CalculadoraPrecoCorrida.estimarDistanciaKm(
                corrida.getOrigemEndereco(), corrida.getDestinoEndereco());
            double tempo = CalculadoraPrecoCorrida.estimarTempoMinutos(distancia);
            
            if (corrida.getCategoriaEscolhida() != null) {
                double preco = CalculadoraPrecoCorrida.calcularPreco(
                    distancia, tempo, corrida.getCategoriaEscolhida());
                
                System.out.printf("💰 Preço estimado: R$ %.2f%n", preco);
                System.out.printf("🛣️  Distância estimada: %.1f km%n", distancia);
                System.out.printf("⏱️  Tempo estimado: %.0f min%n", tempo);
            }
        }
        
        System.out.println("──────────────────────────────────────────────");
        System.out.println("📝 Status: " + 
            (corrida.getStatus() != null ? corrida.getStatus() : "Não definido"));
    }
}