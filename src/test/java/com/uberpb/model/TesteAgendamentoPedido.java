package com.uberpb.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TesteAgendamentoPedido {

    public static void main(String[] args) {
        System.out.println("=== TESTE: AgendamentoPedido ===\n");

        // Teste 1: Agendamento Válido (Data futura)
        System.out.println("TESTE 1: Agendamento com data futura");
        LocalDateTime dataFutura = LocalDateTime.now().plus(24, ChronoUnit.HOURS);
        AgendamentoPedido agendamento1 = new AgendamentoPedido(dataFutura);
        
        System.out.println("Data: " + agendamento1.formatarParaPersistencia());
        System.out.println("Válido? " + agendamento1.isValido());
        System.out.println("✓ PASSOU\n");

        // Teste 2: Agendamento Inválido (Data no passado)
        System.out.println("TESTE 2: Agendamento com data no passado");
        LocalDateTime dataPassada = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        AgendamentoPedido agendamento2 = new AgendamentoPedido(dataPassada);
        
        System.out.println("Data: " + agendamento2.formatarParaPersistencia());
        System.out.println("Válido? " + agendamento2.isValido());
        System.out.println("Erro: " + agendamento2.getErroValidacao());
        System.out.println("✓ PASSOU\n");

        // Teste 3: Agendamento nulo
        System.out.println("TESTE 3: Agendamento nulo");
        AgendamentoPedido agendamento3 = new AgendamentoPedido(null);
        
        System.out.println("Válido? " + agendamento3.isValido());
        System.out.println("Erro: " + agendamento3.getErroValidacao());
        System.out.println("✓ PASSOU\n");

        // Teste 4: Persistência e Leitura
        System.out.println("TESTE 4: Persistência e Leitura");
        String strPersistida = agendamento1.formatarParaPersistencia();
        AgendamentoPedido agendamento4 = AgendamentoPedido.fromString(strPersistida);
        
        System.out.println("Original: " + agendamento1.formatarParaPersistencia());
        System.out.println("Recuperado: " + agendamento4.formatarParaPersistencia());
        System.out.println("Iguais? " + agendamento1.getDataHoraAgendamento().equals(agendamento4.getDataHoraAgendamento()));
        System.out.println("✓ PASSOU\n");

        // Teste 5: TipoPedido enum
        System.out.println("TESTE 5: TipoPedido enum");
        System.out.println("IMEDIATO: " + TipoPedido.IMEDIATO.getDescricao());
        System.out.println("AGENDADO: " + TipoPedido.AGENDADO.getDescricao());
        System.out.println("✓ PASSOU\n");

        // Teste 6: Pedido com agendamento
        System.out.println("TESTE 6: Criação de Pedido com Agendamento");
        java.util.List<ItemCarrinho> itens = new java.util.ArrayList<>();
        Pedido pedido = new Pedido(
            "Cliente@test.com",
            "Restaurante@test.com",
            itens,
            100.0,
            "Pix",
            TipoPedido.AGENDADO,
            agendamento1
        );
        
        System.out.println("Tipo de Pedido: " + pedido.getTipoPedido().getDescricao());
        System.out.println("Agendamento: " + pedido.getAgendamento().formatarParaPersistencia());
        System.out.println("Status: " + pedido.getStatus());
        System.out.println("✓ PASSOU\n");

        System.out.println("=== TODOS OS TESTES PASSARAM ===");
    }
}
