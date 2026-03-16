package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.repository.RepositorioUsuario;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProvedorDependenciasTest {

    @Test
    void testFornecerContextoNaoNulo() {

        ContextoAplicacao ctx = ProvedorDependencias.fornecerContexto();

        assertNotNull(ctx);
        assertNotNull(ctx.sessao);
        assertNotNull(ctx.servicoLocalizacao);
        assertNotNull(ctx.servicoCorrida);
        assertNotNull(ctx.servicoPagamento);
        assertNotNull(ctx.servicoPedido);
    }

    @Test
    void testFornecerComandosListaNaoVazia() {

        List<Comando> comandos = ProvedorDependencias.fornecerComandos();

        assertNotNull(comandos);
        assertFalse(comandos.isEmpty());
    }

    @Test
    void testListaContemComandosPrincipais() {

        List<Comando> comandos = ProvedorDependencias.fornecerComandos();

        boolean temLogin = comandos.stream().anyMatch(c -> c instanceof LoginComando);
        boolean temLogout = comandos.stream().anyMatch(c -> c instanceof LogoutComando);
        boolean temSolicitarCorrida = comandos.stream().anyMatch(c -> c instanceof SolicitarCorridaComando);
        boolean temVisualizarRestaurantes = comandos.stream().anyMatch(c -> c instanceof VisualizarRestaurantesComando);

        assertTrue(temLogin);
        assertTrue(temLogout);
        assertTrue(temSolicitarCorrida);
        assertTrue(temVisualizarRestaurantes);
    }

    @Test
    void testSemearAdminCriado() {

        ContextoAplicacao ctx = ProvedorDependencias.fornecerContexto();

        RepositorioUsuario repo = ctx.repositorioUsuario;

        var usuario = repo.buscarPorEmail("admin@uberpb.com");

        assertNotNull(usuario);
        assertTrue(usuario instanceof Administrador);
    }
}