package com.uberpb.model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

public class VeiculoTest {

    @Test
    public void testToStringParaPersistencia_ComCategorias() {
        Veiculo veiculo = new Veiculo("Toyota Corolla", 2021, "ABC-1234", "Preto", 5, "M");
        veiculo.setCategoriasDisponiveis(Arrays.asList(
            CategoriaVeiculo.fromString("COMFORT"),
            CategoriaVeiculo.fromString("BLACK")
        ));
        String expected = "Toyota Corolla|2021|ABC-1234|Preto|5|M|Comfort;Black";
        assertEquals(expected, veiculo.toStringParaPersistencia());
    }

    @Test
    public void testToStringParaPersistencia_SemCategorias() {
        Veiculo veiculo = new Veiculo("Honda Civic", 2020, "XYZ-5678", "Branco", 5, "G");
        veiculo.setCategoriasDisponiveis(Collections.emptyList());
        String expected = "Honda Civic|2020|XYZ-5678|Branco|5|G|";
        assertEquals(expected, veiculo.toStringParaPersistencia());
    }

    @Test
    public void testFromStringParaPersistencia_Completo() {
        String data = "Nissan Kicks|2022|DEF-5678|Azul|5|P|COMFORT;BAG";
        Veiculo veiculo = Veiculo.fromStringParaPersistencia(data);
        assertNotNull(veiculo);
        assertEquals("Nissan Kicks", veiculo.getModelo());
        assertEquals(2022, veiculo.getAno());
        assertEquals("DEF-5678", veiculo.getPlaca());
        assertEquals("Azul", veiculo.getCor());
        assertEquals(5, veiculo.getCapacidadePassageiros());
        assertEquals("P", veiculo.getTamanhoPortaMalas());
        assertEquals(2, veiculo.getCategoriasDisponiveis().size());
        assertTrue(veiculo.getCategoriasDisponiveis().contains(CategoriaVeiculo.fromString("COMFORT")));
        assertTrue(veiculo.getCategoriasDisponiveis().contains(CategoriaVeiculo.fromString("BAG")));
    }

    @Test
    public void testFromStringParaPersistencia_DadosMinimos() {
        String data = "VW Gol|2019|GHI-9012|Vermelho|5|M|UBERX";
        Veiculo veiculo = Veiculo.fromStringParaPersistencia(data);
        assertNotNull(veiculo);
        assertEquals("VW Gol", veiculo.getModelo());
        assertEquals(2019, veiculo.getAno());
        assertEquals(1, veiculo.getCategoriasDisponiveis().size());
    }

    @Test
    public void testFromStringParaPersistencia_DadosVazios() {
        String data = "|||||";
        Veiculo veiculo = Veiculo.fromStringParaPersistencia(data);
        assertNotNull(veiculo);
        assertEquals("", veiculo.getModelo());
        assertEquals(0, veiculo.getAno());
        assertTrue(veiculo.getCategoriasDisponiveis().isEmpty());
    }

    @Test
    public void testFromStringParaPersistencia_NullOuInvalido() {
        assertNull(Veiculo.fromStringParaPersistencia(null));
        assertNull(Veiculo.fromStringParaPersistencia(""));
        assertNull(Veiculo.fromStringParaPersistencia("null"));
    }
}