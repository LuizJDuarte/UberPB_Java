package com.uberpb.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AgendamentoPedido {

    private LocalDateTime dataHoraAgendamento;

    public AgendamentoPedido(LocalDateTime dataHoraAgendamento) {
        this.dataHoraAgendamento = dataHoraAgendamento;
    }

    public LocalDateTime getDataHoraAgendamento() {
        return dataHoraAgendamento;
    }

    public void setDataHoraAgendamento(LocalDateTime dataHoraAgendamento) {
        this.dataHoraAgendamento = dataHoraAgendamento;
    }

    public boolean isValido() {
        if (dataHoraAgendamento == null) {
            return false;
        }
        // Validar se a data/hora é no futuro
        return dataHoraAgendamento.isAfter(LocalDateTime.now());
    }

    public String getErroValidacao() {
        if (dataHoraAgendamento == null) {
            return "Data e hora não podem estar vazias.";
        }
        if (!dataHoraAgendamento.isAfter(LocalDateTime.now())) {
            return "A data e hora devem ser no futuro.";
        }
        return null;
    }

    public String formatarParaPersistencia() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dataHoraAgendamento.format(formatter);
    }

    public static AgendamentoPedido fromString(String data) {
        if (data == null || data.trim().isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(data, formatter);
            return new AgendamentoPedido(dateTime);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Agendado para: " + formatarParaPersistencia();
    }
}
