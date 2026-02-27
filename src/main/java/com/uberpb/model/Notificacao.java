package com.uberpb.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de Notificação para envio de mensagens aos usuários (RF22)
 */
public class Notificacao {

    private final String id;
    private final String destinatarioEmail;
    private final TipoNotificacao tipo;
    private final String mensagem;
    private final LocalDateTime dataHora;
    private boolean lida;

    public Notificacao(String id, String destinatarioEmail, TipoNotificacao tipo, String mensagem) {
        this.id = id;
        this.destinatarioEmail = destinatarioEmail;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.dataHora = LocalDateTime.now();
        this.lida = false;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDestinatarioEmail() {
        return destinatarioEmail;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public boolean isLida() {
        return lida;
    }

    public void marcarComoLida() {
        this.lida = true;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("[%s] %s - %s: %s",
                dataHora.format(formatter),
                lida ? "LIDA" : "NÃO LIDA",
                tipo,
                mensagem);
    }

    public String toStringParaPersistencia() {
        return String.format("%s,%s,%s,%s,%s,%s",
                id,
                destinatarioEmail,
                tipo.name(),
                mensagem.replace(",", ";"),
                dataHora.toString(),
                lida);
    }

    public static Notificacao fromString(String linha) {
        if (linha == null || linha.isBlank())
            return null;

        String[] partes = linha.split(",", -1);
        if (partes.length < 6)
            return null;

        try {
            Notificacao notif = new Notificacao(
                    partes[0],
                    partes[1],
                    TipoNotificacao.valueOf(partes[2]),
                    partes[3]);
            if (Boolean.parseBoolean(partes[5])) {
                notif.marcarComoLida();
            }
            return notif;
        } catch (Exception e) {
            return null;
        }
    }
}
