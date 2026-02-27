package com.uberpb.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MotoristaTest {

    @Test
    void testToStringParaPersistenciaCompleto() {
        Motorista motorista = new Motorista("motorista@teste.com", "senhaHash123");
        motorista.setCnhValida(true);
        motorista.setCrlvValido(true);
        motorista.setContaAtiva(true);
        motorista.setVeiculo(new Veiculo("Fusca", 1980, "ABC-1234", "Azul", 4, "P"));

        String expected = "MOTORISTA,motorista@teste.com,senhaHash123,true,true,true,0.0,0,false,Fusca|1980|ABC-1234|Azul|4|P|";

        assertEquals(expected, motorista.toStringParaPersistencia());
    }

    @Test
    void testToStringParaPersistenciaParcial() {
        Motorista motorista = new Motorista("motorista2@teste.com", "outraSenha");

        String expected = "MOTORISTA,motorista2@teste.com,outraSenha,false,false,false,0.0,0,false,null";

        assertEquals(expected, motorista.toStringParaPersistencia());
    }

    @Test
    void testAdicionarAvaliacao() {
        Motorista motorista = new Motorista("motorista@teste.com", "senha123");
        motorista.adicionarAvaliacao(5);
        motorista.adicionarAvaliacao(4);

        assertEquals(4.5, motorista.getRatingMedio(), 0.01);
        assertEquals(2, motorista.getTotalAvaliacoes());
    }

    @Test
    void testGettersSetters() {
        Motorista motorista = new Motorista("m@teste.com", "123");

        motorista.setCnhValida(true);
        assertTrue(motorista.isCnhValida());

        motorista.setCrlvValido(true);
        assertTrue(motorista.isCrlvValido());

        motorista.setContaAtiva(true);
        assertTrue(motorista.isContaAtiva());

        Veiculo v = new Veiculo("Corsa", 2010, "XYZ-9876", "Prata", 4, "A");
        motorista.setVeiculo(v);
        assertEquals(v, motorista.getVeiculo());
    }

    @Test
    void testToString() {
        Motorista motorista = new Motorista("motorista@teste.com", "senhaHash123");

        String esperado = "Motorista - Email: motorista@teste.com, Status: Inativa | Offline, CNH: Pendente, CRLV: Pendente, ⭐ Sem avaliações, Sem veículo";

        assertEquals(esperado, motorista.toString());
    }

    @Test
    void testVeiculoAlterado() {
        Motorista motorista = new Motorista("motorista@teste.com", "senha");
        assertNull(motorista.getVeiculo());

        Veiculo v1 = new Veiculo("Gol", 2015, "DEF-4567", "Preto", 4, "A");
        motorista.setVeiculo(v1);
        assertEquals(v1, motorista.getVeiculo());

        Veiculo v2 = new Veiculo("Fiesta", 2018, "GHI-8910", "Branco", 4, "B");
        motorista.setVeiculo(v2);
        assertEquals(v2, motorista.getVeiculo());
    }

    @Test
    void testAdicionarAvaliacaoExtremos() {
        Motorista motorista = new Motorista("m@teste.com", "123");

        // rating mínimo
        motorista.adicionarAvaliacao(0);
        assertEquals(0, motorista.getRatingMedio(), 0.01);

        // rating máximo
        motorista.adicionarAvaliacao(5);
        assertEquals(2.5, motorista.getRatingMedio(), 0.01);
    }
}