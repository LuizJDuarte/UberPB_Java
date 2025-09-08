package com.uberpb.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Corrida para RF04 com endereços em texto (e, opcionalmente, coordenadas).
 * Persistência principal (pipe + escape):
 *   id|email|status|origEnd|destEnd|origLat|origLon|destLat|destLon
 * Compatível com formato antigo CSV (id,email,origLat,origLon,destLat,destLon,status)
 */
public class Corrida {
    private final String id;
    private final String emailPassageiro;

    private final String origemEndereco;   // pode ser null
    private final String destinoEndereco;  // pode ser null
    private final Localizacao origem;      // pode ser null
    private final Localizacao destino;     // pode ser null

    private CorridaStatus status;

    public Corrida(String id,
                   String emailPassageiro,
                   String origemEndereco,
                   String destinoEndereco,
                   Localizacao origem,
                   Localizacao destino,
                   CorridaStatus status) {
        this.id = Objects.requireNonNull(id);
        this.emailPassageiro = Objects.requireNonNull(emailPassageiro);
        this.origemEndereco = origemEndereco;
        this.destinoEndereco = destinoEndereco;
        this.origem = origem;
        this.destino = destino;
        this.status = Objects.requireNonNull(status);
    }

    public static Corrida novaComEnderecos(String emailPassageiro, String origemEndereco, String destinoEndereco) {
        return new Corrida(UUID.randomUUID().toString(), emailPassageiro,
                origemEndereco, destinoEndereco, null, null, CorridaStatus.SOLICITADA);
    }
    public static Corrida novaComCoordenadas(String emailPassageiro, Localizacao origem, Localizacao destino) {
        return new Corrida(UUID.randomUUID().toString(), emailPassageiro,
                null, null, origem, destino, CorridaStatus.SOLICITADA);
    }

    public String getId() { return id; }
    public String getEmailPassageiro() { return emailPassageiro; }
    public String getOrigemEndereco() { return origemEndereco; }
    public String getDestinoEndereco() { return destinoEndereco; }
    public Localizacao getOrigem() { return origem; }
    public Localizacao getDestino() { return destino; }
    public CorridaStatus getStatus() { return status; }
    public void setStatus(CorridaStatus status) { this.status = status; }

    // ===================== Persistência =====================

    private static final char SEP = '|';
    private static final char ESC = '\\';

    /** Formato principal (pipe + escape). */
    public String toStringParaPersistencia() {
        StringBuilder sb = new StringBuilder();
        sb.append(esc(id)).append(SEP)
          .append(esc(emailPassageiro)).append(SEP)
          .append(esc(status.name())).append(SEP)
          .append(esc(nvl(origemEndereco))).append(SEP)
          .append(esc(nvl(destinoEndereco))).append(SEP)
          .append(origem  != null ? Double.toString(origem.latitude())  : "").append(SEP)
          .append(origem  != null ? Double.toString(origem.longitude()) : "").append(SEP)
          .append(destino != null ? Double.toString(destino.latitude()) : "").append(SEP)
          .append(destino != null ? Double.toString(destino.longitude()): "");
        return sb.toString();
    }

    /** Aceita tanto o formato novo (pipe) quanto o antigo (CSV). */
    public static Corrida fromStringGenerico(String linha) {
        if (linha.indexOf(SEP) >= 0) return fromPipe(linha);
        return fromCSVAntigo(linha);
    }

    private static Corrida fromPipe(String linha) {
        String[] p = splitComEscape(linha, SEP, ESC);
        String id = unesc(p[0]);
        String email = unesc(p[1]);
        CorridaStatus st = CorridaStatus.valueOf(unesc(p[2]));
        String oEnd = p.length > 3 ? unesc(p[3]) : null;
        String dEnd = p.length > 4 ? unesc(p[4]) : null;

        Localizacao o = null, d = null;
        if (p.length > 6 && !vazio(p[5]) && !vazio(p[6])) {
            o = new Localizacao(parseDouble(p[5]), parseDouble(p[6]));
        }
        if (p.length > 8 && !vazio(p[7]) && !vazio(p[8])) {
            d = new Localizacao(parseDouble(p[7]), parseDouble(p[8]));
        }
        return new Corrida(id, email, oEnd, dEnd, o, d, st);
    }

    private static Corrida fromCSVAntigo(String linha) {
        // id,email,origLat,origLon,destLat,destLon,status
        String[] p = linha.split(",", -1);
        String id = p[0];
        String email = p[1];
        double oLat = parseDouble(p[2]);
        double oLon = parseDouble(p[3]);
        double dLat = parseDouble(p[4]);
        double dLon = parseDouble(p[5]);
        CorridaStatus st = CorridaStatus.valueOf(p[6]);
        return new Corrida(id, email, null, null,
                new Localizacao(oLat, oLon), new Localizacao(dLat, dLon), st);
    }

    // ===== helpers =====
    private static String esc(String s) {
        StringBuilder b = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == SEP || c == ESC || c == '\n' || c == '\r') b.append(ESC);
            b.append(c);
        }
        return b.toString();
    }
    private static String unesc(String s) {
        StringBuilder b = new StringBuilder();
        boolean e = false;
        for (char c : s.toCharArray()) {
            if (e) { b.append(c); e = false; }
            else if (c == ESC) e = true;
            else b.append(c);
        }
        return b.toString();
    }
    private static String[] splitComEscape(String s, char sep, char esc) {
        java.util.List<String> campos = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean e = false;
        for (char c : s.toCharArray()) {
            if (e) { cur.append(c); e = false; }
            else if (c == esc) e = true;
            else if (c == sep) { campos.add(cur.toString()); cur.setLength(0); }
            else cur.append(c);
        }
        campos.add(cur.toString());
        return campos.toArray(new String[0]);
    }
    private static boolean vazio(String s) { return s == null || s.isBlank(); }
    private static double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }
    private static String nvl(String s) { return s == null ? "" : s; }
}
