package com.uberpb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MotoristaTest {

    @Test
    public void testToStringParaPersistenciaCompleto() {
        Motorista motorista = new Motorista("motorista@teste.com", "senhaHash123");
        motorista.setCnhValida(true);
        motorista.setCrlvValido(true);
        motorista.setContaAtiva(true);
        motorista.setVeiculo(new Veiculo("Fusca", 1980, "ABC-1234", "Azul", 4, "P"));

        String expected = "MOTORISTA,motorista@teste.com,senhaHash123,true,true,true,0.0,0,Fusca|1980|ABC-1234|Azul|4|P|";
        assertEquals(expected, motorista.toStringParaPersistencia());
    }

    @Test
    public void testToStringParaPersistenciaParcial() {
        Motorista motorista = new Motorista("motorista2@teste.com", "outraSenha");
        String expected = "MOTORISTA,motorista2@teste.com,outraSenha,false,false,false,0.0,0,,null";
        assertEquals(expected, motorista.toStringParaPersistencia());
    }

    @Test
    public void testAdicionarAvaliacao() {
        Motorista motorista = new Motorista("motorista@teste.com", "senha123");
        motorista.adicionarAvaliacao(5);
        motorista.adicionarAvaliacao(4);
        assertEquals(4.5, motorista.getRatingMedio(), 0.01);
        assertEquals(2, motorista.getTotalAvaliacoes());
    }
}
