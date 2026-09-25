# Teatro ABC

Sistema de gerenciamento de vendas de ingressos para o Teatro ABC, desenvolvido em Kotlin. A aplicação funciona no terminal e permite comprar ingressos, consultar estatísticas de vendas e imprimir os ingressos associados a um CPF.

## Funcionalidades

- Compra de ingressos para as peças:
  - *Wicked*
  - *O Rei Leão*
  - *O Auto da Compadecida*
- Escolha de sessão: manhã, tarde ou noite.
- Seleção de área e assento, com controle de assentos ocupados:
  - Plateia A — 25 assentos — R$ 120,00
  - Plateia B — 100 assentos — R$ 80,00
  - Frisa — 6 lugares — R$ 250,00
  - Camarote — 5 lugares — R$ 400,00
  - Balcão Nobre — 50 assentos — R$ 60,00
- Validação de CPF, incluindo os dígitos verificadores.
- Cadastro opcional de cliente do programa de fidelidade.
- Formas de pagamento: crédito, débito, boleto e PIX.
- Consulta de estatísticas por peça, sessão e área.
- Consulta e impressão dos ingressos por CPF.

## Tecnologias

- Kotlin 2.3.20
- JVM com alvo 1.8 ou superior
- Java Development Kit (JDK)

## Estrutura do projeto

```text
.
├── src/
│   ├── main.kt       # Menu principal, compras, estatísticas e ingressos
│   ├── dados.kt      # Peças, sessões, áreas, preços e capacidades
│   └── validar.kt    # Validações e leitura das entradas
├── teatro.iml        # Configuração do módulo no IntelliJ IDEA
└── README.md
```

## Como executar pela linha de comando

### Pré-requisitos

Instale o JDK e o compilador Kotlin. Verifique se os comandos estão disponíveis:

```bash
java -version
kotlinc -version
```

### Compilar e executar

Na raiz do projeto, execute:

```bash
kotlinc src/*.kt -include-runtime -d teatro.jar
java -jar teatro.jar
```

Para encerrar o programa, escolha a opção `0` no menu principal.

## Como executar no IntelliJ IDEA

1. Abra a pasta do projeto no IntelliJ IDEA.
2. Confirme que um JDK está configurado para o projeto.
3. Aguarde o IntelliJ reconhecer o módulo Kotlin e as dependências.
4. Abra o arquivo `src/main.kt`.
5. Execute a função `main()` usando o botão de execução ao lado da função ou a opção **Run**.

## Observações

- Os dados de clientes, assentos e vendas ficam somente em memória.
- Ao encerrar a aplicação, os dados cadastrados e as vendas são perdidos.
- O CPF deve ser informado com 11 dígitos, sem pontos ou traços.
- Durante o cadastro, a data de nascimento deve seguir o formato `dd/mm/aaaa`.
