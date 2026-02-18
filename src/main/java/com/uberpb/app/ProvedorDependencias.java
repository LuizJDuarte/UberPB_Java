package com.uberpb.app;

import com.uberpb.model.Administrador;
import com.uberpb.repository.*;
import com.uberpb.service.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public final class ProvedorDependencias {
    private ProvedorDependencias() {}

    public static ContextoAplicacao fornecerContexto() {

        // ========================
        // REPOSITÓRIOS
        // ========================
        RepositorioUsuario repoUsuario = ImplRepositorioUsuarioArquivo.getInstance();
        RepositorioCorrida repoCorrida = ImplRepositorioCorridaArquivo.getInstance();
        RepositorioRestaurante repoRestaurante = ImplRepositorioRestauranteArquivo.getInstance();
        RepositorioOferta repoOferta = ImplRepositorioOfertaArquivo.getInstance();
        RepositorioAvaliacao repoAvaliacao = ImplRepositorioAvaliacaoArquivo.getInstance();

        // NOVO — PEDIDOS
        RepositorioPedido repoPedido = ImplRepositorioPedidoArquivo.getInstance();

        // ========================
        // SEED ADMIN
        // ========================
        semearAdminSePreciso(repoUsuario);

        // ========================
        // SERVIÇOS AUXILIARES
        // ========================
        EstimativaCorrida estimativaCorrida = new EstimativaCorrida(0, 0, 0);
        ServicoLocalizacao servLoc = new ServicoLocalizacao();

        // ========================
        // SERVIÇOS DE NEGÓCIO
        // ========================
        ServicoCadastro servCadastro = new ServicoCadastro(repoUsuario);
        servCadastro.setRepositorioRestaurante(repoRestaurante);

        ServicoAutenticacao servAuth = new ServicoAutenticacao(repoUsuario, repoRestaurante);
        ServicoCorrida servCorrida = new ServicoCorrida(repoCorrida, repoUsuario, estimativaCorrida, servLoc);
        ServicoOferta servOferta = new ServicoOferta(repoOferta, repoUsuario, repoCorrida);
        ServicoPagamento servPagto = new ServicoPagamento(repoCorrida, repoUsuario);
        ServicoAvaliacao servAval = new ServicoAvaliacao(repoAvaliacao, repoCorrida, repoUsuario);

        //  NOVO — SERVIÇO DE PEDIDOS
        ServicoPedido servPedido = new ServicoPedido(repoPedido);

        GerenciadorCorridasAtivas ger = new GerenciadorCorridasAtivas();
        Sessao sessao = new Sessao();

        // Carrinho
        ServicoCarrinho servCarrinho = new ServicoCarrinho();

        // Serviços opcionais
        ServicoValidacaoMotorista servValid = null;
        ServicoOtimizacaoRota servOpt = null;
        ServicoDirecionamentoCorrida servDirec = null;
        EstimativaChegada servEta = null;
        ServicoAdmin servAdmin = null;

        // ========================
        // CONTEXTO (ATUALIZADO)
        // ========================
        return new ContextoAplicacao(
            sessao,
            repoUsuario,
            repoRestaurante,
            servCadastro,
            servAuth,
            repoCorrida,
            servCorrida,
            repoOferta,
            repoAvaliacao,
            servOferta,
            servValid,
            servPagto,
            servAval,
            servOpt,
            servLoc,
            servDirec,
            servEta,
            servAdmin,
            ger,
            servCarrinho,

            // 🆕 NOVOS (IMPORTANTE!)
            repoPedido,
            servPedido
        );
    }

    public static List<Comando> fornecerComandos() {
        List<Comando> lista = new ArrayList<>();

        lista.add(new LoginComando());
        lista.add(new LogoutComando());

        lista.add(new CadastrarPassageiroComando());
        lista.add(new CadastrarMotoristaComando());
        lista.add(new CadastrarEntregadorComando());
        lista.add(new CadastrarRestauranteComando());
        lista.add(new CompletarCadastroMotoristaComando());
        lista.add(new VerificarStatusAprovacaoComando());

        lista.add(new SolicitarCorridaComando());
        lista.add(new CancelarCorridaComando());
        lista.add(new AvaliarMotoristaComando());

        lista.add(new VisualizarRestaurantesComando());
        lista.add(new VisualizarCarrinhoComando());

        lista.add(new FicarOnlineOfflineComando());
        lista.add(new MotoristaVerOfertasComando());
        lista.add(new AtualizarLocalizacaoComando());

        lista.add(new GerenciarCardapioComando());
        lista.add(new VisualizarPedidosComando());

        lista.add(new VisualizarHistoricoComando());
        lista.add(new MenuAdminComando());

        return lista;
    }

    private static void semearAdminSePreciso(RepositorioUsuario ru) {
        var existente = ru.buscarPorEmail("admin@uberpb.com");
        if (existente != null) return;

        String senhaHash = sha256Hex("admin123");
        var admin = new Administrador("admin@uberpb.com", senhaHash);
        ru.salvar(admin);
    }

    private static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(s.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder(dig.length * 2);
            for (byte b : dig) sb.append(String.format("%02x", b));

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao calcular SHA-256", e);
        }
    }
}
