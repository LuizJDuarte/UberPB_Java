# RF22 - Notificação do Restaurante e Entregador Mais Próximo

## 📋 Descrição

Implementação completa do requisito funcional RF22, que adiciona:
1. **Notificação para o restaurante** sobre novos pedidos
2. **Busca do entregador mais próximo** do restaurante
3. **Notificação ao entregador disponível** sobre pedidos disponíveis

## 🏗️ Componentes Implementados

### 1. Modelo de Dados

#### `Notificacao.java`
- Modelo para representar notificações no sistema
- Campos:
  - `id`: Identificador único
  - `destinatarioEmail`: Email do destinatário
  - `tipo`: Tipo da notificação (enum `TipoNotificacao`)
  - `mensagem`: Conteúdo da notificação
  - `dataHora`: Data e hora de criação
  - `lida`: Status de leitura

#### `TipoNotificacao.java` (Enum)
- `NOVO_PEDIDO_RESTAURANTE`: Notificação para restaurante sobre novo pedido
- `PEDIDO_DISPONIVEL_ENTREGADOR`: Notificação para entregador sobre pedido disponível
- `PEDIDO_ACEITO`: Pedido foi aceito
- `PEDIDO_EM_ROTA`: Entregador está a caminho
- `PEDIDO_ENTREGUE`: Pedido foi entregue
- `PEDIDO_CANCELADO`: Pedido foi cancelado
- Outros tipos para expansão futura

#### Melhorias no `Entregador.java`
- Adicionado campo `disponivel` para indicar se está online/offline
- Atualizado toString para mostrar status online/offline
- Atualizada persistência para salvar novo campo

#### Melhorias no `Pedido.java`
- Adicionado campo `entregadorAlocado` para armazenar o entregador responsável
- Atualizada persistência e deserialização

### 2. Repositórios

#### `RepositorioNotificacao.java` (Interface)
- `salvar(Notificacao)`: Salva uma notificação
- `buscarPorDestinatario(String email)`: Busca todas as notificações de um usuário
- `buscarNaoLidasPorDestinatario(String email)`: Busca apenas não lidas
- `buscarPorId(String id)`: Busca por ID
- `atualizar(Notificacao)`: Atualiza uma notificação

#### `ImplRepositorioNotificacaoArquivo.java`
- Implementação em arquivo do repositório de notificações
- Segue o padrão singleton dos outros repositórios
- Persiste em `data/notificacoes.txt`

### 3. Serviços

#### `ServicoNotificacao.java`
Gerencia o envio e consulta de notificações:
- `enviarNotificacao(String, TipoNotificacao, String)`: Envia uma notificação genérica
- `notificarRestauranteNovoPedido(String, String, double)`: Notifica restaurante sobre novo pedido
- `notificarEntregadorPedidoDisponivel(String, String, double)`: Notifica entregador sobre pedido disponível
- `buscarNotificacoesNaoLidas(String)`: Busca notificações não lidas
- `marcarComoLida(String)`: Marca notificação como lida
- `contarNotificacoesNaoLidas(String)`: Conta quantas não lidas o usuário tem

#### `ServicoEntrega.java`
Gerencia a busca de entregadores e alocação para pedidos:
- `buscarEntregadorMaisProximo(String emailRestaurante)`: Encontra o entregador disponível mais próximo do restaurante
  - Utiliza `ServicoLocalizacao` para calcular distâncias
  - Filtra apenas entregadores ativos e disponíveis (online)
  - Retorna o email do entregador mais próximo ou null

- `processarNovoPedido(Pedido)`: Processa um novo pedido completo
  1. Notifica o restaurante sobre o novo pedido
  2. Busca o entregador mais próximo
  3. Aloca o entregador ao pedido
  4. Notifica o entregador sobre o pedido disponível
  5. Retorna true se entregador foi encontrado e alocado

- `listarEntregadoresDisponiveis()`: Lista todos os entregadores online e ativos

### 4. Comandos de Interface

#### `VisualizarNotificacoesComando.java`
- Comando: `visualizar-notificacoes`
- Disponível para: Qualquer usuário logado
- Funcionalidades:
  - Mostra total de notificações e quantas não lidas
  - Lista notificações não lidas primeiro
  - Lista notificações já lidas
  - Permite marcar todas como lidas de uma vez

#### `EntregadorOnlineOfflineComando.java`
- Comando: `entregador-online-offline`
- Disponível para: Entregadores logados
- Funcionalidades:
  - Mostra status atual (ONLINE/OFFLINE)
  - Permite alternar entre online e offline
  - Quando online, o entregador pode receber pedidos
  - Quando offline, não recebe novos pedidos

## 🧪 Testes Implementados

### `ServicoNotificacaoTest.java`
- ✅ Deve enviar notificação para restaurante sobre novo pedido
- ✅ Deve enviar notificação para entregador sobre pedido disponível
- ✅ Deve buscar notificações não lidas de um usuário
- ✅ Deve marcar notificação como lida
- ✅ Deve contar notificações não lidas corretamente

### `ServicoEntregaTest.java`
- ✅ Deve buscar entregador mais próximo do restaurante
- ✅ Deve retornar null quando não há entregadores disponíveis
- ✅ Deve processar novo pedido com notificações para restaurante e entregador
- ✅ Deve retornar false quando não há entregador disponível ao processar pedido
- ✅ Deve listar apenas entregadores disponíveis e ativos

### `NotificacaoTest.java`
- ✅ Deve criar notificação corretamente
- ✅ Deve marcar notificação como lida
- ✅ Deve converter para string de persistência corretamente
- ✅ Deve restaurar notificação a partir de string
- ✅ Deve retornar null para string inválida
- ✅ toString deve incluir informações principais

## 🚀 Como Usar

