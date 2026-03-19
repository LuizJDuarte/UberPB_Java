package com.uberpb.app;

import com.uberpb.model.Localizacao;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.ServicoCadastro;
import com.uberpb.service.ServicoLocalizacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadastrarPassageiroComandoTest {

    private CadastrarPassageiroComando comando;
    private ContextoAplicacao contexto;

    private ServicoCadastro servicoCadastro;
    private ServicoLocalizacao servicoLocalizacao;
    private RepositorioUsuario repositorioUsuario;

    @BeforeEach
    void setup() throws Exception {
        comando = new CadastrarPassageiroComando();

        contexto = new ContextoAplicacao(); // Construtor padrão, campos serão injetados
        servicoCadastro = mock(ServicoCadastro.class);
        servicoLocalizacao = mock(ServicoLocalizacao.class);
        repositorioUsuario = mock(RepositorioUsuario.class);

        setField(contexto, "servicoCadastro", servicoCadastro);
        setField(contexto, "servicoLocalizacao", servicoLocalizacao);
        setField(contexto, "repositorioUsuario", repositorioUsuario);
    }

    // Helper para injetar campos privados
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = ContextoAplicacao.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ===============================
    // VISIBILIDADE
    // ===============================
    @Test
    void deveSerVisivelQuandoUsuarioNaoLogado() {
        assertTrue(comando.visivelPara(null));
    }

    @Test
    void naoDeveSerVisivelQuandoUsuarioLogado() {
        Usuario usuario = mock(Usuario.class);
        assertFalse(comando.visivelPara(usuario));
    }

    // ===============================
    // EXECUÇÃO
    // ===============================
    @Test
    void deveCadastrarPassageiroComSucesso() {

        // Input do usuário para o Scanner
        String input = String.join("\n",
                "passageiro@email.com",
                "senha123",
                "Bairro Liberdade"
        ) + "\n";

        Scanner scanner = new Scanner(input);

        // Mock do passageiro retornado pelo serviço de cadastro
        Passageiro passageiroMock = mock(Passageiro.class);
        when(servicoCadastro.cadastrarPassageiro(
                anyString(), anyString()
        )).thenReturn(passageiroMock);

        // Mock da localização geocodificada
        Localizacao locMock = mock(Localizacao.class);
        when(servicoLocalizacao.geocodificar(anyString())).thenReturn(locMock);

        // Executa o comando
        assertDoesNotThrow(() -> comando.executar(contexto, scanner));

        // Verifica se os serviços foram chamados corretamente
        verify(servicoCadastro).cadastrarPassageiro(
                eq("passageiro@email.com"),
                eq("senha123")
        );

        verify(servicoLocalizacao).geocodificar("Bairro Liberdade");

        // Verifica se a localização foi setada no passageiro
        verify(passageiroMock).setLocalizacao(locMock);
    }
}