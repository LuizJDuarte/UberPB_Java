# UberPB: Sistema de Mobilidade Simplificado

## Visão Geral do Projeto

O UberPB é um sistema de mobilidade simplificado, inspirado no funcionamento do aplicativo Uber, desenvolvido como parte do projeto de Engenharia de Software 2 (ES2) da UEPB. O objetivo principal é criar uma plataforma funcional que gerencia a solicitação, aceitação e acompanhamento de corridas, bem como o gerenciamento de usuários e pagamentos, tudo isso focado na qualidade do código e na aplicação de conceitos de engenharia de software.

## Funcionalidades Principais

O sistema prevê três tipos de usuários, cada um com uma visão e conjunto de funcionalidades específicas:

1. Passageiro/Cliente: Solicita corridas, acompanha motoristas e avalia o serviço.
2. Motorista: Aceita corridas, transporta passageiros e gerencia sua disponibilidade.
3. Administrador: Gerencia usuários, pagamentos e oferece suporte.

### Requisitos Funcionais (RFs) Implementados até o momento (Release 1 - Parcial)

**RF01: Cadastro de Usuários:**

- Permite o cadastro de novos Passageiros e Motoristas.
- Tratamento de erro para e-mail já cadastrado.
- Validação de formato de e-mail.

**RF02: Validação de Motorista e Veículo:**

- Permite que motoristas cadastrem seus veículos e informem dados de documentos (CNH, CRLV).
- Determina automaticamente as categorias de serviço que o motorista pode atuar (UberX, Comfort, Black, Bag, XL) com base nas características do veículo e documentos.
- Ativa a conta do motorista após a validação bem-sucedida.

**RF03: Autenticação (Login):**

- Permite o login de usuários (Passageiros e Motoristas) utilizando e-mail e senha.
- Senha armazenada com hash para segurança.

### Requisitos Funcionais Futuros (RFs a serem implementados em Releases futuras)

- **RF04-RF07:** Solicitação de Corrida: Inserção de origem/destino, cálculo de estimativa de tempo/preço, notificação de motoristas.

- **RF08-RF09:** Aceite da Corrida: Motorista pode aceitar ou recusar, atribuição ao motorista mais próximo.

- **RF10-RF12:** Acompanhamento da Corrida: Passageiro acompanha localização, atualização de estimativa, motorista visualiza rota.

- **RF13-RF15:** Pagamentos: Pagamento via cartão, PIX, PayPal, dinheiro; cálculo automático do valor; emissão de recibo.

- **RF16-RF17:** Avaliações: Passageiros e motoristas se avaliam mutuamente; uso da média das avaliações para priorização.

- **RF18-RF19:** Histórico: Manutenção de histórico de corridas filtrável; Requisito surpresa.

## Especificações Técnicas e Restrições

Este projeto segue rigorosamente as seguintes diretrizes:

- **Linguagem:** Exclusivamente Java.
- **Interface:** Somente Interface de Linha de Comando (CLI). Não há interface gráfica para focar na lógica de negócio e na qualidade do código.
- **Testes:** Uso obrigatório de JUnit para testes unitários, com uma cobertura mínima de 80%.
- **Persistência:** Armazenamento de dados local em arquivo (sem uso de bancos de dados tradicionais).
- **Bibliotecas/APIs:** Não é permitido o uso de APIs externas (ex: Swagger, frameworks web). O foco é na implementação pura de Java e na aplicação de Padrões de Projeto.
- **Segurança:** Senhas de usuário armazenadas usando hashing (SHA-256).

### Estrutura do Projeto

A organização do código segue uma estrutura modular para separar responsabilidades:

uberpb/   
├── src/   
│   ├── main/   
│   │   ├── java/   
│   │   │   ├── com/   
│   │   │   │   ├── uberpb/   
│   │   │   │   │   ├── app/             # Classe principal da aplicação CLI   
│   │   │   │   │   ├── exceptions/      # Exceções customizadas do projeto   
│   │   │   │   │   ├── model/           # Classes de entidade (Passageiro, Motorista, Veiculo, CategoriaVeiculo)   
│   │   │   │   │   ├── repository/      # Interfaces e implementações para persistência de dados   
│   │   │   │   │   ├── service/         # Camada de lógica de negócio (ServicoCadastro, ServicoAutenticacao, ServicoValidacaoMotorista)   
│   │   │   │   │   └── util/            # Utilitários (ex: PasswordUtil para hashing e validação de e-mail)   
│   ├── test/    # Testes unitários (a serem implementados com JUnit)   
└── .gitignore   # Arquivos e pastas a serem ignorados pelo controle de versão   
└── README.md    # Este arquivo   

