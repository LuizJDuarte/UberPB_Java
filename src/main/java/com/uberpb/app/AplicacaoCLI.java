package com.uberpb.app;

import static com.uberpb.app.ConsoleUI.cabecalho;
import static com.uberpb.app.ConsoleUI.dicaAjuda;
import static com.uberpb.app.ConsoleUI.erro;
import static com.uberpb.app.ConsoleUI.ok;

import java.util.List;
import java.util.Scanner;

/**
 * Orquestra o loop do CLI. As dependências vêm do ProvedorDependencias.
 * Mantém o menu bonito, atalhos e tratamento de erros.
 */
public final class AplicacaoCLI {
    private AplicacaoCLI() {}

    public static void executar() {
        // Obtém contexto e comandos de um único lugar (sem 'new' aqui)
        ContextoAplicacao contexto = ProvedorDependencias.fornecerContexto();
        List<Comando> comandos = ProvedorDependencias.fornecerComandosPadrao();

        try (Scanner entrada = new Scanner(System.in)) {
            loopPrincipal(comandos, contexto, entrada);
        }
        ok("Até mais!");
    }

    private static void loopPrincipal(List<Comando> comandos, ContextoAplicacao contexto, Scanner entrada) {
        while (true) {
            cabecalho("UberPB", contexto.sessao);

            // Filtra conforme sessão e mostra menu
            List<Comando> visiveis = comandos.stream()
                    .filter(c -> c.visivelPara(contexto.sessao.getUsuarioAtual()))
                    .toList();

            for (int i = 0; i < visiveis.size(); i++) {
                System.out.printf("%d) %s%n", i + 1, visiveis.get(i).nome());
            }
            System.out.println("0) Sair   (atalho: 'q')");
            dicaAjuda();
            System.out.print("> ");

            String op = entrada.nextLine().trim();
            if (op.equalsIgnoreCase("q") || op.equalsIgnoreCase("sair")) break;
            if (op.equalsIgnoreCase("h") || op.equalsIgnoreCase("ajuda")) {
                mostrarAjuda(visiveis);
                aguardarEnter(entrada);
                continue;
            }
            if (op.isBlank()) continue;

            try {
                int idx = Integer.parseInt(op) - 1;
                if (idx == -1) break;
                if (idx < 0 || idx >= visiveis.size()) {
                    erro("Opção inválida.");
                    aguardarEnter(entrada);
                    continue;
                }
                visiveis.get(idx).executar(contexto, entrada);
            } catch (NumberFormatException nfe) {
                erro("Digite o número da opção (ou 'h' para ajuda, 'q' para sair).");
            } catch (Exception e) {
                erro(e.getMessage() != null ? e.getMessage() : "Falha inesperada.");
            }
            aguardarEnter(entrada);
        }
    }

    private static void mostrarAjuda(List<Comando> visiveis) {
        System.out.println();
        System.out.println(ConsoleUI.BOLD + "Ajuda" + ConsoleUI.RESET);
        System.out.println("• Selecione pelo número da opção.");
        System.out.println("• " + ConsoleUI.BOLD + "h" + ConsoleUI.RESET + " mostra esta ajuda; "
                + ConsoleUI.BOLD + "q" + ConsoleUI.RESET + " sai.");
        System.out.println("• Opções disponíveis agora:");
        for (int i = 0; i < visiveis.size(); i++) {
            System.out.printf("  %d) %s%n", i + 1, visiveis.get(i).nome());
        }
        System.out.println();
    }

    private static void aguardarEnter(Scanner entrada) {
        System.out.print(ConsoleUI.GRAY + "(Enter para continuar) " + ConsoleUI.RESET);
        entrada.nextLine();
    }
}
