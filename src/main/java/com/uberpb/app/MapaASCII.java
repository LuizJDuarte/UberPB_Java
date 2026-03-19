package com.uberpb.app;

public final class MapaASCII {
    private MapaASCII() {}

    /** “Mapa” 1D com o carro na posição do percentual. */
    public static String trilho(String origem, String destino, int percent) {
        var o = encurtar(origem != null ? origem : "Origem", 36);
        var d = encurtar(destino != null ? destino : "Destino", 36);

        int total = 30;

        // CORREÇÃO: usa (total-1) para centralizar corretamente
        int pos = (percent * (total - 1)) / 100;
        pos = Math.max(0, Math.min(pos, total - 1)); // garante limite

        StringBuilder trilho = new StringBuilder("|");
        for (int i = 0; i < total; i++) {
            trilho.append(i == pos ? 'o' : '-'); // 'o' = “carro”
        }
        trilho.append("|");

        return o + "\n" + trilho + "\n" + d;
    }

    private static String encurtar(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}