package com.uberpb.service;

import com.uberpb.model.Localizacao;

public class ServicoLocalizacao {

    public ServicoLocalizacao() {}

    public Localizacao geocodificar(String endereco) {
        int h = endereco == null ? 0 : endereco.hashCode();
        double lat = -7.12 + ((h & 0x7FFF) / 32767.0) * 0.20;
        double lon = -34.90 + (((h>>>15) & 0x7FFF) / 32767.0) * 0.20;
        return new Localizacao(lat, lon);
    }

    public double distanciaKm(Localizacao a, Localizacao b) {
        double R = 6371.0;
        double dLat = Math.toRadians(b.latitude() - a.latitude());
        double dLon = Math.toRadians(b.longitude() - a.longitude());
        double s1 = Math.sin(dLat/2), s2 = Math.sin(dLon/2);
        double aa = s1*s1 + Math.cos(Math.toRadians(a.latitude()))*Math.cos(Math.toRadians(b.latitude()))*s2*s2;
        double c = 2 * Math.atan2(Math.sqrt(aa), Math.sqrt(1-aa));
        return Math.max(0.1, R*c);
    }

    public Localizacao obterLocalizacaoAtual(String emailUsuario) {
        return geocodificar(emailUsuario);
    }
}
