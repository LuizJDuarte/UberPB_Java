import com.uberpb.model.*;
import com.uberpb.repository.*;
import com.uberpb.service.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Teste manual do RF24 - Aceitar/Recusar Pedidos
 */
public class TesteRF24 {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("TESTE RF24 - ACEITAR/RECUSAR PEDIDOS");
        System.out.println("========================================\n");

        // 1. Configurar dependências
        RepositorioUsuario repoUsuario = ImplRepositorioUsuarioArquivo.getInstance();
        RepositorioPedido repoPedido = ImplRepositorioPedidoArquivo.getInstance();
        RepositorioNotificacao repoNotificacao = ImplRepositorioNotificacaoArquivo.getInstance();

        ServicoLocalizacao servicoLoc = new ServicoLocalizacao();
        ServicoNotificacao servicoNotif = new ServicoNotificacao(repoNotificacao);
        ServicoEntrega servicoEntrega = new ServicoEntrega(repoUsuario, repoPedido, servicoLoc, servicoNotif);
        ServicoPedido servicoPedido = new ServicoPedido(repoPedido);

        // 2. Verificar entregadores disponíveis
        System.out.println("--- ENTREGADORES DISPONÍVEIS ---");
        List<Entregador> entregadores = servicoEntrega.listarEntregadoresDisponiveis();
        if (entregadores.isEmpty()) {
            System.out.println("⚠️  Nenhum entregador disponível no momento!");
            System.out.println("\nCriando um entregador de teste...");
            criarEntregadorTeste(repoUsuario, servicoLoc);
            entregadores = servicoEntrega.listarEntregadoresDisponiveis();
        }

        for (Entregador e : entregadores) {
            System.out.println(
                    "  ✓ " + e.getEmail() + " (Ativo: " + e.isContaAtiva() + ", Online: " + e.isDisponivel() + ")");
        }
        System.out.println();

        // 3. Criar um pedido de teste
        System.out.println("--- CRIAR PEDIDO DE TESTE ---");
        Pedido pedidoTeste = new Pedido(
                "kay@gmail.com",
                "cantinho@gmail.com",
                new ArrayList<>(),
                50.0,
                "PIX");

        // Processar o pedido (aloca entregador automaticamente)
        boolean sucesso = servicoEntrega.processarNovoPedido(pedidoTeste);

        if (sucesso) {
            System.out.println("✅ Pedido criado e entregador alocado: " + pedidoTeste.getEntregadorAlocado());
            repoPedido.salvar(pedidoTeste);
        } else {
            System.out.println("❌ Falha ao processar pedido");
            return;
        }
        System.out.println();

        // 4. Listar pedidos disponíveis para o entregador
        System.out.println("--- PEDIDOS DISPONÍVEIS PARA O ENTREGADOR ---");
        String emailEntregador = pedidoTeste.getEntregadorAlocado();
        List<Pedido> pedidosDisponiveis = servicoPedido.buscarPedidosDisponiveisParaEntregador(emailEntregador);

        System.out.println("Entregador: " + emailEntregador);
        System.out.println("Pedidos disponíveis: " + pedidosDisponiveis.size());
        for (Pedido p : pedidosDisponiveis) {
            System.out.println("  • Restaurante: " + p.getEmailRestaurante() +
                    " | Cliente: " + p.getEmailCliente() +
                    " | Total: R$ " + p.getTotal() +
                    " | Status: " + p.getStatus());
        }
        System.out.println();

        // 5. TESTE 1: ACEITAR PEDIDO
        System.out.println("========================================");
        System.out.println("TESTE 1: ACEITAR PEDIDO");
        System.out.println("========================================");

        Pedido pedidoParaAceitar = pedidosDisponiveis.get(0);
        System.out.println("Status antes: " + pedidoParaAceitar.getStatus());

        boolean aceito = servicoEntrega.aceitarPedido(emailEntregador, pedidoParaAceitar);

        System.out.println("Resultado: " + (aceito ? "✅ ACEITO" : "❌ FALHOU"));
        System.out.println("Status depois: " + pedidoParaAceitar.getStatus());
        System.out.println("Entregador alocado: " + pedidoParaAceitar.getEntregadorAlocado());
        System.out.println();

        // Verificar que o pedido não aparece mais como disponível
        pedidosDisponiveis = servicoPedido.buscarPedidosDisponiveisParaEntregador(emailEntregador);
        System.out.println("Pedidos disponíveis após aceitação: " + pedidosDisponiveis.size());
        System.out.println();

