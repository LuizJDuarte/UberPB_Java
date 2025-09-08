package com.uberpb.app;

/** Utilitário simples para deixar o CLI mais bonito e consistente. */
public final class ConsoleUI {
    private ConsoleUI() {}

    public static final String RESET  = "\u001B[0m";
    public static final String BOLD   = "\u001B[1m";
    public static final String CYAN   = "\u001B[36m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GRAY   = "\u001B[90m";
    public static final String RED    = "\u001B[31m";

    /** Limpa a tela na maioria dos terminais. Se não suportar ANSI, apenas ignora. */
    public static void limparTela() {
        try {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception ignored) {}
    }

    /** Cabeçalho padrão do app. */
    public static void cabecalho(String titulo, Sessao sessao) {
        limparTela();
        System.out.println(BOLD + CYAN + "== " + titulo + " ==" + RESET);
        if (sessao != null && sessao.estaLogado()) {
            System.out.println(GRAY + "Logado como: " + sessao.getUsuarioAtual().getEmail() + RESET);
        } else {
            System.out.println(GRAY + "Não logado" + RESET);
        }
        System.out.println();
    }

    public static void dicaAjuda() {
        System.out.println(GRAY + "[Dica] Digite " + BOLD + "h" + RESET + GRAY + " para ajuda, "
                + BOLD + "q" + RESET + GRAY + " para sair." + RESET);
    }

    public static void erro(String msg) {
        System.out.println(RED + "Erro: " + msg + RESET);
    }

    public static void ok(String msg) {
        System.out.println(GREEN + msg + RESET);
    }
}
