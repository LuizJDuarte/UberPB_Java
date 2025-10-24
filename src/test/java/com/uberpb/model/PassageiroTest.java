package com.uberpb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PassageiroTest {
    @Test
    public void testToStringParaPersistencia() {
        Passageiro passageiro = new Passageiro("passageiro@teste.com", "senhaHash123");
        String expected = "PASSAGEIRO,passageiro@teste.com,senhaHash123,0.0,0";
        assertEquals(expected, passageiro.toStringParaPersistencia());
    }

    @Test
    public void testToString() {
        Passageiro passageiro = new Passageiro("passageiro@teste.com", "senhaHash123");
        String str = passageiro.toString();
        assertTrue(str.contains("Passageiro"));
        assertTrue(str.contains("passageiro@teste.com"));
    }
}
