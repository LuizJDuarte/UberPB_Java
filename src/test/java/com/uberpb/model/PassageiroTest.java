package com.uberpb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PassageiroTest {

    @Test
    public void testToStringParaPersistencia() {
        Passageiro passageiro = new Passageiro("passageiro@teste.com", "senhaHash456");
        String expected = "PASSAGEIRO,passageiro@teste.com,senhaHash456";
        assertEquals(expected, passageiro.toStringParaPersistencia());
    }

    @Test
    public void testToString() {
        Passageiro passageiro = new Passageiro("passageiro@teste.com", "senhaHash456");
        String str = passageiro.toString();

        assertTrue(str.contains("Passageiro"));
        assertTrue(str.contains("passageiro@teste.com"));
    }
}