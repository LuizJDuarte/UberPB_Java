package com.uberpb.app;

public final class MapaASCII {
    private MapaASCII() {}

    /** “Mapa” 1D com o carro na posição do percentual. */
    public static String trilho(String origem, String destino, int percent) {
        var o = encurtar(origem != null ? origem : "Origem", 36);
        var d = encurtar(destino != null ? destino : "Destino", 36);

        int total = 30;
        int pos = Math.max(0, Math.min(total, (percent * total) / 100));

        StringBuilder trilho = new StringBuilder("|");
        for (int i = 0; i < total; i++) trilho.append(i == pos ? 'o' : '-'); // o = “carro”
        trilho.append("|");

        return o + "\n" + trilho + "\n" + d;
    }

    private static String encurtar(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}
