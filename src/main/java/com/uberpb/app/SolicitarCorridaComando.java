package com.uberpb.app;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Corrida;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.service.CalculadoraPrecoCorrida;

import java.util.Scanner;

public class SolicitarCorridaComando implements Comando {

    @Override
    public String nome() {
        return "Solicitar Corrida";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.println("=== SOLICITAR CORRIDA ===");
        System.out.println("Informe os endereços:");
        System.out.print("Endereço de ORIGEM: ");
        String origem = entrada.nextLine().trim();
        
        System.out.print("Endereço de DESTINO: ");
        String destino = entrada.nextLine().trim();
        
        if (origem.isEmpty() || destino.isEmpty()) {
            System.out.println("Origem e destino são obrigatórios!");
            return;
        }
        
        if (origem.equalsIgnoreCase(destino)) {
            System.out.println("Origem e destino não podem ser iguais!");
            return;
        }
        
        // Mostrar estimativa antes de confirmar
        double distanciaEstimada = CalculadoraPrecoCorrida.estimarDistanciaKm(origem, destino);
        double tempoEstimado = CalculadoraPrecoCorrida.estimarTempoMinutos(distanciaEstimada);
        
        System.out.println("\n--- ESTIMATIVA ---");
        System.out.printf("Distância: %.1f km | Tempo: %.0f min%n", distanciaEstimada, tempoEstimado);
        
        // Mostrar categorias disponíveis
        System.out.println("\nCategorias disponíveis:");
        CategoriaVeiculo[] categorias = CategoriaVeiculo.values();
        for (int i = 0; i < categorias.length; i++) {
            double preco = CalculadoraPrecoCorrida.calcularPreco(
                distanciaEstimada, tempoEstimado, categorias[i]);
            System.out.printf("%d) %-10s: R$ %.2f%n", 
                i + 1, categorias[i].getNome(), preco);
        }
        
        System.out.print("\nEscolha a categoria (1-" + categorias.length + "): ");
        try {
            int escolha = Integer.parseInt(entrada.nextLine().trim());
            if (escolha < 1 || escolha > categorias.length) {
                System.out.println("Opção inválida!");
                return;
            }
            
            CategoriaVeiculo categoriaEscolhida = categorias[escolha - 1];
            double precoFinal = CalculadoraPrecoCorrida.calcularPreco(
                distanciaEstimada, tempoEstimado, categoriaEscolhida);
            
            System.out.printf("\nConfirmar corrida %s por R$ %.2f? (s/n): ", 
                categoriaEscolhida.getNome(), precoFinal);
            
            String confirmacao = entrada.nextLine().trim();
            if (confirmacao.equalsIgnoreCase("s")) {
                Corrida corrida = contexto.servicoCorrida
                    .solicitarCorrida(contexto.sessao.getUsuarioAtual().getEmail(), origem, destino);
                
                System.out.println("? Corrida solicitada com sucesso!");
                System.out.println("ID: " + corrida.getId());
                System.out.printf("Categoria: %s | Preço: R$ %.2f%n", 
                    categoriaEscolhida.getNome(), precoFinal);
            } else {
                System.out.println("? Corrida cancelada.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido.");
        }
    }
}