        // 6. TESTE 2: RECUSAR PEDIDO
        System.out.println("========================================");
        System.out.println("TESTE 2: RECUSAR PEDIDO");
        System.out.println("========================================");

        // Criar outro pedido para testar recusa
        Pedido pedidoParaRecusar = new Pedido(
                "passageiro@email.com",
                "cantinho@gmail.com",
                new ArrayList<>(),
                75.0,
                "CARTAO");

        servicoEntrega.processarNovoPedido(pedidoParaRecusar);
        repoPedido.salvar(pedidoParaRecusar);
        String entregadorOriginal = pedidoParaRecusar.getEntregadorAlocado();

        System.out.println("Pedido alocado para: " + entregadorOriginal);
        System.out.println("Status antes: " + pedidoParaRecusar.getStatus());

        boolean recusado = servicoEntrega.recusarPedido(entregadorOriginal, pedidoParaRecusar);

        System.out.println("Resultado: " + (recusado ? "✅ RECUSADO" : "❌ FALHOU"));
        System.out.println("Status depois: " + pedidoParaRecusar.getStatus());
        System.out.println("Entregador após recusa: " +
                (pedidoParaRecusar.getEntregadorAlocado() != null ? pedidoParaRecusar.getEntregadorAlocado()
                        : "NENHUM"));

        if (pedidoParaRecusar.getEntregadorAlocado() != null &&
                !pedidoParaRecusar.getEntregadorAlocado().equals(entregadorOriginal)) {
            System.out.println("✅ Novo entregador foi alocado automaticamente!");
        }
        System.out.println();

        // 7. TESTE 3: VALIDAÇÕES
        System.out.println("========================================");
        System.out.println("TESTE 3: VALIDAÇÕES DE SEGURANÇA");
        System.out.println("========================================");

        // Criar pedido de teste
        Pedido pedidoValidacao = new Pedido(
                "kay@gmail.com",
                "cantinho@gmail.com",
                new ArrayList<>(),
                100.0,
                "DINHEIRO");
        pedidoValidacao.setEntregadorAlocado("entregador1@teste.com");
        pedidoValidacao.setStatus("CRIADO");

        // Teste 3.1: Tentar aceitar pedido não alocado
        System.out.println("\n3.1 - Tentando aceitar pedido não alocado:");
        boolean resultado31 = servicoEntrega.aceitarPedido("outro@entregador.com", pedidoValidacao);
        System.out.println("Resultado esperado: FALSO | Resultado: " + resultado31 +
                (resultado31 == false ? " ✅" : " ❌"));

        // Teste 3.2: Tentar aceitar pedido já processado
        System.out.println("\n3.2 - Tentando aceitar pedido já processado:");
        pedidoValidacao.setStatus("ACEITO");
        boolean resultado32 = servicoEntrega.aceitarPedido("entregador1@teste.com", pedidoValidacao);
        System.out.println("Resultado esperado: FALSO | Resultado: " + resultado32 +
                (resultado32 == false ? " ✅" : " ❌"));

        // Teste 3.3: Tentar recusar pedido não alocado
        System.out.println("\n3.3 - Tentando recusar pedido não alocado:");
        pedidoValidacao.setStatus("CRIADO");
        boolean resultado33 = servicoEntrega.recusarPedido("outro@entregador.com", pedidoValidacao);
        System.out.println("Resultado esperado: FALSO | Resultado: " + resultado33 +
                (resultado33 == false ? " ✅" : " ❌"));

        System.out.println("\n========================================");
        System.out.println("TESTES CONCLUÍDOS!");
        System.out.println("========================================");
    }

    private static void criarEntregadorTeste(RepositorioUsuario repo, ServicoLocalizacao servicoLoc) {
        Entregador entregador = new Entregador("teste.entregador@rf24.com", hashSenha("123456"));
        entregador.setCnhNumero("12345678901");
        entregador.setCpfNumero("12345678901");
        entregador.setCnhValida(true);
        entregador.setDocIdentidadeValido(true);
        entregador.setContaAtiva(true);
        entregador.setDisponivel(true);

        repo.salvar(entregador);

        // Definir localização do entregador
        servicoLoc.atualizarLocalizacao(entregador.getEmail(), new Localizacao(51.17, 171.17));

        System.out.println("✅ Entregador de teste criado: " + entregador.getEmail());
    }

    private static String hashSenha(String senha) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(senha.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return senha;
        }
    }
}
