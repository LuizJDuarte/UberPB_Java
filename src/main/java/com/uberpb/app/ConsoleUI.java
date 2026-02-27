package com.uberpb.app;

import java.util.Scanner;

/** Utilitários para deixar o CLI consistente. */
public final class ConsoleUI {
    private ConsoleUI() {}

    public static final String RESET  = "\u001B[0m";
    public static final String BOLD   = "\u001B[1m";
    public static final String CYAN   = "\u001B[36m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GRAY   = "\u001B[90m";
    public static final String RED    = "\u001B[31m";

    /** Limpa a tela (se o terminal suportar ANSI). */
    public static void limparTela() {
        try {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception ignored) {}
    }

    /** Cabeçalho padrão do app. */
    public static void cabecalho(String titulo, Sessao sessao) {
        limparTela();
        String info = "";
        if (sessao != null && sessao.getUsuarioAtual() != null) {
            var u = sessao.getUsuarioAtual();
            info = " | " + u.getEmail() + " (" + u.getTipo().name() + ")";
        }
        System.out.println(BOLD + "== " + titulo + info + " ==" + RESET);
    }

    public static void dicaAjuda() {
        System.out.println(GRAY + "[Dica] Digite " + BOLD + "h" + RESET + GRAY + " para ajuda, "
                + BOLD + "q" + RESET + GRAY + " para sair." + RESET);
    }

    public static void erro(String msg) { System.out.println(RED + "Erro: " + msg + RESET); }
    public static void ok(String msg)   { System.out.println(GREEN + msg + RESET); }

    /** Confirmação simples: retorna true apenas se o usuário digitar 's'. */
    public static boolean confirmar(Scanner in, String pergunta) {
        System.out.print(pergunta + " (s/N): ");
        return "s".equalsIgnoreCase(in.nextLine().trim());
    }

    /** Barra de 0–100% para acompanhar progresso. */
    public static void barra(int percent) {
        int p = Math.max(0, Math.min(100, percent));
        int total = 30;
        int fill = (p * total) / 100;
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < fill; i++) sb.append('#');
        for (int i = fill; i < total; i++) sb.append('-');
        sb.append("] ").append(p).append('%');
        System.out.println(sb);
    }

    /** “Mapa” textual simples: carro anda em uma linha pontilhada entre rótulos. */
    public static String mapaLinha(int percentual, String origem, String destino) {
    int total = 60;
    int pos = Math.max(0, Math.min(total, (percentual * total) / 100));
    StringBuilder sb = new StringBuilder();
    
    // CORREÇÃO: Origem alinhada
    sb.append(String.format("%-15s", origem.length() > 15 ? origem.substring(0, 12) + "..." : origem));
    sb.append(' ');

    for (int i = 0; i < total; i++) {
        if (i == pos) {
            // 🚗 (carro) na posição correta
            sb.append("🚗");
        } else {
            sb.append('·'); // ponto médio
        }
    }

    sb.append(' ');
    // CORREÇÃO: Destino alinhado
    sb.append(String.format("%-15s", destino.length() > 15 ? destino.substring(0, 12) + "..." : destino));
    return sb.toString();
}
}
