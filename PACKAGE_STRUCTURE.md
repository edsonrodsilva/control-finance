# 📂 Estrutura de Pacotes - Referência Rápida

```
src/main/java/com/controlfinance/
│
├── ControlFinanceApplication.java          [Application Entry Point]
│
├── api/                                    [CAMADA: APRESENTAÇÃO]
│   └── rest/
│       ├── AuthController.java             → POST /api/v1/auth/*
│       ├── CategoryController.java         → GET/POST /api/v1/categories
│       ├── HealthController.java           → GET /actuator/health
│       ├── ReportingController.java        → GET /api/v1/reporting/dashboard
│       ├── TransactionController.java      → CRUD /api/v1/transactions
│       └── UserController.java             → GET/PATCH /api/v1/users/me
│
├── auth/                                   [CONTEXTO: AUTENTICAÇÃO]
│   ├── application/
│   │   ├── dto/
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── RefreshRequest.java
│   │   │   └── AuthTokensDto.java
│   │   ├── mapper/
│   │   │   └── [mapeadores]
│   │   └── usecases/
│   │       ├── RegisterUserUseCase.java
│   │       ├── LoginUseCase.java
│   │       └── RefreshTokenUseCase.java
│   ├── domain/
│   │   └── events/
│   │       └── UserRegisteredEvent.java
│   └── infrastructure/
│       └── [aqui iriam adapters MongoDB se necessário]
│
├── user/                                   [CONTEXTO: USUÁRIOS]
│   ├── application/
│   │   ├── dto/
│   │   ├── mapper/
│   │   │   └── UserMapper.java
│   │   └── usecases/
│   │       ├── GetMyProfileUseCase.java
│   │       ├── UpdateProfileUseCase.java
│   │       ├── ChangePasswordUseCase.java
│   │       ├── DeleteAccountUseCase.java
│   │       └── Enable2FAUseCase.java
│   ├── domain/
│   │   ├── entities/
│   │   │   └── User.java (estende BaseDocument)
│   │   ├── repositories/
│   │   │   └── UserRepositoryPort.java
│   │   └── services/
│   │       └── TotpService.java (regras de domínio)
│   └── infrastructure/
│       └── persistence/
│           ├── UserMongoRepository.java (Spring Data)
│           └── UserRepositoryAdapter.java (implementa Port)
│
├── categories/                             [CONTEXTO: CATEGORIAS]
│   ├── application/
│   │   ├── dto/
│   │   │   └── CategoryDto.java
│   │   ├── mapper/
│   │   │   └── CategoryMapper.java
│   │   └── usecases/
│   │       ├── CreateCategoryUseCase.java
│   │       ├── ListCategoriesUseCase.java
│   │       ├── UpdateCategoryUseCase.java
│   │       └── DeleteCategoryUseCase.java
│   ├── domain/
│   │   ├── entities/
│   │   │   ├── Category.java
│   │   │   └── SubCategory.java
│   │   └── repositories/
│   │       └── CategoryRepositoryPort.java
│   └── infrastructure/
│       └── persistence/
│           ├── CategoryMongoRepository.java
│           └── CategoryRepositoryAdapter.java
│
├── transactions/                           [CONTEXTO: TRANSAÇÕES]
│   ├── application/
│   │   ├── dto/
│   │   │   └── TransactionDto.java
│   │   ├── mapper/
│   │   │   └── TransactionMapper.java
│   │   └── usecases/
│   │       ├── CreateTransactionUseCase.java
│   │       ├── UpdateTransactionUseCase.java
│   │       ├── DeleteTransactionUseCase.java
│   │       ├── SearchTransactionsUseCase.java
│   │       └── ComputeMonthSpentService.java
│   ├── domain/
│   │   ├── entities/
│   │   │   └── Transaction.java (estende BaseDocument)
│   │   ├── enums/
│   │   │   ├── TransactionType.java
│   │   │   └── TransactionStatus.java
│   │   ├── events/
│   │   │   └── TransactionCreatedEvent.java
│   │   └── repositories/
│   │       └── TransactionRepositoryPort.java
│   └── infrastructure/
│       └── persistence/
│           ├── TransactionMongoRepository.java
│           └── TransactionRepositoryAdapter.java
│
├── budget/                                 [CONTEXTO: ORÇAMENTOS]
│   ├── application/
│   │   └── services/
│   │       └── BudgetEvaluationService.java
│   ├── domain/
│   │   ├── entities/
│   │   │   └── Budget.java
│   │   ├── events/
│   │   │   └── BudgetLimitReachedEvent.java
│   │   └── repositories/
│   │       └── BudgetRepositoryPort.java
│   └── infrastructure/
│       └── persistence/
│           ├── BudgetMongoRepository.java
│           └── BudgetRepositoryAdapter.java
│
├── audit/                                  [CONTEXTO: AUDITORIA]
│   ├── application/
│   │   └── handlers/
│   │       └── AuditEventHandler.java (escuta eventos)
│   ├── domain/
│   │   └── entities/
│   │       └── AuditLog.java
│   └── infrastructure/
│       └── persistence/
│           ├── AuditLogMongoRepository.java
│           └── [adapter se necessário]
│
├── notifications/                          [CONTEXTO: NOTIFICAÇÕES]
│   ├── application/
│   │   └── handlers/
│   │       └── BudgetNotificationHandler.java
│   ├── domain/
│   │   └── entities/
│   │       └── Notification.java
│   └── infrastructure/
│       └── persistence/
│           ├── NotificationMongoRepository.java
│           └── [adapter se necessário]
│
├── reporting/                              [CONTEXTO: RELATÓRIOS]
│   ├── application/
│   │   ├── dto/
│   │   │   └── DashboardSummaryDto.java
│   │   └── usecases/
│   │       └── GetDashboardSummaryUseCase.java
│   └── domain/
│       └── [entidades se necessário]
│
├── assets/                                 [ESTRUTURA VAZIA - Planejado]
├── cashflow/                               [ESTRUTURA VAZIA - Planejado]
├── debts/                                  [ESTRUTURA VAZIA - Planejado]
├── investments/                            [ESTRUTURA VAZIA - Planejado]
├── projects/                               [ESTRUTURA VAZIA - Planejado]
│
├── infrastructure/                         [CAMADA: INFRAESTRUTURA COMPARTILHADA]
│   ├── config/
│   │   ├── [configurações gerais]
│   │   └── [beans customizados]
│   │
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtService.java
│   │   ├── SecurityConfig.java
│   │   ├── SecurityUtils.java
│   │   └── [outras classes de segurança]
│   │
│   ├── events/
│   │   ├── DomainEventPublisher.java
│   │   └── [listeners globais]
│   │
│   ├── mongo/
│   │   ├── MongoConfig.java
│   │   └── [customizações do driver]
│   │
│   └── openapi/
│       ├── OpenApiConfig.java
│       └── [configurações do Swagger]
│
└── common/                                 [CAMADA: UTILITÁRIOS COMPARTILHADOS]
    ├── base/
    │   └── BaseDocument.java               [Classe-pai com userId, timestamps]
    │
    ├── exceptions/
    │   ├── ApiException.java               [Classe-pai]
    │   ├── BadRequestException.java
    │   ├── NotFoundException.java
    │   ├── UnauthorizedException.java
    │   ├── ForbiddenException.java
    │   └── GlobalExceptionHandler.java     [@RestControllerAdvice]
    │
    └── utils/
        ├── DateUtils.java
        └── [outras funções utilitárias]


src/test/java/com/controlfinance/          [TESTES - Espelhando src/main/]
├── transactions/
│   └── TransactionRepositoryIT.java        [Teste de integração com MongoDB]
└── [adicionar testes para outros módulos]
```

