package com.uberpb.app;

import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.ImplRepositorioCorridaArquivo;
import com.uberpb.repository.ImplRepositorioOfertaArquivo;
import com.uberpb.repository.ImplRepositorioUsuarioArquivo;
import com.uberpb.repository.RepositorioCorrida;
import com.uberpb.repository.RepositorioOferta;
import com.uberpb.repository.RepositorioUsuario;
import com.uberpb.service.*;

import java.util.ArrayList;
import java.util.List;

import static com.uberpb.app.ConsoleUI.*;

public final class ProvedorDependencias {
    private ProvedorDependencias() {}

    private static RepositorioUsuario repoUsuario() { return new ImplRepositorioUsuarioArquivo(); }
    private static RepositorioCorrida repoCorrida() { return new ImplRepositorioCorridaArquivo(); }
    private static RepositorioOferta repoOferta() { return new ImplRepositorioOfertaArquivo(); }

    private static ServicoCadastro servicoCadastro(RepositorioUsuario rUsuario) { return new ServicoCadastro(rUsuario); }
    private static ServicoAutenticacao servicoAutenticacao(RepositorioUsuario rUsuario) { return new ServicoAutenticacao(rUsuario); }
    private static ServicoCorrida servicoCorrida(RepositorioCorrida rCorrida, RepositorioUsuario rUsuario) { return new ServicoCorrida(rCorrida, rUsuario); }
    private static ServicoOferta servicoOferta(RepositorioOferta rOferta, RepositorioUsuario rUsuario, RepositorioCorrida rCorrida) { return new ServicoOferta(rOferta, rUsuario, rCorrida); }
    private static ServicoValidacaoMotorista servicoValidacaoMotorista(RepositorioUsuario rUsuario) { return new ServicoValidacaoMotorista(rUsuario); }

    public static ContextoAplicacao fornecerContexto() {
        RepositorioUsuario rUsuario = repoUsuario();
        RepositorioCorrida rCorrida = repoCorrida();
        RepositorioOferta rOferta = repoOferta();

        ServicoCadastro sCadastro = servicoCadastro(rUsuario);
        ServicoAutenticacao sAuth = servicoAutenticacao(rUsuario);
        ServicoCorrida sCorrida = servicoCorrida(rCorrida, rUsuario);
        ServicoOferta sOferta = servicoOferta(rOferta, rUsuario, rCorrida);
        ServicoValidacaoMotorista sValidacao = servicoValidacaoMotorista(rUsuario);

        Sessao sessao = new Sessao();

        return new ContextoAplicacao(sessao, rUsuario, sCadastro, sAuth, rCorrida, sCorrida, rOferta, sOferta, sValidacao);
    }

    public static List<Comando> fornecerComandosPadrao() {
        List<Comando> comandos = new ArrayList<>();

        comandos.add(new LoginComando());
        comandos.add(new CadastrarPassageiroComando());
        comandos.add(new CadastrarMotoristaComando());
        comandos.add(new SolicitarCorridaComando());
        comandos.add(new VisualizarCorridaComando());
        comandos.add(new MotoristaVerOfertasComando());
        comandos.add(new CompletarCadastroMotoristaComando()); // << NOVO (RF02)

        // Listar usuários (mostra categorias do motorista, se houver)
        comandos.add(new ComandoFuncional(
                "Listar Usuários", u -> u != null,
                (ctx, in) -> {
                    var lista = ctx.repositorioUsuario.buscarTodos();
                    if (lista.isEmpty()) { System.out.println("(sem usuários cadastrados)"); return; }
                    for (Usuario u2 : lista) {
                        if (u2 instanceof Motorista m) {
                            String status = m.isContaAtiva() ? "Ativo" : "Inativo";
                            String cats = (m.getVeiculo() != null && m.getVeiculo().getCategoriasDisponiveis()!=null)
                                    ? m.getVeiculo().getCategoriasDisponiveis().toString()
                                    : "[]";
                            System.out.println("[MOTORISTA] " + m.getEmail()
                                    + " | CNH: " + m.isCnhValida()
                                    + " | CRLV: " + m.isCrlvValido()
                                    + " | " + status
                                    + " | Categorias: " + cats);
                        } else if (u2 instanceof Passageiro) {
                            System.out.println("[PASSAGEIRO] " + u2.getEmail());
                        } else {
                            System.out.println("[USUARIO] " + u2.getEmail());
                        }
                    }
                }
        ));
        comandos.add(new ComandoFuncional("Logout", u -> u != null, (ctx, in) -> { ctx.sessao.deslogar(); ok("Sessão encerrada."); }));

        return comandos;
    }
}
