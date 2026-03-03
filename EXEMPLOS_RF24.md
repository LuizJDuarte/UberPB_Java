# Exemplos de Uso - RF24: Aceitar/Recusar Pedidos

## Cenário 1: Entregador Aceita Pedido

### Passo 1: Entregador fica online
```
Comando: [Entregador] Online/Offline
Status atual: OFFLINE
Deseja mudar o status? (s/n): s

✓ Status alterado para: ONLINE ✅
Você está ONLINE e pode receber pedidos de entrega!
```

### Passo 2: Listar pedidos disponíveis
```
Comando: [Entregador] Pedidos Disponíveis

========== PEDIDOS DISPONÍVEIS ==========

[1] Pedido:
    Restaurante: pizzaria@teste.com
    Cliente: joao@teste.com
    Total: R$ 85.00
    Taxa de Entrega (estimada): R$ 12.75
    Status: CRIADO
    Pagamento: CARTAO

[2] Pedido:
    Restaurante: burguer@teste.com
    Cliente: maria@teste.com
    Total: R$ 45.00
    Taxa de Entrega (estimada): R$ 6.75
    Status: CONFIRMADO
    Pagamento: PIX

=========================================
Total de pedidos disponíveis: 2

Use os comandos 'Aceitar Pedido' ou 'Recusar Pedido' para responder.
```

### Passo 3: Aceitar um pedido
```
Comando: [Entregador] Aceitar Pedido

========== ACEITAR PEDIDO ==========

[1] Pedido:
    Restaurante: pizzaria@teste.com
    Cliente: joao@teste.com
    Total: R$ 85.00
    Taxa de Entrega: R$ 12.75
    Status: CRIADO

[2] Pedido:
    Restaurante: burguer@teste.com
    Cliente: maria@teste.com
    Total: R$ 45.00
    Taxa de Entrega: R$ 6.75
    Status: CONFIRMADO

====================================

Digite o número do pedido que deseja aceitar (ou 0 para cancelar): 1

Confirma aceitar o pedido do restaurante pizzaria@teste.com? (s/n): s

✅ Pedido aceito com sucesso!
   Taxa de Entrega: R$ 12.75
   O cliente foi notificado.

📍 Próximos passos:
   1. Aguarde o restaurante preparar o pedido
   2. Retire o pedido no restaurante
   3. Entregue ao cliente
```

---

## Cenário 2: Entregador Recusa Pedido

### Passo 1: Listar pedidos disponíveis
```
Comando: [Entregador] Pedidos Disponíveis

========== PEDIDOS DISPONÍVEIS ==========

[1] Pedido:
    Restaurante: sushi@teste.com
    Cliente: carlos@teste.com
    Total: R$ 120.00
    Taxa de Entrega (estimada): R$ 18.00
    Status: CRIADO
    Pagamento: DINHEIRO

=========================================
Total de pedidos disponíveis: 1
```

### Passo 2: Recusar o pedido
```
Comando: [Entregador] Recusar Pedido

========== RECUSAR PEDIDO ==========

[1] Pedido:
    Restaurante: sushi@teste.com
    Cliente: carlos@teste.com
    Total: R$ 120.00
    Taxa de Entrega: R$ 18.00
    Status: CRIADO

====================================

Digite o número do pedido que deseja recusar (ou 0 para cancelar): 1

Confirma recusar o pedido do restaurante sushi@teste.com? (s/n): s

✅ Pedido recusado.
   O sistema tentará alocar outro entregador para este pedido.
```

### O que acontece nos bastidores:
1. Status do pedido muda para RECUSADO
2. Alocação do entregador é removida
3. Sistema busca outro entregador disponível mais próximo
4. Se encontrar:
   - Novo entregador é alocado
   - Status volta para CRIADO
   - Novo entregador recebe notificação
5. Se não encontrar:
   - Pedido aguarda próximo entregador ficar disponível

---

## Cenário 3: Validações de Segurança

### Tentativa de aceitar pedido não alocado
```
Comando: [Entregador] Aceitar Pedido

❌ Este pedido não foi alocado para você.
```

### Tentativa de aceitar pedido já processado
```
Comando: [Entregador] Aceitar Pedido

❌ Este pedido não está mais disponível para aceitação.
```

### Entregador offline tenta aceitar pedido
```
Comando: [Entregador] Aceitar Pedido

⚠️ Você precisa estar online para aceitar pedidos.
```

### Conta não ativada
```
Comando: [Entregador] Pedidos Disponíveis

⚠️ Sua conta ainda não foi ativada pelo administrador.
```

---

## Cenário 4: Fluxo Completo com Múltiplos Entregadores

### Estado Inicial
```
Sistema: 3 entregadores online
- ent1@teste.com (mais próximo da pizzaria)
- ent2@teste.com 
- ent3@teste.com
```

### Cliente faz pedido
```
Cliente: joao@teste.com
Restaurante: pizzaria@teste.com
Total: R$ 85.00

📧 Notificação enviada para pizzaria@teste.com
✅ Entregador ent1@teste.com alocado para o pedido
📧 Notificação enviada para ent1@teste.com
```

