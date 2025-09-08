package com.uberpb.app;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.service.CalculadoraPrecoCorrida;

import java.util.Scanner;

public class EstimativaCorridaComando implements Comando {

    @Override
    public String nome() {
        return "Ver Estimativa de Corrida";
    }

    @Override
    public boolean visivelPara(Usuario usuarioAtualOuNull) {
        return usuarioAtualOuNull instanceof Passageiro;
    }

    @Override
    public void executar(ContextoAplicacao contexto, Scanner entrada) {
        System.out.println("=== ESTIMATIVA DE CORRIDA ===");
        System.out.println("Informe os endereços para calcular a estimativa:");
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
        
        // Calcular estimativas
        double distanciaEstimada = CalculadoraPrecoCorrida.estimarDistanciaKm(origem, destino);
        double tempoEstimado = CalculadoraPrecoCorrida.estimarTempoMinutos(distanciaEstimada);
        
        System.out.println("\n--- RESULTADO DA ESTIMATIVA ---");
        System.out.printf("? Distância estimada: %.1f km%n", distanciaEstimada);
        System.out.printf("? Tempo estimado: %.0f minutos%n", tempoEstimado);
        System.out.println("\n? Preços por categoria:");
        System.out.println("-----------------------------");
        
        for (CategoriaVeiculo categoria : CategoriaVeiculo.values()) {
            double preco = CalculadoraPrecoCorrida.calcularPreco(
                distanciaEstimada, tempoEstimado, categoria);
            System.out.printf("? %-10s: R$ %.2f%n", categoria.getNome(), preco);
            System.out.printf("  %s%n", categoria.getDescricao());
        }
        
        System.out.println("-----------------------------");
        System.out.println("Observação: Valores estimados podem variar.");
    }
}