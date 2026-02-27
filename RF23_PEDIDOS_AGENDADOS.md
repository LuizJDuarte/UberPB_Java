# RF23 - Pedidos Agendados ou Imediatos

## Descrição Geral
Implementação da funcionalidade de permitir que os usuários escolham entre fazer pedidos imediatos ou agendar pedidos para um horário específico.

## Funcionalidades Implementadas

### 1. **Tipos de Pedidos**
- **IMEDIATO**: Pedido processado imediatamente
- **AGENDADO**: Pedido agendado para uma data/hora específica

### 2. **Validação de Agendamento**
A classe `AgendamentoPedido` valida:
- Data e hora não podem estar vazias
- Data e hora devem ser no futuro
- Formato esperado: `dd/MM/yyyy HH:mm` (ex: 25/02/2026 14:30)

### 3. **Fluxo de Finalização de Pedido**

#### Etapa 1: Escolher Tipo de Pedido
```
1) Pedido Imediato
2) Pedido Agendado
```

#### Etapa 2: Agendamento (apenas para pedidos agendados)
- Solicita data e hora em formato `dd/MM/yyyy HH:mm`
- Valida se a data/hora é no futuro
- Exibe mensagem de erro se a data for inválida

#### Etapa 3: Forma de Pagamento
```
1) Cartão de Crédito
2) Cartão de Débito
3) Pix
4) Dinheiro
```

#### Etapa 4: Confirmação
- Exibe resumo completo do pedido incluindo:
  - Total a pagar
  - Forma de pagamento
  - Tipo de pedido
  - Data/hora do agendamento (se aplicável)

## Classes Implementadas

### `TipoPedido.java` (Enum)
Enumeration contendo os tipos de pedido:
- `IMEDIATO`: Pedido imediato
- `AGENDADO`: Pedido agendado

### `AgendamentoPedido.java`
Classe responsável por gerenciar agendamentos de pedidos:

**Métodos principais:**
- `getDataHoraAgendamento()`: Retorna a data/hora do agendamento
- `isValido()`: Valida se o agendamento é válido (data no futuro)
- `getErroValidacao()`: Retorna mensagem de erro se inválido
- `formatarParaPersistencia()`: Formata para salvar em arquivo
- `fromString(String data)`: Reconstrói agendamento a partir de string persistida

### `PedidoStatus.java` (Enum)
Enumeration com os possíveis estados de um pedido:
- `CRIADO`: Pedido recém-criado
- `CONFIRMADO`: Pedido confirmado
- `AGENDADO`: Pedido aguardando o horário agendado
- `ACEITO`: Pedido aceito pelo restaurante
- `EM_PREPARACAO`: Sendo preparado
- `PRONTO`: Pronto para entrega
- `EM_ROTA`: Em rota de entrega
- `ENTREGUE`: Entregue com sucesso
- `CANCELADO`: Cancelado

### Modificações em `Pedido.java`
Adicionados:
- `tipoPedido`: TipoPedido - Tipo do pedido (imediato ou agendado)
- `agendamento`: AgendamentoPedido - Informações de agendamento (null se imediato)

**Novo construtor:**
```java
public Pedido(String emailCliente, String emailRestaurante, 
              List<ItemCarrinho> itens, double total, 
              String formaPagamento, TipoPedido tipoPedido, 
              AgendamentoPedido agendamento)
```

**Métodos adicionados:**
- `getTipoPedido()`: Retorna o tipo de pedido
- `setTipoPedido(TipoPedido)`: Define o tipo de pedido
- `getAgendamento()`: Retorna dados de agendamento
- `setAgendamento(AgendamentoPedido)`: Define dados de agendamento

### Modificações em `VisualizarCarrinhoComando.java`
Adicionados:
- Método `coletarAgendamento()`: Interface para coleta e validação de data/hora
- Novo fluxo em `finalizarPedido()` com seleção de tipo de pedido

## Persistência de Dados

O formato de persistência agora inclui dois novos campos:
```
emailCliente,emailRestaurante,itens,...,total,formaPagamento,status,tipoPedido,dataHoraAgendamento
```

Exemplo:
```
usuario@email.com,restaurante@email.com,pizza:2;bebida:1;,85.50,Pix,CRIADO,AGENDADO,25/02/2026 14:30
```

## Compatibilidade com Dados Antigos

A implementação mantém compatibilidade com dados antigos (sem os novos campos):
- Pedidos antigos são carregados com `tipoPedido = IMEDIATO` por padrão
- Campo de agendamento é deixado como null se não existir

## Exemplo de Uso

```
=== Tipo de Pedido ===
1) Pedido Imediato
2) Pedido Agendado
> 2

=== Agendar Pedido ===
Data e hora do agendamento (formato: dd/MM/yyyy HH:mm)
> 25/02/2026 14:30

✓ Agendamento válido!

Escolha a forma de pagamento:
1) Cartão de Crédito
...
> 3

Resumo do Pedido:
Total a pagar: R$ 85,50
Forma de pagamento: Pix
Tipo de pedido: Agendado
Data/Hora: 25/02/2026 14:30

Confirmar pedido?
1) Confirmar
2) Cancelar
> 1

✓ Pedido confirmado e salvo com sucesso!
Seu pedido está agendado para: 25/02/2026 14:30
```

## Testes Recomendados

1. ✅ Criar pedido imediato com sucesso
2. ✅ Criar pedido agendado com data/hora válida
3. ❌ Tentar criar pedido agendado com data no passado
4. ❌ Tentar criar pedido agendado com formato de data inválido
5. ✅ Verificar persistência correta dos dados
6. ✅ Verificar compatibilidade com dados antigos