## Como Compilar e Executar

Para compilar e rodar o projeto, você precisará do Java Development Kit (JDK) instalado em sua máquina.

### Clone o Repositório:

**bash**

`git clone https://github.com/SeuUsuario/uberpb.git # (Substitua pelo URL do seu repositório)`

`cd uberpb/src/main/java`

**Compile os Arquivos Java:**

Navegue até o diretório src/main/java e execute o comando de compilação.

`cd C:\Users\SeuUsuario\Desktop\2025.2\ES2\UberPb_Java\src\main\java # Ou o caminho para o seu projeto`

`javac com/uberpb/app/AplicacaoUberPB.java com/uberpb/exceptions/*.java com/uberpb/model/*.java com/uberpb/util/*.java com/uberpb/repository/*.java com/uberpb/service/*.java`

**Observação:** Certifique-se de que não há erros de compilação. Se houver, verifique as correções de escape de caracteres (\. e \|) nos arquivos PasswordUtil.java e Veiculo.java.
Execute a Aplicação: Após a compilação bem-sucedida, execute a classe principal AplicacaoUberPB a partir do mesmo diretório, especificando o classpath.

`java -cp . com.uberpb.app.AplicacaoUberPB`

A aplicação será iniciada na linha de comando, e você poderá interagir com ela. Os dados serão persistidos em um arquivo chamado usuarios.txt no diretório de execução.

### Processo de Desenvolvimento

O projeto é desenvolvido em equipe de 4 integrantes seguindo uma metodologia ágil (inspirada em Scrum). O acompanhamento é feito através de um repositório GitHub.

Entregas (Releases)
Release 1 (22/09/2025): RF01 a RF12
Release 2 (20/10/2025): RF13 a RF19
Release 3 (17/11/2025): Troca de projetos e novos requisitos.

# Sprints e Reuniões

O projeto é dividido em Sprints de 2 semanas, com reuniões de Sprint Planning e Sprint Review/Planning regulares.

**Sprints:**

- SPRINT1: 26/08 - 08/09
- SPRINT2: 09/09 - 22/09
- SPRINT3: 23/09 - 06/10
- SPRINT4: 07/10 - 20/10
- SPRINT5: 21/10 - 03/11
- SPRINT6: 04/11 - 17/11

**Reuniões:**

- SPRINT PLANNING – 26/08
- SPRINT REVIEW/PLANNING – 09/09, 23/09, 07/10, 21/10, 04/11
- SPRINT REVIEW – 18/11

**Artefatos de Entrega por Release**

Cada release deve conter:

Repositório: Implementação dos requisitos da release, persistência, interface simplificada, testes e tratamento de erros.

**Relatório de Release (relatorio-releaseX.pdf):**

- Descrição da arquitetura (diagramas de caso de uso, classes, sequência).

- Funcionalidades desenvolvidas e prints do terminal.

- Relatório de testes e cobertura de código.

**Relatório de Gerenciamento do Processo (relatorio-processo-releaseX.pdf):**

- Backlog de trabalho e tarefas por sprint.
- Descrição da organização da equipe e papéis.
- Gráfico de burndown por sprint.

**Avaliação do Projeto**

O projeto será avaliado com base nos seguintes pesos:

- Compilação: 0% (Se não compilar, o projeto não será aceito)
- Repositório + Apresentação das Funcionalidades + Testes: 30%
- Relatório de Release: 20%
- Relatório de Gerenciamento do Processo de Desenvolvimento: 50%

**Equipe:**

- Antônio Neri Pereira Monteiro Vieira de Melo
- Dirceu Araújo Macêdo
- Luiz José Mendonça Duarte
- Rodrigo Lira de Farias

