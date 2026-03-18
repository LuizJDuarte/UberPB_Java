package com.uberpb.service;

import com.uberpb.model.Localizacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoOtimizacaoRotaTest {

    private ServicoOtimizacaoRota servico;

    @BeforeEach
    void setUp() {
        servico = new ServicoOtimizacaoRota();
    }

    @Test
    public void VisualizacaoDaRota() {
        ServicoOtimizacaoRota servico = new ServicoOtimizacaoRota();
        Localizacao origem = new Localizacao(-7.22, -35.88);
        Localizacao destino = new Localizacao(-7.25, -35.91);

        var rota = servico.calcularRotaOtimizada(origem, destino);

        // Testa se a lista de pontos tem exatamente 5 itens
        assertEquals(5, rota.getPontosRota().size(), "A rota deveria ter 5 pontos");
        
        // Testa se a distância é maior que zero
        assertTrue(rota.getDistanciaKm() > 0, "A distância deveria ser positiva");
    }


    @Test
    void deveCalcularRotaOtimizada() {

        Localizacao origem = new Localizacao(-7.23072, -35.8817);
        Localizacao destino = new Localizacao(-7.25000, -35.9000);

        RotaOtimizada rota = servico.calcularRotaOtimizada(origem, destino);

        assertNotNull(rota);
    }

    @Test
    void deveManterOrigemEDestinoCorretos() {

        Localizacao origem = new Localizacao(-7.23, -35.88);
        Localizacao destino = new Localizacao(-7.25, -35.90);

        RotaOtimizada rota = servico.calcularRotaOtimizada(origem, destino);

        assertEquals(origem, rota.getOrigem());
        assertEquals(destino, rota.getDestino());
    }

    @Test
    void deveGerarPontosDaRota() {

        Localizacao origem = new Localizacao(-7.23, -35.88);
        Localizacao destino = new Localizacao(-7.25, -35.90);

        RotaOtimizada rota = servico.calcularRotaOtimizada(origem, destino);

        assertNotNull(rota.getPontosRota());
        assertTrue(rota.getPontosRota().size() >= 2);
    }

    @Test
    void distanciaOtimizadaDeveSerPositiva() {

        Localizacao origem = new Localizacao(-7.23, -35.88);
        Localizacao destino = new Localizacao(-7.25, -35.90);

        RotaOtimizada rota = servico.calcularRotaOtimizada(origem, destino);

        assertTrue(rota.getDistanciaKm() > 0);
    }

    @Test
    void economiaDeTempoDeveSerPositiva() {

        Localizacao origem = new Localizacao(-7.23, -35.88);
        Localizacao destino = new Localizacao(-7.25, -35.90);

        RotaOtimizada rota = servico.calcularRotaOtimizada(origem, destino);

        assertTrue(rota.getEconomiaTempoPercentual() >= 0);
    }
}
