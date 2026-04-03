# CONTROL FINANCE Backend

Backend do sistema CONTROL FINANCE, desenvolvido com Spring Boot 3, Java 21 e MongoDB.

O projeto segue uma organização por módulos de negócio, com separação entre camadas de aplicação, domínio, infraestrutura e interfaces. A proposta é servir como uma base evolutiva para um sistema financeiro com autenticação, gestão de usuários, categorias, transações, relatórios e recursos transversais como auditoria e notificações.

## Visão geral

O que já existe no repositório:

- autenticação com JWT usando access token e refresh token
- suporte a 2FA com TOTP
- CRUD de categorias
- CRUD e busca de transações
- consulta de perfil do usuário e manutenção da conta
- endpoint de dashboard financeiro
- auditoria baseada em eventos de domínio
- notificações geradas por eventos de orçamento
- documentação OpenAPI com Swagger UI
- testes de integração com MongoDB via Testcontainers

## Stack técnica

- Java 21
- Spring Boot 3.3.2
- Spring Web
- Spring Security 6
- Spring Data MongoDB
- Bean Validation
- Lombok
- MapStruct
- JJWT
- Springdoc OpenAPI
- Docker e docker compose
- Testcontainers

## Arquitetura

O projeto está organizado como um monólito modular, com inspiração em Clean Architecture e DDD.

Estrutura principal:

- `src/main/java/com/controlfinance/modules`: módulos de negócio
- `src/main/java/com/controlfinance/interfaces/rest`: controllers HTTP
- `src/main/java/com/controlfinance/infrastructure`: segurança, eventos, MongoDB e OpenAPI
- `src/main/java/com/controlfinance/shared`: base comum, exceções e utilitários

Fluxo típico de uma requisição:

1. o controller HTTP recebe a requisição
2. a camada de application executa o caso de uso
3. a camada de domain contém entidades, regras e contratos
4. a camada de infrastructure adapta persistência, segurança e integração com o framework

## Estrutura do projeto

```text
src/
  main/
    java/com/controlfinance/
      ControlFinanceApplication.java
      infrastructure/
        events/
        mongo/
        openapi/
        security/
      interfaces/
        rest/
      modules/
        assets/
        audit/
        auth/
        budget/
        cashflow/
        categories/
        debts/
        investments/
        notifications/
        projects/
        reporting/
        transactions/
        user/
      shared/
        base/
        exceptions/
        utils/
  test/
    java/com/controlfinance/
```

## Módulos

### Módulos com implementação ativa

- `auth`: registro, login e refresh de token
- `user`: perfil do usuário, troca de senha, remoção de conta e 2FA
- `categories`: cadastro e listagem de categorias
- `transactions`: criação, atualização, remoção e busca de lançamentos
- `reporting`: resumo financeiro do dashboard
- `audit`: persistência de logs de auditoria a partir de eventos
- `notifications`: criação de notificações a partir de eventos de orçamento
- `budget`: entidade, repositório e eventos ligados ao orçamento

### Módulos já previstos na estrutura

Os diretórios abaixo já existem como parte da organização do domínio, mas não expõem funcionalidades REST completas neste estado do projeto:

- `assets`
- `cashflow`
- `debts`
- `investments`
- `projects`

## Requisitos

Para desenvolvimento local:

- Java 21
- Docker e Docker Compose
- opcionalmente Maven local, embora o projeto já inclua `./mvnw`

## Configuração

As propriedades principais estão em `src/main/resources/application.yml`.

### Variáveis de ambiente

