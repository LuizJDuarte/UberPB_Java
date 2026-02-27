package com.uberpb.app;

/**
 * EXEMPLO DE INTEGRAÇÃO DO RF22
 * 
 * Este arquivo mostra como integrar as funcionalidades do RF22
 * (Notificação do restaurante e entregador mais próximo)
 * em um comando de criação de pedido.
 */

/*
 * public class ExemploIntegracaoRF22Comando implements Comando {
 * 
 * @Override
 * public String nome() {
 * return "exemplo-criar-pedido-rf22";
 * }
 * 
 * @Override
 * public String descricao() {
 * return "Exemplo de criação de pedido com RF22 integrado";
 * }
 * 
 * @Override
 * public Autorizacao autorizacao() {
 * return Autorizacao.REQUER_LOGIN;
 * }
 * 
 * @Override
 * public void executar(Scanner scanner, ContextoAplicacao contexto) {
 * // 1. Validações iniciais
 * Usuario usuario = contexto.sessao.getUsuarioAtual();
 * 
 * // 2. Coletar informações do pedido
 * System.out.print("Email do restaurante: ");
 * String emailRestaurante = scanner.nextLine().trim();
 * 
 * // 3. Criar o pedido (simplificado para exemplo)
 * List<ItemCarrinho> itens = new ArrayList<>();
 * // ... adicionar itens ...
 * 
 * Pedido pedido = new Pedido(
 * usuario.getEmail(),
 * emailRestaurante,
 * itens,
 * 100.0, // total
 * "CARTAO" // forma de pagamento
 * );
 * 
 * // 4. Salvar o pedido
 * contexto.servicoPedido.salvarPedido(pedido);
 * System.out.println("✓ Pedido criado com sucesso!");
 * 
 * // ============================================================
 * // 🆕 RF22: NOTIFICAÇÕES E BUSCA DE ENTREGADOR
 * // ============================================================
 * 
 * System.out.println("\n📡 Processando notificações e busca de entregador...");
 * 
 * // Chama o serviço de entrega que:
 * // 1. Notifica o restaurante sobre o novo pedido
 * // 2. Busca o entregador mais próximo do restaurante
 * // 3. Aloca o entregador ao pedido
 * // 4. Notifica o entregador sobre o pedido disponível
 * boolean entregadorEncontrado =
 * contexto.servicoEntrega.processarNovoPedido(pedido);
 * 
 * if (entregadorEncontrado) {
 * System.out.println("✅ Entregador alocado com sucesso!");
 * System.out.println("   Entregador: " + pedido.getEntregadorAlocado());
 * System.out.println("   ✉️ Restaurante e entregador foram notificados!");
 * } else {
 * System.out.println("⚠️ Nenhum entregador disponível no momento.");
 * System.out.
 * println("   O pedido foi criado, mas aguarda entregador disponível.");
 * System.out.println("   ✉️ Restaurante foi notificado do novo pedido.");
 * }
 * 
 * // ============================================================
 * 
 * // 5. Mostrar resumo do pedido
 * System.out.println("\n========== RESUMO DO PEDIDO ==========");
 * System.out.println("Cliente: " + pedido.getEmailCliente());
 * System.out.println("Restaurante: " + pedido.getEmailRestaurante());
 * System.out.println("Total: R$ " + String.format("%.2f", pedido.getTotal()));
 * System.out.println("Status: " + pedido.getStatus());
 * System.out.println("Entregador: " +
 * (pedido.getEntregadorAlocado() != null ? pedido.getEntregadorAlocado() :
 * "Aguardando"));
 * System.out.println("======================================");
 * 
 * // Dica: O restaurante e o entregador podem visualizar suas notificações
 * // usando o comando: visualizar-notificacoes
 * }
 * }
 */

// ============================================================
// EXEMPLO DE USO DIRETO DOS SERVIÇOS
// ============================================================

/*
 * // Em qualquer ponto do código onde você tem acesso ao contexto:
 * 
 * // 1. NOTIFICAR RESTAURANTE SOBRE NOVO PEDIDO
 * contexto.servicoNotificacao.notificarRestauranteNovoPedido(
 * "restaurante@exemplo.com", // email do restaurante
 * "cliente@exemplo.com", // email do cliente
 * 150.00 // valor total do pedido
 * );
 * 
 * // 2. BUSCAR ENTREGADOR MAIS PRÓXIMO
 * String emailEntregador = contexto.servicoEntrega.buscarEntregadorMaisProximo(
 * "restaurante@exemplo.com" // email do restaurante
 * );
 * 
 * if (emailEntregador != null) {
 * // 3. ALOCAR ENTREGADOR AO PEDIDO
 * pedido.setEntregadorAlocado(emailEntregador);
 * 
 * // 4. NOTIFICAR ENTREGADOR
 * contexto.servicoNotificacao.notificarEntregadorPedidoDisponivel(
 * emailEntregador, // email do entregador
 * "Pizzaria do João", // nome do restaurante
 * 15.00 // valor da entrega
 * );
 * }
 * 
 * // OU USE O MÉTODO COMPLETO QUE FAZ TUDO:
 * boolean sucesso = contexto.servicoEntrega.processarNovoPedido(pedido);
 */

// ============================================================
// EXEMPLO PARA ENTREGADOR FICAR ONLINE/OFFLINE
// ============================================================

/*
 * // Em um comando ou interface de entregador:
 * 
 * Entregador entregador = (Entregador) contexto.sessao.getUsuarioAtual();
 * 
 * // Ficar ONLINE para receber pedidos
 * entregador.setDisponivel(true);
 * contexto.repositorioUsuario.atualizar(entregador);
 * System.out.println("✅ Você está ONLINE e pode receber pedidos!");
 * 
 * // Ficar OFFLINE
 * entregador.setDisponivel(false);
 * contexto.repositorioUsuario.atualizar(entregador);
 * System.out.println("⏸️ Você está OFFLINE e não receberá novos pedidos.");
 */

// ============================================================
// EXEMPLO DE VISUALIZAÇÃO DE NOTIFICAÇÕES
// ============================================================

/*
 * String emailUsuario = contexto.sessao.getUsuarioAtual().getEmail();
 * 
 * // Buscar notificações não lidas
 * List<Notificacao> naoLidas = contexto.servicoNotificacao
 * .buscarNotificacoesNaoLidas(emailUsuario);
 * 
 * System.out.println("Você tem " + naoLidas.size() +
 * " notificações não lidas:");
 * for (Notificacao n : naoLidas) {
 * System.out.println(" - " + n.getMensagem());
 * }
 * 
 * // Marcar uma notificação como lida
 * if (!naoLidas.isEmpty()) {
 * contexto.servicoNotificacao.marcarComoLida(naoLidas.get(0).getId());
 * }
 */