---

## 🎯 Regras de Importação

### Dependências Permitidas (De cima para baixo)

```
api/rest/ 
  ↓ pode depender de ↓
application/
  ↓ pode depender de ↓
domain/
  ↓ pode depender de ↓
infrastructure/

Todos podem depender de: common/
```

### Exemplos ✅ Permitidos

```java
// Controller pode chamar UseCase
@Autowired private CreateTransactionUseCase createUseCase;

// UseCase pode chamar Repository (via Port)
@Autowired private TransactionRepositoryPort repository;

// UseCase pode mapear DTO
@Autowired private TransactionMapper mapper;

// Qualquer um pode usar exceções
throw new NotFoundException("...");
```

### Exemplos ❌ NÃO Permitidos

```java
// Controller NÃO acessa repository diretamente
@Autowired private TransactionMongoRepository repo;  // ❌ Errado

// Domain (entity) NÃO importa de application
import com.controlfinance.transactions.application.*;  // ❌ Errado

// Domain NÃO importa de infrastructure
import com.controlfinance.transactions.infrastructure.*;  // ❌ Errado

// UseCase NÃO chama outro UseCase
new CreateTransactionUseCase(...).execute();  // ❌ Errado (injete)
```

---

## 📌 Tabela Rápida de Responsabilidades