| Variável | Descrição | Valor padrão no projeto |
| --- | --- | --- |
| `MONGODB_URI` | URI de conexão com o MongoDB | `mongodb://localhost:27017/control_finance` |
| `APP_JWT_SECRET` | segredo do access token JWT | `change-me-change-me-change-me-change-me` |
| `APP_JWT_REFRESH_SECRET` | segredo do refresh token JWT | `change-me-refresh-change-me-refresh-change-me` |
| `APP_JWT_ACCESS_TTL_MINUTES` | tempo de vida do access token em minutos | `15` |
| `APP_JWT_REFRESH_TTL_DAYS` | tempo de vida do refresh token em dias | `30` |
| `APP_ENCRYPTION_KEY` | chave Base64 usada para criptografar segredos de 2FA | `AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=` |

### Observações importantes

- os valores padrão de JWT e criptografia servem apenas para desenvolvimento local
- em ambientes reais, substitua todos os segredos por valores seguros
- a aplicação sobe na porta `8080`

## Como executar localmente

### 1. Subir o MongoDB

```bash
docker compose up -d
```

O `docker-compose.yml` sobe um MongoDB 7 com o banco `control_finance` exposto na porta `27017`.

### 2. Executar a API

```bash
./mvnw spring-boot:run
```

### 3. Acessar a documentação

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health

## Build e empacotamento

### Rodar os testes

```bash
./mvnw test
```

### Gerar o artefato

```bash
./mvnw clean package
```

O `Dockerfile` usa build em duas etapas:

1. compila o projeto com JDK 21
2. executa o jar final com JRE 21

### Gerar imagem Docker

```bash
docker build -t control-finance-backend:local .
```

### Executar a imagem

```bash
docker run --rm -p 8080:8080 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/control_finance \
  -e APP_JWT_SECRET=uma-chave-bem-segura-com-32-caracteres-ou-mais \
  -e APP_JWT_REFRESH_SECRET=outra-chave-bem-segura-com-32-caracteres-ou-mais \
  -e APP_ENCRYPTION_KEY=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA= \
  control-finance-backend:local
```

## Segurança

A configuração de segurança usa `SecurityFilterChain` com autenticação stateless baseada em JWT.

Rotas públicas:

- `/api/v1/auth/**`
- `/swagger-ui.html`
- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/actuator/health`

Todas as demais rotas exigem autenticação.

## Autenticação e 2FA

Fluxo básico:

1. registrar usuário em `POST /api/v1/auth/register`
2. fazer login em `POST /api/v1/auth/login`
3. usar o `accessToken` no header `Authorization: Bearer <token>`
4. renovar tokens em `POST /api/v1/auth/refresh` quando necessário

Fluxo de 2FA:

1. gerar segredo em `POST /api/v1/users/me/2fa/secret`
2. confirmar o código em `POST /api/v1/users/me/2fa/confirm`
3. enviar `twoFactorCode` no login quando a conta estiver com 2FA habilitado

## Convenções de persistência

As entidades persistidas em MongoDB herdam de `BaseDocument`, que inclui:

- `id`
- `userId`
- `createdAt`
- `updatedAt`

O campo `userId` é usado como base para ownership dos dados por usuário.

## Eventos de domínio, auditoria e notificações

O projeto usa eventos de domínio publicados dentro da aplicação.

- eventos genéricos implementam o contrato `DomainEvent`
- o módulo `audit` escuta eventos e grava registros na coleção `audit_logs`
- o módulo `notifications` escuta `BudgetLimitReachedEvent` e grava notificações na coleção `notifications`

Esse modelo permite adicionar comportamentos transversais sem acoplamento forte entre módulos.

## Tratamento de erros

As exceções são centralizadas por um `@RestControllerAdvice`.

Comportamentos principais:

- exceções de negócio retornam status e código específicos
- erros de validação retornam `422 Unprocessable Entity`
- erros inesperados retornam `500 Internal Server Error`

Formato base da resposta de erro:

```json
{
  "timestamp": "2026-04-01T12:00:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "path": "/api/v1/transactions",
  "fields": {
    "amount": "must be greater than 0"
  }
}
```

## Endpoints

Base da API: `/api/v1`

### Health

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| `GET` | `/actuator/health` | verificação de saúde da aplicação | não |

### Auth

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/register` | registra um novo usuário | não |
| `POST` | `/api/v1/auth/login` | autentica usuário | não |
| `POST` | `/api/v1/auth/refresh` | gera novos tokens | não |

