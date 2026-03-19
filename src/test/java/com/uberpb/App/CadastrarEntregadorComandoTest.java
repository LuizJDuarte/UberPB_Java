package com.uberpb.app;

import com.uberpb.model.Entregador;
import com.uberpb.model.Usuario;
import com.uberpb.model.Localizacao;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.ServicoCadastro;
import com.uberpb.service.ServicoLocalizacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadastrarEntregadorComandoTest {

    private CadastrarEntregadorComando comando;
    private ContextoAplicacao contexto;

    private ServicoCadastro servicoCadastro;
    private ServicoLocalizacao servicoLocalizacao;
    private RepositorioUsuario repositorioUsuario;

    @BeforeEach
    void setup() throws Exception {
        comando = new CadastrarEntregadorComando();

        contexto = new ContextoAplicacao(); // Construtor padrão, campos serão injetados
        servicoCadastro = mock(ServicoCadastro.class);
        servicoLocalizacao = mock(ServicoLocalizacao.class);
        repositorioUsuario = mock(RepositorioUsuario.class);

        setField(contexto, "servicoCadastro", servicoCadastro);
        setField(contexto, "servicoLocalizacao", servicoLocalizacao);
        setField(contexto, "repositorioUsuario", repositorioUsuario);
    }

    // ===============================
    // Helper de reflection para injetar campos privados
    // ===============================
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
    void deveCadastrarEntregadorComSucesso() {

        // Input do usuário para o Scanner
        String input = String.join("\n",
                "entregador@email.com",
                "Rua Central, 100",
                "senha123",
                "123456",
                "98765432100"
        ) + "\n";

        Scanner scanner = new Scanner(input);

        // Mock do entregador retornado pelo serviço de cadastro
        Entregador entregadorMock = mock(Entregador.class);
        when(servicoCadastro.cadastrarEntregador(
                anyString(), anyString(), anyString(), anyString()
        )).thenReturn(entregadorMock);

        // Mock da localização geocodificada
        Localizacao locMock = mock(Localizacao.class);
        when(servicoLocalizacao.geocodificar(anyString())).thenReturn(locMock);

        // Executa o comando
        assertDoesNotThrow(() -> comando.executar(contexto, scanner));

        // Verifica se os serviços foram chamados corretamente
        verify(servicoCadastro).cadastrarEntregador(
                eq("entregador@email.com"),
                eq("senha123"),
                eq("123456"),
                eq("98765432100")
        );

        verify(servicoLocalizacao).geocodificar("Rua Central, 100");

        // Verifica se a localização foi setada no entregador
        verify(entregadorMock).setLocalizacao(locMock);

        // Verifica se o entregador foi salvo no repositório
        verify(repositorioUsuario).salvar(entregadorMock);
    }
}