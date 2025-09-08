package com.uberpb.app;

import static com.uberpb.app.ConsoleUI.*;
import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.ImplRepositorioCorridaArquivo;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.ServicoAutenticacao;
import com.uberpb.service.ServicoCadastro;
import com.uberpb.service.ServicoCorrida;
import java.util.ArrayList;
import java.util.List;

public final class ProvedorDependencias {
    private ProvedorDependencias() {}

    // ===== Repositórios =====
    private static RepositorioUsuario repoUsuario() {
        return new ImplRepositorioUsuarioArquivo();
    }
    private static RepositorioCorrida repoCorrida() {
        return new ImplRepositorioCorridaArquivo();
    }

    // ===== Serviços =====
    private static ServicoCadastro servicoCadastro(RepositorioUsuario rUsuario) {
        return new ServicoCadastro(rUsuario);
    }
    private static ServicoAutenticacao servicoAutenticacao(RepositorioUsuario rUsuario) {
        return new ServicoAutenticacao(rUsuario);
    }
    private static ServicoCorrida servicoCorrida(RepositorioCorrida rCorrida, RepositorioUsuario rUsuario) {
        return new ServicoCorrida(rCorrida, rUsuario);
    }

    // ===== Sessão/Contexto =====
    public static ContextoAplicacao fornecerContexto() {
        RepositorioUsuario rUsuario = repoUsuario();
        RepositorioCorrida rCorrida = repoCorrida();

        ServicoCadastro sCadastro = servicoCadastro(rUsuario);
        ServicoAutenticacao sAuth = servicoAutenticacao(rUsuario);
        ServicoCorrida sCorrida = servicoCorrida(rCorrida, rUsuario);

        Sessao sessao = new Sessao();

        return new ContextoAplicacao(
                sessao,
                rUsuario,
                sCadastro,
                sAuth,
                rCorrida,
                sCorrida
        );
    }

    // ===== Menu (Command pattern) =====
    public static List<Comando> fornecerComandosPadrao() {
        List<Comando> comandos = new ArrayList<>();

        // Comandos "de negócio" (mantidos como classes) — clareza > micro-otimização
        comandos.add(new LoginComando());
        comandos.add(new CadastrarPassageiroComando());
        comandos.add(new CadastrarMotoristaComando());
        comandos.add(new SolicitarCorridaComando());
        comandos.add(new VisualizarCorridaComando());
        comandos.add(new EstimativaCorridaComando());

        // Comandos utilitários como "funcionais" — reduzem arquivos
        comandos.add(new ComandoFuncional(
                "Listar Usuários",
                u -> u != null, // visível apenas logado
                (ctx, in) -> {
                    var lista = ctx.repositorioUsuario.buscarTodos();
                    if (lista.isEmpty()) { System.out.println("(sem usuários cadastrados)"); return; }
                    for (Usuario u2 : lista) {
                        if (u2 instanceof Motorista m) {
                            String status = m.isContaAtiva() ? "Ativo" : "Inativo";
                            System.out.println("[MOTORISTA] " + m.getEmail()
                                    + " | CNH: " + m.isCnhValida()
                                    + " | CRLV: " + m.isCrlvValido()
                                    + " | " + status);
                        } else if (u2 instanceof Passageiro) {
                            System.out.println("[PASSAGEIRO] " + u2.getEmail());
                        } else {
                            System.out.println("[USUARIO] " + u2.getEmail());
                        }
                    }
                }
        ));

        comandos.add(new ComandoFuncional(
                "Logout",
                u -> u != null,
                (ctx, in) -> { ctx.sessao.deslogar(); ok("Sessão encerrada."); }
        ));

        return comandos;
    }
}
