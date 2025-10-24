package com.uberpb.service;

import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServicoAdminTest {

    @Test
    public void testLimparDados() {
        RepositorioUsuario repoUsuario = mock(RepositorioUsuario.class);
        RepositorioCorrida repoCorrida = mock(RepositorioCorrida.class);
        ServicoAdmin servicoAdmin = new ServicoAdmin(repoUsuario, repoCorrida);

        servicoAdmin.limparDados();

        verify(repoUsuario).limpar();
        verify(repoCorrida).limpar();
    }
}