| Camada | O que fica lá | O que NÃO fica | Depende de |
|--------|---|---|---|
| **api/rest/** | Controllers, validação HTTP | Lógica de negócio | application/ |
| **application/** | UseCases, Mappers, DTOs | Persistência, Spring | domain/ |
| **domain/** | Entidades, Ports, Eventos | Spring, HTTP, BD | [nada] |
| **infrastructure/** | Adapters, Config Spring | Lógica de negócio | domain/ |
| **common/** | Exceptions, Base, Utils | Contexto específico | [nada] |

---

## 🔍 Como Encontrar Uma Classe

### Preciso encontrar um UseCase?
→ `modules/{contexto}/application/usecases/{Acao}{Entidade}UseCase.java`

### Preciso encontrar uma Entidade?
→ `modules/{contexto}/domain/entities/{Entidade}.java`

### Preciso encontrar um Adapter de Persistência?
→ `modules/{contexto}/infrastructure/persistence/{Entidade}RepositoryAdapter.java`

### Preciso encontrar um Controller?
→ `api/rest/{Entidade}Controller.java`

### Preciso de uma exceção?
→ `common/exceptions/{TipoException}.java`

### Preciso de um DTO?
→ `modules/{contexto}/application/dto/{Entidade}Dto.java`

---

## 💡 Exemplo: Adicionar UseCase de Relatório

```
→ Criar arquivo:
   reporting/application/usecases/GetFinancialReportUseCase.java

→ Criar DTO:
   reporting/application/dto/FinancialReportDto.java

→ Se necessário, criar Mapper:
   reporting/application/mapper/FinancialReportMapper.java

→ Se necessário, criar Entity de domínio:
   reporting/domain/entities/FinancialReport.java

→ Se necessário, criar Port:
   reporting/domain/repositories/FinancialReportRepositoryPort.java

→ Se necessário, criar Adapter:
   reporting/infrastructure/persistence/FinancialReportRepositoryAdapter.java

→ Se necessário, criar Controller:
   api/rest/ReportingController.java
   (ou adicionar método ao existente se for mesmo recurso)

→ Sempre:
   - Use injeção de dependência
   - Publique eventos importantes
   - Adicione testes
```

---

**Para guias completos, consulte:**
- 📖 [README.md](README.md) - Descrição geral do projeto
- 📐 [ARCHITECTURE.md](ARCHITECTURE.md) - Boas práticas detalhadas
- 📝 [REFACTORING_SUMMARY.md](REFACTORING_SUMMARY.md) - Detalhes da refatoração

