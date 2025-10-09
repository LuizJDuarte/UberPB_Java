package com.uberpb.service;

import com.uberpb.exceptions.OperacaoNaoPermitidaException;
import com.uberpb.model.Passageiro;
import com.uberpb.repository.ImplRepositorioCorridaArquivo;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServicoAdminTest {
    @Test
    void removerUsuarioSemSerAdminDisparaExcecao() {
        var admin = new ServicoAdmin(new ImplRepositorioUsuarioArquivo(), new ImplRepositorioCorridaArquivo());
        var usuarioComum = new Passageiro("x@x", "hash");
        assertThrows(OperacaoNaoPermitidaException.class,
                () -> admin.removerUsuario("alvo@x", usuarioComum));
    }
}
