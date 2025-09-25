package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServicoValidacaoMotoristaTest {

    private ServicoValidacaoMotorista servicoValidacao;
    private RepositorioUsuario repositorioUsuario;
    private Motorista motorista;

    // In-memory repository for isolated tests
    static class InMemoryRepositorioUsuario implements RepositorioUsuario {
        private final Map<String, Usuario> database = new HashMap<>();

        @Override
        public void salvar(Usuario usuario) {
            database.put(usuario.getEmail(), usuario);
        }

        @Override
        public void atualizar(Usuario usuario) {
            database.put(usuario.getEmail(), usuario);
        }

        @Override
        public Usuario buscarPorEmail(String email) {
            return database.get(email);
        }

        @Override
        public List<Usuario> buscarTodos() {
            return List.copyOf(database.values());
        }
    }

    @BeforeEach
    public void setUp() {
        repositorioUsuario = new InMemoryRepositorioUsuario();
        servicoValidacao = new ServicoValidacaoMotorista(repositorioUsuario);
        motorista = new Motorista("motorista@teste.com", "senhaHash123");
        repositorioUsuario.salvar(motorista);
    }

    @Test
    public void testAtivarMotoristaComDocumentosEVeiculoValidos() {
        Veiculo veiculo = new Veiculo("Toyota Corolla", 2019, "ABC-1234", "Prata", 4, "M");

        servicoValidacao.validarVeiculoEDocumentos(motorista, veiculo, true, true);

        assertTrue(motorista.isContaAtiva());
        assertTrue(motorista.isCnhValida());
        assertTrue(motorista.isCrlvValido());
        assertNotNull(motorista.getVeiculo());
        assertEquals("Toyota Corolla", motorista.getVeiculo().getModelo());
    }

    @Test
    public void testNaoAtivarMotoristaComCnhInvalida() {
        Veiculo veiculo = new Veiculo("VW Gol", 2015, "DEF-5678", "Branco", 4, "P");

        assertThrows(IllegalArgumentException.class, () -> {
            servicoValidacao.validarVeiculoEDocumentos(motorista, veiculo, false, true);
        });

        assertFalse(motorista.isContaAtiva());
        assertFalse(motorista.isCnhValida());
    }

    @Test
    public void testVeiculoQualificaParaUberX() {
        Veiculo veiculo = new Veiculo("Fiat Uno", 2010, "GHI-9012", "Vermelho", 4, "P");
        servicoValidacao.validarVeiculoEDocumentos(motorista, veiculo, true, true);

        List<CategoriaVeiculo> categorias = motorista.getVeiculo().getCategoriasDisponiveis();
        assertTrue(categorias.contains(CategoriaVeiculo.UBERX));
        assertEquals(1, categorias.size());
    }

    @Test
    public void testVeiculoQualificaParaComfort() {
        Veiculo veiculo = new Veiculo("Honda Civic", 2018, "JKL-3456", "Preto", 4, "M");
        servicoValidacao.validarVeiculoEDocumentos(motorista, veiculo, true, true);

        List<CategoriaVeiculo> categorias = motorista.getVeiculo().getCategoriasDisponiveis();
        assertTrue(categorias.contains(CategoriaVeiculo.UBERX));
        assertTrue(categorias.contains(CategoriaVeiculo.COMFORT));
    }

    @Test
    public void testVeiculoQualificaParaBlack() {
        Veiculo veiculo = new Veiculo("BMW 320i", 2021, "MNO-7890", "Preto", 4, "M");
        servicoValidacao.validarVeiculoEDocumentos(motorista, veiculo, true, true);

        List<CategoriaVeiculo> categorias = motorista.getVeiculo().getCategoriasDisponiveis();
        assertTrue(categorias.contains(CategoriaVeiculo.UBERX));
        assertTrue(categorias.contains(CategoriaVeiculo.COMFORT));
        assertTrue(categorias.contains(CategoriaVeiculo.BLACK));
    }

    @Test
    public void testVeiculoQualificaParaBag() {
        Veiculo veiculo = new Veiculo("Renault Duster", 2019, "PQR-1234", "Branco", 4, "G");
        servicoValidacao.validarVeiculoEDocumentos(motorista, veiculo, true, true);

        List<CategoriaVeiculo> categorias = motorista.getVeiculo().getCategoriasDisponiveis();
        assertTrue(categorias.contains(CategoriaVeiculo.UBERX));
        assertTrue(categorias.contains(CategoriaVeiculo.BAG));
    }

    @Test
    public void testVeiculoQualificaParaXL() {
        Veiculo veiculo = new Veiculo("Chevrolet Spin", 2020, "STU-5678", "Azul", 7, "G");
        servicoValidacao.validarVeiculoEDocumentos(motorista, veiculo, true, true);

        List<CategoriaVeiculo> categorias = motorista.getVeiculo().getCategoriasDisponiveis();
        assertTrue(categorias.contains(CategoriaVeiculo.UBERX));
        assertTrue(categorias.contains(CategoriaVeiculo.COMFORT));
        assertTrue(categorias.contains(CategoriaVeiculo.BAG));
        assertTrue(categorias.contains(CategoriaVeiculo.XL));
    }
}