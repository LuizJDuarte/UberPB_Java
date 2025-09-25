package com.uberpb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MotoristaTest {

    @Test
    public void testToStringParaPersistencia_SemVeiculo() {
        Motorista motorista = new Motorista("motorista@teste.com", "senhaHash123");
        motorista.setCnhValida(true);
        motorista.setCrlvValido(true);
        motorista.setContaAtiva(true);

        String expected = "MOTORISTA,motorista@teste.com,senhaHash123,true,true,true,null";
        assertEquals(expected, motorista.toStringParaPersistencia());
    }

    @Test
    public void testToStringParaPersistencia_ComVeiculo() {
        Motorista motorista = new Motorista("motorista@teste.com", "senhaHash123");
        motorista.setCnhValida(true);
        motorista.setCrlvValido(true);
        motorista.setContaAtiva(true);
        Veiculo veiculo = new Veiculo("VW Nivus", 2023, "JKL-3456", "Cinza", 5, "G");
        motorista.setVeiculo(veiculo);

        String expected = "MOTORISTA,motorista@teste.com,senhaHash123,true,true,true," + veiculo.toStringParaPersistencia();
        assertEquals(expected, motorista.toStringParaPersistencia());
    }

    @Test
    public void testToString() {
        Motorista motorista = new Motorista("motorista@teste.com", "senhaHash123");
        motorista.setContaAtiva(true);
        String str = motorista.toString();

        assertTrue(str.contains("Motorista"));
        assertTrue(str.contains("motorista@teste.com"));
        assertTrue(str.contains("Status: Ativa"));
        assertTrue(str.contains("Sem veículo cadastrado"));
    }
}