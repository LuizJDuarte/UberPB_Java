package com.uberpb.service;

import com.uberpb.model.CategoriaVeiculo;
import com.uberpb.model.Motorista;
import com.uberpb.model.Usuario;
import com.uberpb.model.Veiculo;
import com.uberpb.repository.RepositorioUsuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicoValidacaoMotoristaTest {

    private RepositorioUsuario repoUsuario;
    private ServicoValidacaoMotorista servico;

    @BeforeEach
    void setUp() {
        repoUsuario = mock(RepositorioUsuario.class);
        servico = new ServicoValidacaoMotorista(repoUsuario);
    }

    @Test
    void deveRegistrarMotoristaComUberX() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);
        when(veiculo.getAno()).thenReturn(2015);
        when(veiculo.getCapacidadePassageiros()).thenReturn(4);
        when(veiculo.getCor()).thenReturn("branco");
        when(veiculo.getTamanhoPortaMalas()).thenReturn("M");

        Motorista resultado = servico.registrarDocumentosEVeiculo(
                "motorista@email.com", true, true, veiculo
        );

        verify(motorista).setCnhValida(true);
        verify(motorista).setCrlvValido(true);
        verify(motorista).setVeiculo(veiculo);
        verify(repoUsuario).atualizar(motorista);

        assertNotNull(resultado);
    }

    @Test
    void deveAdicionarCategoriaComfort() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);
        when(veiculo.getAno()).thenReturn(2019);
        when(veiculo.getCapacidadePassageiros()).thenReturn(4);
        when(veiculo.getCor()).thenReturn("prata");
        when(veiculo.getTamanhoPortaMalas()).thenReturn("M");

        servico.registrarDocumentosEVeiculo("motorista@email.com", true, true, veiculo);

        verify(veiculo).setCategoriasDisponiveis(argThat(lista ->
                lista.contains(CategoriaVeiculo.COMFORT)
        ));
    }

    @Test
    void deveAdicionarCategoriaBlack() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);
        when(veiculo.getAno()).thenReturn(2021);
        when(veiculo.getCapacidadePassageiros()).thenReturn(4);
        when(veiculo.getCor()).thenReturn("preto");
        when(veiculo.getTamanhoPortaMalas()).thenReturn("M");

        servico.registrarDocumentosEVeiculo("motorista@email.com", true, true, veiculo);

        verify(veiculo).setCategoriasDisponiveis(argThat(lista ->
                lista.contains(CategoriaVeiculo.BLACK)
        ));
    }

    @Test
    void deveAdicionarCategoriaBag() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);
        when(veiculo.getAno()).thenReturn(2018);
        when(veiculo.getCapacidadePassageiros()).thenReturn(4);
        when(veiculo.getCor()).thenReturn("azul");
        when(veiculo.getTamanhoPortaMalas()).thenReturn("G");

        servico.registrarDocumentosEVeiculo("motorista@email.com", true, true, veiculo);

        verify(veiculo).setCategoriasDisponiveis(argThat(lista ->
                lista.contains(CategoriaVeiculo.BAG)
        ));
    }

    @Test
    void deveAdicionarCategoriaXL() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);
        when(veiculo.getAno()).thenReturn(2019);
        when(veiculo.getCapacidadePassageiros()).thenReturn(6);
        when(veiculo.getCor()).thenReturn("cinza");
        when(veiculo.getTamanhoPortaMalas()).thenReturn("M");

        servico.registrarDocumentosEVeiculo("motorista@email.com", true, true, veiculo);

        verify(veiculo).setCategoriasDisponiveis(argThat(lista ->
                lista.contains(CategoriaVeiculo.XL)
        ));
    }

    @Test
    void naoDeveAtivarContaSemDocumentos() {

        Motorista motorista = mock(Motorista.class);
        Veiculo veiculo = mock(Veiculo.class);

        when(repoUsuario.buscarPorEmail("motorista@email.com")).thenReturn(motorista);
        when(veiculo.getAno()).thenReturn(2019);
        when(veiculo.getCapacidadePassageiros()).thenReturn(4);
        when(veiculo.getCor()).thenReturn("branco");
        when(veiculo.getTamanhoPortaMalas()).thenReturn("M");

        servico.registrarDocumentosEVeiculo("motorista@email.com", false, false, veiculo);

        verify(motorista).setContaAtiva(false);
    }

    @Test
    void deveLancarErroSeUsuarioNaoForMotorista() {

        Usuario usuario = mock(Usuario.class);

        when(repoUsuario.buscarPorEmail("user@email.com")).thenReturn(usuario);

        Veiculo veiculo = mock(Veiculo.class);

        assertThrows(IllegalArgumentException.class, () -> {
            servico.registrarDocumentosEVeiculo("user@email.com", true, true, veiculo);
        });
    }
}
