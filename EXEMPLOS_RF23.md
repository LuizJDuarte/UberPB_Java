# Exemplos de Uso - RF23: Pedidos Agendados ou Imediatos

## Cenário 1: Pedido Imediato

```
=== Tipo de Pedido ===
1) Pedido Imediato
2) Pedido Agendado
> 1

Escolha a forma de pagamento:
1) Cartão de Crédito
2) Cartão de Débito
3) Pix
4) Dinheiro
> 3

Resumo do Pedido:
Total a pagar: R$ 85,50
Forma de pagamento: Pix
Tipo de pedido: Imediato

Confirmar pedido?
1) Confirmar
2) Cancelar
> 1

✓ Pedido confirmado e salvo com sucesso!
```

**Resultado**: Pedido criado imediatamente com status CRIADO

---

## Cenário 2: Pedido Agendado com Sucesso

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
2) Cartão de Débito
3) Pix
4) Dinheiro
> 1

Resumo do Pedido:
Total a pagar: R$ 120,00
Forma de pagamento: Cartão de Crédito
Tipo de pedido: Agendado
Data/Hora: 25/02/2026 14:30

Confirmar pedido?
1) Confirmar
2) Cancelar
> 1

✓ Pedido confirmado e salvo com sucesso!
Seu pedido está agendado para: 25/02/2026 14:30
```

**Resultado**: Pedido criado com tipo AGENDADO e data de agendamento registrada

---

## Cenário 3: Data No Passado

```
=== Tipo de Pedido ===
1) Pedido Imediato
2) Pedido Agendado
> 2

=== Agendar Pedido ===
Data e hora do agendamento (formato: dd/MM/yyyy HH:mm)
> 23/02/2026 10:00

✗ A data e hora devem ser no futuro.
✗ Agendamento cancelado.
```

**Resultado**: Agendamento rejeitado, usuário volta ao menu

---

## Cenário 4: Formato de Data Inválido

```
=== Tipo de Pedido ===
1) Pedido Imediato
2) Pedido Agendado
> 2

=== Agendar Pedido ===
Data e hora do agendamento (formato: dd/MM/yyyy HH:mm)
> 25022026 1430

✗ Formato de data/hora inválido. Use dd/MM/yyyy HH:mm
✗ Agendamento cancelado.
```

**Resultado**: Formato rejeitado, usuário volta ao menu

---

## Cenário 5: Agendamento Entre Dias

```
=== Tipo de Pedido ===
1) Pedido Imediato
2) Pedido Agendado
> 2

=== Agendar Pedido ===
Data e hora do agendamento (formato: dd/MM/yyyy HH:mm)
> 26/02/2026 22:00

✓ Agendamento válido!
```

**Resultado**: Agendamento válido mesmo para próximo dia

---

## Estrutura de Dados Persistida

### Pedido Imediato
```
usuario@email.com,restaurante@email.com,pizza:2;coca:1;,85.50,Pix,CRIADO,IMEDIATO,
```

### Pedido Agendado
```
usuario@email.com,restaurante@email.com,pizza:2;coca:1;,85.50,Cartão de Crédito,CRIADO,AGENDADO,25/02/2026 14:30
```

---

## Fluxo Completo da Aplicação

```
Menu Principal
  ↓
Adicionar Itens ao Carrinho
  ↓
Visualizar Carrinho de Compras (VisualizarCarrinhoComando)
  ↓
  ├─→ Remover Item
  ├─→ Limpar Carrinho
  ├─→ Voltar
  │
  └─→ Finalizar Pedido
       ├─→ [RF23] Escolher Tipo de Pedido
       │    ├─→ Imediato → Próxima etapa
       │    └─→ Agendado → [RF23] Coletar e Validar Data/Hora
       │                    └─→ Se válido → Próxima etapa
       │                    └─→ Se inválido → Voltar
       │
       ├─→ Escolher Forma de Pagamento
       ├─→ Confirmar Pedido
       └─→ Salvar e Limpar Carrinho
```

---

## Validações Implementadas

| Validação | Comportamento |
|-----------|---------------|
| Tipo de Pedido Inválido | ❌ Mensagem de erro, volta ao menu |
| Data/Hora Nula | ❌ Rejeição com mensagem explicativa |
| Data no Passado | ❌ Rejeição com mensagem explicativa |
| Formato Inválido | ❌ Rejeição com formato esperado |
| Data/Hora Válida | ✅ Confirmação e prosseguimento |

---

## Estados Possíveis de um Pedido Agendado

```
CRIADO (inicial)
  ↓
AGENDADO (aguardando a data/hora)
  ↓
ACEITO (restaurante aceitou)
  ↓
EM_PREPARACAO
  ↓
PRONTO
  ↓
EM_ROTA
  ↓
ENTREGUE (final)

Ou em qualquer ponto:
  ↓
CANCELADO (final)
```

---

## Mensagens do Sistema

### Sucesso
```
✓ Agendamento válido!
✓ Pedido confirmado e salvo com sucesso!
Seu pedido está agendado para: 25/02/2026 14:30
```

### Erro
```
✗ A data e hora devem ser no futuro.
✗ Formato de data/hora inválido. Use dd/MM/yyyy HH:mm
✗ Agendamento cancelado.
✗ Tipo de pedido inválido.
✗ Forma de pagamento inválida.
```

---

## Casos de Teste

### Teste 1: Pedido Imediato Simples
- ✅ Selecionar "Pedido Imediato"
- ✅ Escolher pagamento
- ✅ Confirmar
- ✅ Verificar tipo = IMEDIATO

### Teste 2: Pedido Agendado Válido
- ✅ Selecionar "Pedido Agendado"
- ✅ Fornecer data/hora futura válida
- ✅ Escolher pagamento
- ✅ Confirmar
- ✅ Verificar tipo = AGENDADO e data persistida

### Teste 3: Agendamento com Data no Passado
- ✅ Selecionar "Pedido Agendado"
- ✅ Fornecer data/hora no passado
- ✅ Verificar rejeição
- ✅ Voltar ao menu

### Teste 4: Agendamento com Formato Inválido
- ✅ Selecionar "Pedido Agendado"
- ✅ Fornecer formato inválido
- ✅ Verificar rejeição com mensagem
- ✅ Voltar ao menu
