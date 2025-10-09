package com.uberpb.app;

import com.uberpb.model.Motorista;
import com.uberpb.model.Passageiro;
import com.uberpb.model.Usuario;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import java.util.ArrayList;
import java.util.List;

import static com.uberpb.app.ConsoleUI.ok;

public final class ProvedorDependencias {
    private ProvedorDependencias() {}

    private static RepositorioUsuario rUsuario() { return new ImplRepositorioUsuarioArquivo(); }
    private static RepositorioCorrida rCorrida() { return new ImplRepositorioCorridaArquivo(); }
    private static RepositorioOferta rOferta()   { return new ImplRepositorioOfertaArquivo(); }
    private static RepositorioAvaliacao rAval()  { return new ImplRepositorioAvaliacaoArquivo(); }

    public static ContextoAplicacao fornecerContexto() {
        var ru = rUsuario();
        var rc = rCorrida();
        var ro = rOferta();
        var ra = rAval();

        var sCadastro = new ServicoCadastro(ru);
        var sAuth     = new ServicoAutenticacao(ru);
        var sCorrida  = new ServicoCorrida(rc, ru);
        var sOferta   = new ServicoOferta(ro, ru, rc);
        var sValMot   = new ServicoValidacaoMotorista(ru);
        var sPagto    = new ServicoPagamento(rc, ru);            // construtor incluído
        var sAval     = new ServicoAvaliacao(ra, rc, ru);
        var sOtim     = new ServicoOtimizacaoRota();             // construtor padrão
        var sLocal    = new ServicoLocalizacao();                // construtor padrão
        var sDir      = new ServicoDirecionamentoCorrida(ru, rc);// construtor incluído
        var sEta      = new EstimativaChegada();                 // construtor padrão
        var sAdmin    = new ServicoAdmin(ru, rc);
        var ger       = new GerenciadorCorridasAtivas();

        var sessao = new Sessao();

        return new ContextoAplicacao(sessao, ru, sCadastro, sAuth,
                rc, sCorrida, ro, sOferta, sValMot, sPagto, sAval,
                sOtim, sLocal, sDir, sEta, sAdmin, ger);
    }

    public static List<Comando> fornecerComandosPadrao() {
        List<Comando> c = new ArrayList<>();
        c.add(new LoginComando());
        c.add(new CadastrarPassageiroComando());
        c.add(new CadastrarMotoristaComando());
        c.add(new CompletarCadastroMotoristaComando());
        c.add(new SolicitarCorridaComando());
        c.add(new MotoristaVerOfertasComando());
        c.add(new AcompanharCorridaComando());
        c.add(new OtimizarRotaComando());
        c.add(new ConcluirCorridaComando());
        c.add(new DetalhesPagamentoComando());
        c.add(new AvaliarCorridaComando());
        c.add(new VisualizarAvaliacoesComando());
        c.add(new VisualizarCorridaComando());
        c.add(new VisualizarHistoricoComando());
        c.add(new AdminComando());
        c.add(new ComandoFuncional("Logout", u -> u != null, (ctx,in)-> { ctx.sessao.deslogar(); ok("Sessão encerrada."); }));

        // utilitário simples p/ listagem rápida (mantido)
        c.add(new ComandoFuncional("Listar Usuários", u -> u != null, (ctx, in) -> {
            var lista = ctx.repositorioUsuario.buscarTodos();
            for (Usuario u2 : lista) {
                if (u2 instanceof Motorista m) {
                    var cats = (m.getVeiculo()!=null && m.getVeiculo().getCategoriasDisponiveis()!=null)
                            ? m.getVeiculo().getCategoriasDisponiveis().toString() : "[]";
                    System.out.println("[MOTORISTA] " + m.getEmail()
                            + " | CNH: " + m.isCnhValida()
                            + " | CRLV: " + m.isCrlvValido()
                            + " | " + (m.isContaAtiva() ? "Ativo" : "Inativo")
                            + " | Categorias: " + cats);
                } else if (u2 instanceof Passageiro) {
                    System.out.println("[PASSAGEIRO] " + u2.getEmail());
                } else {
                    System.out.println("[USUARIO] " + u2.getEmail());
                }
            }
        }));
        return c;
    }
}