### Entregador 1 recusa
```
[ent1@teste.com] Recusa o pedido

Status: RECUSADO
Buscando novo entregador...
✅ Entregador ent2@teste.com alocado para o pedido
📧 Notificação enviada para ent2@teste.com
```

### Entregador 2 aceita
```
[ent2@teste.com] Aceita o pedido

Status: ACEITO
📧 Notificação enviada para joao@teste.com: 
    "Seu pedido foi aceito pelo entregador!"

Pedido agora vinculado a ent2@teste.com
Pedido NÃO aparece mais para outros entregadores
```

---

## Cenário 5: Código de Teste (JUnit)

### Teste de Aceitação
```java
@Test
@DisplayName("RF24: Deve permitir entregador aceitar pedido alocado para ele")
public void testAceitarPedidoComSucesso() {
    // Arrange
    Pedido pedido = new Pedido(
        "cliente@teste.com",
        "rest@teste.com",
        new ArrayList<>(),
        100.0,
        "CARTAO"
    );
    pedido.setEntregadorAlocado("ent@teste.com");
    pedido.setStatus("CRIADO");

    // Act
    boolean resultado = servicoEntrega.aceitarPedido("ent@teste.com", pedido);

    // Assert
    assertTrue(resultado);
    assertEquals("ACEITO", pedido.getStatus());
    verify(repositorioPedido, times(1)).atualizar(pedido);
    verify(servicoNotificacao, times(1)).notificarCliente(
        eq("cliente@teste.com"),
        anyString()
    );
}
```

### Teste de Recusa com Realocação
```java
@Test
@DisplayName("RF24: Deve permitir entregador recusar pedido e alocar novo entregador")
public void testRecusarPedidoComNovoEntregador() {
    // Arrange
    Restaurante restaurante = new Restaurante("rest@teste.com", "hash");
    restaurante.setLocalizacao(new Localizacao(-7.12, -34.88));

    Entregador ent1 = new Entregador("ent1@teste.com", "hash");
    ent1.setContaAtiva(true);
    ent1.setDisponivel(true);

    Entregador ent2 = new Entregador("ent2@teste.com", "hash");
    ent2.setContaAtiva(true);
    ent2.setDisponivel(true);

    Pedido pedido = new Pedido(
        "cliente@teste.com",
        "rest@teste.com",
        new ArrayList<>(),
        100.0,
        "CARTAO"
    );
    pedido.setEntregadorAlocado("ent1@teste.com");
    pedido.setStatus("CRIADO");

    when(repositorioUsuario.buscarPorEmail("rest@teste.com"))
        .thenReturn(restaurante);
    when(repositorioUsuario.buscarTodos())
        .thenReturn(Arrays.asList(restaurante, ent1, ent2));

    // Act
    boolean resultado = servicoEntrega.recusarPedido("ent1@teste.com", pedido);

    // Assert
    assertTrue(resultado);
    assertEquals("ent2@teste.com", pedido.getEntregadorAlocado());
    assertEquals("CRIADO", pedido.getStatus());
    verify(repositorioPedido, atLeast(2)).atualizar(pedido);
    verify(servicoNotificacao, times(1))
        .notificarEntregadorPedidoDisponivel(
            eq("ent2@teste.com"),
            anyString(),
            anyDouble()
        );
}
```

---

## Dados Persistidos (pedidos.txt)

### Formato do arquivo
```
cliente@email,restaurante@email,itens,total,pagamento,status,tipo,agendamento,entregadorAlocado
```

### Exemplo de pedido CRIADO com entregador alocado
```
joao@teste.com,pizzaria@teste.com,Pizza:2;Refrigerante:1;,85.00,CARTAO,CRIADO,IMEDIATO,,ent1@teste.com
```

### Exemplo de pedido ACEITO
```
maria@teste.com,burguer@teste.com,Hamburger:1;Batata:1;,45.00,PIX,ACEITO,IMEDIATO,,ent2@teste.com
```

### Exemplo de pedido RECUSADO
```
carlos@teste.com,sushi@teste.com,Sushi:3;,120.00,DINHEIRO,RECUSADO,IMEDIATO,,
```

---

## Notas Importantes

1. **Validação de Online**: Entregador DEVE estar online (disponível) para aceitar/recusar
2. **Validação de Conta**: Conta do entregador DEVE estar ativa
3. **Validação de Status**: Apenas pedidos CRIADO ou CONFIRMADO podem ser aceitos/recusados
4. **Validação de Alocação**: Entregador só pode aceitar/recusar pedidos alocados para ele
5. **Persistência**: Todas as mudanças são salvas automaticamente no arquivo pedidos.txt
6. **Notificações**: Sistema notifica automaticamente clientes e entregadores
7. **Realocação Inteligente**: Ao recusar, sistema busca entregador mais próximo do restaurante
8. **Unicidade**: Pedido aceito não aparece para mais ninguém