### Fluxo Completo de Pedido com Notificações

1. **Cliente cria um pedido** (usando carrinho e restaurante)
   - Sistema chama `servicoEntrega.processarNovoPedido(pedido)`

2. **Restaurante recebe notificação**
   - Notificação tipo: `NOVO_PEDIDO_RESTAURANTE`
   - Exemplo: "Novo pedido de cliente@teste.com! Total: R$ 50,00"

3. **Sistema busca entregador mais próximo**
   - Considera apenas entregadores com:
     - `contaAtiva = true`
     - `disponivel = true` (online)
   - Calcula distância entre entregador e restaurante
   - Seleciona o mais próximo

4. **Entregador recebe notificação**
   - Notificação tipo: `PEDIDO_DISPONIVEL_ENTREGADOR`
   - Exemplo: "Pedido disponível do restaurante Pizzaria Teste! Valor da entrega: R$ 7,50"
   - Pedido é alocado: `pedido.setEntregadorAlocado(emailEntregador)`

5. **Usuários visualizam notificações**
   - Comando: `visualizar-notificacoes`
   - Veem suas notificações não lidas e lidas
   - Podem marcar todas como lidas

### Comandos Disponíveis

```
visualizar-notificacoes
  - Visualiza todas as notificações do usuário
  - Mostra não lidas em destaque
  - Permite marcar todas como lidas

entregador-online-offline
  - [Apenas Entregadores] Alterna status online/offline
  - Quando online, recebe notificações de pedidos
  - Quando offline, não recebe novos pedidos
```

### Exemplo de Integração em Código

```java
// Em um comando de criar pedido:
public void executar(Scanner scanner, ContextoAplicacao contexto) {
    // ... criação do pedido ...
    
    Pedido pedido = new Pedido(emailCliente, emailRestaurante, itens, total, "CARTAO");
    
    // Salvar pedido
    contexto.servicoPedido.salvarPedido(pedido);
    
    // Processar notificações e buscar entregador (RF22)
    boolean entregadorEncontrado = contexto.servicoEntrega.processarNovoPedido(pedido);
    
    if (entregadorEncontrado) {
        System.out.println("✅ Pedido criado e entregador alocado!");
        System.out.println("Entregador: " + pedido.getEntregadorAlocado());
    } else {
        System.out.println("⚠️ Pedido criado, mas nenhum entregador disponível no momento.");
    }
}
```

## 📊 Estrutura de Arquivos

```
src/main/java/com/uberpb/
├── model/
│   ├── Notificacao.java (NOVO)
│   ├── TipoNotificacao.java (NOVO)
│   ├── Entregador.java (ATUALIZADO - campo disponivel)
│   └── Pedido.java (ATUALIZADO - campo entregadorAlocado)
├── repository/
│   ├── RepositorioNotificacao.java (NOVO)
│   ├── ImplRepositorioNotificacaoArquivo.java (NOVO)
│   └── ImplRepositorioUsuarioArquivo.java (ATUALIZADO)
├── service/
│   ├── ServicoNotificacao.java (NOVO)
│   └── ServicoEntrega.java (NOVO)
└── app/
    ├── VisualizarNotificacoesComando.java (NOVO)
    ├── EntregadorOnlineOfflineComando.java (NOVO)
    ├── ContextoAplicacao.java (ATUALIZADO)
    └── ProvedorDependencias.java (ATUALIZADO)

src/test/java/com/uberpb/
├── service/
│   ├── ServicoNotificacaoTest.java (NOVO)
│   └── ServicoEntregaTest.java (NOVO)
└── model/
    └── NotificacaoTest.java (NOVO)

data/
└── notificacoes.txt (criado automaticamente)
```

## ✨ Características Técnicas

- **Busca por Proximidade**: Utiliza `ServicoLocalizacao` para geocodificação e cálculo de distância
- **Notificações em Tempo Real**: Sistema de notificações simula envio imediato ao persistir
- **Persistência**: Notificações são salvas em arquivo para consulta posterior
- **Testável**: 100% dos componentes novos possuem testes unitários
- **Extensível**: Enum `TipoNotificacao` permite fácil adição de novos tipos

## 🔄 Fluxo de Dados

```
[Cliente cria pedido]
        ↓
[ServicoPedido.salvar()]
        ↓
[ServicoEntrega.processarNovoPedido()]
        ↓
    ┌───┴───┐
    ↓       ↓
[Notifica   [Busca
Restaurante] Entregador]
    ↓           ↓
[ServicoNotificacao] [servicoLocalizacao
   .notificar()]      .distanciaKm()]
                      ↓
                [Aloca Entregador]
                      ↓
                [Notifica Entregador]
```

## 📝 Notas de Implementação

1. **Compatibilidade**: Todas as mudanças em modelos existentes (`Entregador`, `Pedido`) são retrocompatíveis com dados antigos
2. **Singleton**: Repositórios seguem padrão singleton como os demais do projeto
3. **Injeção de Dependências**: Serviços são injetados via `ProvedorDependencias` e `ContextoAplicacao`
4. **Testes Isolados**: Todos os testes usam mocks para isolamento completo

## ✅ Requisitos Atendidos

- ✅ **Implementar notificação para o restaurante** - `ServicoNotificacao.notificarRestauranteNovoPedido()`
- ✅ **Implementar busca do entregador mais próximo** - `ServicoEntrega.buscarEntregadorMaisProximo()`
- ✅ **Enviar notificação ao entregador disponível** - `ServicoNotificacao.notificarEntregadorPedidoDisponivel()`
- ✅ **Testes simples, bem organizados, para cada um** - 3 arquivos de teste com 16 casos de teste

---

**Desenvolvido seguindo os padrões do projeto UberPB_Java**
