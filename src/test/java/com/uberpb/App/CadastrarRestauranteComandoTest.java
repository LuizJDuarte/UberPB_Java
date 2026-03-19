package com.uberpb.app;

import com.uberpb.model.Localizacao;
import com.uberpb.model.Restaurante;
import com.uberpb.model.Usuario;
import com.uberpb.service.ServicoCadastro;
import com.uberpb.service.ServicoLocalizacao;
import com.uberpb.repository.RepositorioRestaurante;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadastrarRestauranteComandoTest {

    private CadastrarRestauranteComando comando;
    private ContextoAplicacao contexto;

    private ServicoCadastro servicoCadastro;
    private ServicoLocalizacao servicoLocalizacao;
    private RepositorioRestaurante repositorio;

    @BeforeEach
    void setup() throws Exception {
        comando = new CadastrarRestauranteComando();

        contexto = new ContextoAplicacao(); 
        servicoCadastro = mock(ServicoCadastro.class);
        servicoLocalizacao = mock(ServicoLocalizacao.class);
        repositorio = mock(RepositorioRestaurante.class);

        // INJETANDO NOS CAMPOS PRIVADOS/FINAL VIA REFLECTION
        setField(contexto, "servicoCadastro", servicoCadastro);
        setField(contexto, "servicoLocalizacao", servicoLocalizacao);
        setField(contexto, "repositorioRestaurante", repositorio);
    }

    // método helper de reflection
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = ContextoAplicacao.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // =========================
    // VISIBILIDADE
    // =========================

    @Test
    void deveSerVisivelQuandoUsuarioNaoLogado() {
        assertTrue(comando.visivelPara(null));
    }

    @Test
    void naoDeveSerVisivelQuandoUsuarioLogado() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    // =========================
    // EXECUÇÃO
    // =========================

    @Test
    void deveCadastrarRestauranteComSucesso() {

        String input = String.join("\n",
                "restaurante@email.com",
                "123456",
                "Meu Restaurante",
                "Rua Teste",
                "5.50",
                "30"
        ) + "\n";

        Scanner scanner = new Scanner(input);

        Restaurante restauranteMock = mock(Restaurante.class);

        when(servicoCadastro.cadastrarRestaurante(
                anyString(), anyString(), anyString(), anyString()
        )).thenReturn(restauranteMock);

        Localizacao localizacaoMock = mock(Localizacao.class);
        when(localizacaoMock.latitude()).thenReturn(10.0);
        when(localizacaoMock.longitude()).thenReturn(20.0);

        when(servicoLocalizacao.geocodificar(anyString()))
                .thenReturn(localizacaoMock);

        assertDoesNotThrow(() ->
                comando.executar(contexto, scanner)
        );

        verify(servicoCadastro).cadastrarRestaurante(
                eq("restaurante@email.com"),
                eq("123456"),
                eq("Meu Restaurante"),
                eq("")
        );

        verify(servicoLocalizacao).geocodificar("Rua Teste");

        verify(restauranteMock).setLocalizacao(localizacaoMock);
        verify(restauranteMock).setTaxaEntrega(5.50);
        verify(restauranteMock).setTempoEstimadoEntregaMinutos(30);
        verify(restauranteMock).setContaAtiva(true);

        verify(repositorio).salvar(restauranteMock);
    }
}