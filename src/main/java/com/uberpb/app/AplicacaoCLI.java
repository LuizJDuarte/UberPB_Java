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
        List<Comando> comandos = ProvedorDependencias.fornecerComandos();

        try (Scanner entrada = new Scanner(System.in)) {
            loopPrincipal(comandos, contexto, entrada);
        }
        ok("Até mais!");
    }

    private static void loopPrincipal(List<Comando> comandos, ContextoAplicacao contexto, Scanner entrada) {
        while (true) {
            cabecalho("UberPB", contexto.sessao);

            // Exibe o Header de Status antes de qualquer outra coisa
            exibirHeaderDeStatus(contexto);

            // Filtra conforme sessão e mostra menu
            List<Comando> visiveis = comandos.stream()
                    .filter(c -> c.visivelPara(contexto.sessao.getUsuarioAtual()))
                    .toList();

            // Lógica de "Gate" e Filtro de Comandos
            if (contexto.getSessao().getUsuarioAtual() instanceof com.uberpb.model.Motorista motorista) {
                // A lógica de filtragem agora segue a mesma prioridade dos headers
                if (!motorista.isContaAtiva()) {
                    visiveis = visiveis.stream().filter(c -> c instanceof LogoutComando).toList();
                } else if (motorista.getVeiculo() == null) {
                    visiveis = visiveis.stream()
                        .filter(c -> c instanceof CompletarCadastroMotoristaComando || c instanceof LogoutComando)
                        .toList();
                } else if (!motorista.isCnhValida() || !motorista.isCrlvValido()) {
                    visiveis = visiveis.stream()
                        .filter(c -> c instanceof VerificarStatusAprovacaoComando || c instanceof LogoutComando)
                        .toList();
                }
                // Se tudo estiver OK, o motorista vê a lista completa de comandos de motorista (já filtrada pelo visivelPara)
            }

            for (int i = 0; i < visiveis.size(); i++) {
                Comando cmd = visiveis.get(i);
                System.out.printf("%d) %s%n", i + 1, cmd.nomeParaExibicao(contexto.sessao.getUsuarioAtual()));
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

    private static void exibirHeaderDeStatus(ContextoAplicacao contexto) {
        var usuario = contexto.getSessao().getUsuarioAtual();
        if (usuario == null) return;

        if (usuario instanceof com.uberpb.model.Motorista motorista) {
            // Lógica do Header de Status para Motorista
            if (!motorista.isContaAtiva()) {
                imprimirStatusBox("STATUS: CONTA BLOQUEADA", "Sua conta foi desativada. Entre em contato com o suporte.");
            } else if (motorista.getVeiculo() == null) {
                imprimirStatusBox("STATUS: CADASTRO PENDENTE (1/2)", "Bem-vindo! Para começar a dirigir, cadastre seu veículo.");
            } else if (!motorista.isCnhValida() || !motorista.isCrlvValido()) {
                imprimirStatusBox("STATUS: DOCUMENTAÇÃO EM ANÁLISE (2/2)", "Seu veículo foi cadastrado! Aguardando validação dos seus documentos.");
            } else if (motorista.isDisponivel()) {
                imprimirStatusBox("STATUS: ONLINE - PRONTO PARA CORRIDAS", "Você está visível para passageiros. Aguardando chamados...");
            } else {
                imprimirStatusBox("STATUS: OFFLINE", "Você não está recebendo chamados. Fique online para começar a dirigir.");
            }
        } else if (usuario instanceof com.uberpb.model.Passageiro) {
            // Lógica (simplificada) para Passageiro
            boolean emCorrida = contexto.getRepositorioCorrida().buscarCorridaAtivaPorPassageiro(usuario.getEmail()) != null;
            if (emCorrida) {
                imprimirStatusBox("STATUS: EM CORRIDA", "Você está em uma corrida no momento.");
            }
        } else if (usuario instanceof com.uberpb.model.Administrador) {
            imprimirStatusBox("PAINEL DE CONTROLE - ADMINISTRADOR", "Ações administrativas disponíveis abaixo.");
        }
    }

    private static void imprimirStatusBox(String titulo, String mensagem) {
        System.out.println("-------------------------------------------------");
        System.out.println(ConsoleUI.BOLD + titulo + ConsoleUI.RESET);
        System.out.println(mensagem);
        System.out.println("-------------------------------------------------");
    }
}