### User

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| `GET` | `/api/v1/users/me` | retorna o perfil autenticado | sim |
| `PATCH` | `/api/v1/users/me` | atualiza nome do usuário | sim |
| `POST` | `/api/v1/users/me/change-password` | altera senha | sim |
| `DELETE` | `/api/v1/users/me` | remove a conta | sim |
| `POST` | `/api/v1/users/me/2fa/secret` | gera segredo TOTP | sim |
| `POST` | `/api/v1/users/me/2fa/confirm` | confirma 2FA com código | sim |

### Categories

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| `POST` | `/api/v1/categories` | cria categoria | sim |
| `GET` | `/api/v1/categories` | lista categorias do usuário | sim |
| `PUT` | `/api/v1/categories/{id}` | atualiza categoria | sim |
| `DELETE` | `/api/v1/categories/{id}` | remove categoria | sim |

### Transactions

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| `POST` | `/api/v1/transactions` | cria transação | sim |
| `GET` | `/api/v1/transactions` | busca transações com filtros | sim |
| `PUT` | `/api/v1/transactions/{id}` | atualiza transação | sim |
| `DELETE` | `/api/v1/transactions/{id}` | remove transação | sim |

Filtros suportados em `GET /api/v1/transactions`:

- `from`
- `to`
- `categoryId`
- `type`
- `status`

### Reporting

| Método | Rota | Descrição | Auth |
| --- | --- | --- | --- |
| `GET` | `/api/v1/reporting/dashboard` | resumo financeiro do dashboard | sim |

## Exemplos de payload

### Registro de usuário

```json
{
  "name": "Maria Souza",
  "email": "maria@controlfinance.com",
  "cpf": "12345678900",
  "password": "SenhaForte123"
}
```

### Login

```json
{
  "email": "maria@controlfinance.com",
  "password": "SenhaForte123",
  "twoFactorCode": "123456"
}
```

### Resposta de autenticação

```json
{
  "accessToken": "<jwt>",
  "refreshToken": "<jwt>"
}
```

### Criar categoria

```json
{
  "name": "Alimentação",
  "type": "expense"
}
```

### Criar transação

```json
{
  "type": "EXPENSE",
  "categoryId": "cat_123",
  "subCategoryId": "subcat_456",
  "amount": 89.90,
  "description": "Supermercado",
  "status": "PAID",
  "transactionDate": "2026-04-01T10:30:00Z",
  "paymentTicket": "NF-001"
}
```

### Dashboard

```json
{
  "totalIncome": 5000.00,
  "totalExpense": 3200.50,
  "balance": 1799.50
}
```

## Testes

O projeto possui teste de integração com MongoDB real usando Testcontainers.

Cobertura observada no repositório:

- `TransactionRepositoryIT`: valida persistência e leitura de transações com container MongoDB 7

Isso indica uma base pronta para evolução de testes de integração por módulo.

## Documentação interativa

O projeto expõe OpenAPI com esquema Bearer JWT configurado. Ao abrir o Swagger UI, é possível autenticar usando o botão `Authorize` e testar os endpoints protegidos diretamente pela interface.

## Estado atual do projeto

Este repositório já oferece uma fundação funcional para um backend financeiro modular, mas ainda há espaço para expansão natural em módulos já previstos na estrutura, como ativos, dívidas, investimentos, fluxo de caixa e projetos.

Em resumo, o que está mais maduro hoje é:

- autenticação e segurança
- usuários e 2FA
- categorias
- transações
- reporting básico
- auditoria e notificações baseadas em eventos

## Comandos úteis

```bash
docker compose up -d
./mvnw spring-boot:run
./mvnw test
./mvnw clean package
```
