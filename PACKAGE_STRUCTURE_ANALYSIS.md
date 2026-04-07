# Control Finance - Java Package Structure Analysis

**Date:** April 2026  
**Total Java Files:** 87  
**Architecture Pattern:** Hexagonal Architecture (Ports & Adapters) with Domain-Driven Design

---

## 1. PACKAGE TREE WITH FILE COUNTS

```
com.controlfinance/                          (87 files total)
├── ControlFinanceApplication.java           (1 file - Main entry point)
├── infrastructure/                          (13 files - Cross-cutting concerns)
│   ├── events/                              (3 files)
│   ├── mongo/                               (1 file)
│   ├── openapi/                             (1 file)
│   └── security/                            (8 files)
├── interfaces/                              (6 files - REST API layer)
│   └── rest/                                (6 controllers)
├── modules/                                 (67 files - Feature modules)
│   ├── audit/                               (3 files)
│   ├── auth/                                (8 files)
│   ├── budget/                              (6 files)
│   ├── categories/                          (11 files)
│   ├── notifications/                       (3 files)
│   ├── reporting/                           (2 files)
│   ├── transactions/                        (14 files)
│   └── user/                                (12 files)
└── shared/                                  (7 files - Cross-module utilities)
    ├── base/                                (1 file)
    ├── exceptions/                          (6 files)
    └── utils/                               (1 file)
```

---

## 2. DETAILED PACKAGE BREAKDOWN

### 2.1 Root Application Entry Point (1 file)

| File | Type | Purpose |
|------|------|---------|
| `ControlFinanceApplication` | Application | Spring Boot entry point |

---

### 2.2 Infrastructure Layer (13 files)

**Purpose:** Framework integration, cross-cutting concerns, and technical configuration

#### **2.2.1 Security (8 files)**
| File | Type | Purpose |
|------|------|---------|
| `SecurityConfig` | Configuration | Spring Security setup, CORS, JWT filter chain |
| `JwtService` | Service | JWT token generation/validation |
| `JwtAuthenticationFilter` | Filter | Request interceptor for JWT validation |
| `JwtProperties` | Configuration | JWT configuration properties (@ConfigurationProperties) |
| `CryptoService` | Service | Encryption/decryption for sensitive data |
| `EncryptionProperties` | Configuration | Encryption configuration properties |
| `SecurityUtils` | Utility | Helper methods for security context access |
| `AppRoles` | Enum/Constants | Application role definitions |

**Key Classes:**
- `JwtService` - implements token creation & validation
- `JwtAuthenticationFilter` - implements Spring's `OncePerRequestFilter`
- `SecurityConfig` - extends `WebSecurityConfigurerAdapter`

#### **2.2.2 Events (3 files)**
| File | Type | Purpose |
|------|------|---------|
| `DomainEvent` | Interface | Base interface/contract for domain events |
| `DomainEventPublisher` | Interface | Port for event publishing |
| `SpringDomainEventPublisher` | Service | Spring ApplicationEvents adapter implementation |

**Pattern:** Observer pattern for async event handling

#### **2.2.3 MongoDB Configuration (1 file)**
| File | Type | Purpose |
|------|------|---------|
| `MongoConfig` | Configuration | MongoDB setup, connection pooling, auditing |

#### **2.2.4 OpenAPI (1 file)**
| File | Type | Purpose |
|------|------|---------|
| `OpenApiConfig` | Configuration | Springdoc OpenAPI setup (Swagger API documentation) |

---

### 2.3 REST Interfaces Layer (6 files)

**Purpose:** HTTP endpoint definitions, request/response handling

| File | Type | HTTP Path | Depends On |
|------|------|-----------|-----------|
| `AuthController` | Controller | `/api/v1/auth` | LoginUseCase, RegisterUserUseCase, RefreshTokenUseCase |
| `UserController` | Controller | `/api/v1/users` | GetMyProfileUseCase, UpdateProfileUseCase, Enable2FAUseCase, ChangePasswordUseCase, DeleteAccountUseCase |
| `TransactionController` | Controller | `/api/v1/transactions` | CreateTransactionUseCase, UpdateTransactionUseCase, DeleteTransactionUseCase, SearchTransactionsUseCase |
| `CategoryController` | Controller | `/api/v1/categories` | CreateCategoryUseCase, UpdateCategoryUseCase, DeleteCategoryUseCase, ListCategoriesUseCase |
| `ReportingController` | Controller | `/api/v1/reporting` | GetDashboardSummaryUseCase |
| `HealthController` | Controller | `/api/v1/health` | None (health check) |

**Architecture:** Controllers use constructor injection of UseCases (@RequiredArgsConstructor)

---

### 2.4 Business Modules (67 files)

**Architecture Pattern per Module:**
```
├── application/
│   ├── dto/          (Data Transfer Objects)
│   ├── mapper/       (MapStruct mappers, Entity ↔ DTO)
│   ├── usecases/     (Business logic, @Service)
│   └── handlers/     (Event listeners, @EventListener)
├── domain/
│   ├── entities/     (MongoDB @Document, extends BaseDocument)
│   ├── enums/        (Type enums)
│   ├── events/       (Domain events, implements DomainEvent)
│   ├── repositories/ (Port interfaces, NOT implementation)
│   └── services/     (Domain-specific services, e.g., TotpService)
└── infrastructure/
    └── persistence/  (Repository Adapters, MongoDB)
```

#### **2.4.1 Authentication Module (8 files)**

**Structure:**
```
modules/auth/
├── application/
│   ├── dto/
│   │   ├── AuthTokensDto
│   │   ├── LoginRequest
│   │   ├── RegisterRequest
│   │   └── RefreshRequest
│   └── usecases/
│       ├── LoginUseCase
│       ├── RegisterUserUseCase
│       └── RefreshTokenUseCase
├── domain/
│   └── events/
│       └── UserRegisteredEvent
```

**Key Dependencies:**
- Uses `SecurityUtils` from infrastructure
- Publishes `UserRegisteredEvent` via `DomainEventPublisher`
- Interacts with `UserRepositoryPort`

**Event Flow:** RegisterUserUseCase → publishes UserRegisteredEvent → AuditEventHandler listens

#### **2.4.2 User Module (12 files)**

**Structure:**
```
modules/user/
├── application/
│   ├── dto/
│   │   └── UserDto
│   ├── mapper/
│   │   └── UserMapper
│   └── usecases/
│       ├── GetMyProfileUseCase
│       ├── UpdateProfileUseCase
│       ├── ChangePasswordUseCase
│       ├── Enable2FAUseCase
│       └── DeleteAccountUseCase
├── domain/
│   ├── entities/
│   │   └── User (extends BaseDocument)
│   ├── repositories/
│   │   └── UserRepositoryPort (interface)
│   └── services/
│       └── TotpService (Time-based OTP for 2FA)
└── infrastructure/
    └── persistence/
        ├── UserMongoRepository (extends MongoRepository)
        └── UserRepositoryAdapter (implements UserRepositoryPort)
```

**Key Classes:**
- `User`: MongoDB entity, multi-tenant (userId), password hashing
- `TotpService`: Implements TOTP for 2FA
- `UserRepositoryPort`: Interface for user persistence

**Dependencies:** Uses `CryptoService` for encryption

#### **2.4.3 Transactions Module (14 files)**

**Structure:**
```
modules/transactions/
├── application/
│   ├── dto/
│   │   └── TransactionDto
│   ├── mapper/
│   │   └── TransactionMapper
│   └── usecases/
│       ├── CreateTransactionUseCase
│       ├── UpdateTransactionUseCase
│       ├── DeleteTransactionUseCase
│       ├── SearchTransactionsUseCase
│       └── ComputeMonthSpentService
├── domain/
│   ├── entities/
│   │   └── Transaction (extends BaseDocument)
│   ├── enums/
│   │   ├── TransactionType (INCOME, EXPENSE)
│   │   └── TransactionStatus
│   ├── events/
│   │   └── TransactionCreatedEvent
│   ├── repositories/
│   │   └── TransactionRepositoryPort
└── infrastructure/
    └── persistence/
        ├── TransactionMongoRepository
        └── TransactionRepositoryAdapter
```

**Key Classes:**
- `Transaction`: MongoDB document, BigDecimal amount, supports income/expense, multi-tenant
- `TransactionCreatedEvent`: Triggers audit logging and budget evaluation
- `ComputeMonthSpentService`: Calculates spending by category per month
- `SearchTransactionsUseCase`: Supports filtering by date range, category, type, status

**Critical Dependencies:**
- → `BudgetEvaluationService` (budget module)
- → `DomainEventPublisher` (infrastructure)
- → `SecurityUtils` (infrastructure)

#### **2.4.4 Budget Module (6 files)**

**Structure:**
```
modules/budget/
├── application/
│   └── services/
│       └── BudgetEvaluationService
├── domain/
│   ├── entities/
│   │   └── Budget (extends BaseDocument)
│   ├── events/
│   │   └── BudgetLimitReachedEvent
│   └── repositories/
│       └── BudgetRepositoryPort
└── infrastructure/
    └── persistence/
        ├── BudgetMongoRepository
        └── BudgetRepositoryAdapter
```

**Key Classes:**
- `BudgetEvaluationService`: Called by `CreateTransactionUseCase`, evaluates if transaction exceeds 90% or 100% of budget
- `BudgetLimitReachedEvent`: Published when budget exceeds threshold
- Triggers notifications through `BudgetNotificationHandler`

**Cross-Module Integration:** Central dependency point for transaction creation

#### **2.4.5 Categories Module (11 files)**

**Structure:**
```
modules/categories/
├── application/
│   ├── dto/
│   │   └── CategoryDto
│   ├── mapper/
│   │   └── CategoryMapper
│   └── usecases/
│       ├── CreateCategoryUseCase
│       ├── UpdateCategoryUseCase
│       ├── DeleteCategoryUseCase
│       └── ListCategoriesUseCase
├── domain/
│   ├── entities/
│   │   ├── Category (extends BaseDocument)
│   │   └── SubCategory (nested within Category)
│   └── repositories/
│       └── CategoryRepositoryPort
└── infrastructure/
    └── persistence/
        ├── CategoryMongoRepository
        └── CategoryRepositoryAdapter
```

**Key Classes:**
- `Category`: Root entity with nested SubCategory objects
- Hierarchical structure (main category → sub-categories)
- Multi-tenant design (userId field)

#### **2.4.6 Notifications Module (3 files)**

**Structure:**
```
modules/notifications/
├── application/
│   └── handlers/
│       └── BudgetNotificationHandler (@EventListener)
├── domain/
│   └── entities/
│       └── Notification (extends BaseDocument)
└── infrastructure/
    └── persistence/
        └── NotificationMongoRepository
```

**Pattern:** Event-driven
- Listens to `BudgetLimitReachedEvent`
- Creates `Notification` entities when budget exceeds threshold
- Asynchronous via Spring Events

#### **2.4.7 Audit Module (3 files)**

**Structure:**
```
modules/audit/
├── application/
│   └── handlers/
│       └── AuditEventHandler (@EventListener)
├── domain/
│   └── entities/
│       └── AuditLog (extends BaseDocument)
└── infrastructure/
    └── persistence/
        └── AuditLogMongoRepository
```

**Pattern:** Global event listener
- `AuditEventHandler` listens to ALL `DomainEvent` instances
- Serializes event payload to JSON
- Creates audit trail for compliance/debugging
- No explicit dependency on other modules

**Usage:** Automatically captures every domain event across all modules

#### **2.4.8 Reporting Module (2 files)**

**Structure:**
```
modules/reporting/
├── application/
│   ├── dto/
│   │   └── DashboardSummaryDto
│   └── usecases/
│       └── GetDashboardSummaryUseCase
```

**Purpose:** Read-only aggregation of data for dashboard
- Summarizes user's financial overview
- Likely queries multiple modules (transactions, budget, categories)
- Simple data structure without complex domain logic

---

### 2.5 Shared Layer (7 files)

**Purpose:** Common utilities and base classes shared across all modules

#### **2.5.1 Base Entity (1 file)**
```java
BaseDocument (abstract class)
├── id: String (@Id)
├── userId: String (multi-tenant key)
├── createdAt: Instant (@CreatedDate)
└── updatedAt: Instant (@LastModifiedDate)
```

**Pattern:** All MongoDB entities extend BaseDocument
**Multi-tenancy:** userId field filters all queries by default

#### **2.5.2 Exception Hierarchy (6 files)**

```
Exceptions (all extend RuntimeException):
├── ApiException (base)
├── BadRequestException (400)
├── UnauthorizedException (401)
├── ForbiddenException (403)
├── NotFoundException (404)
└── GlobalExceptionHandler (@ControllerAdvice)
```

**Handler:** `GlobalExceptionHandler`
- Maps exceptions to HTTP status codes
- Implements uniform error response format
- Centralized error handling for all REST endpoints

#### **2.5.3 Utilities (1 file)**
```
DateUtils
├── Instant conversions
├── Zone ID handling
└── Date formatting utilities
```

Used by: Transaction, Budget, Reporting modules

---

## 3. CLASS CLASSIFICATIONS

### 3.1 Entity Classes (Domain Entities)
All extend `BaseDocument`, annotated with `@Document`

| Module | Entity | Purpose |
|--------|--------|---------|
| transactions | `Transaction` | Financial transaction record |
| budget | `Budget` | Spending limits per category |
| categories | `Category`, `SubCategory` | Transaction categorization |
| user | `User` | User account information |
| notifications | `Notification` | Budget alert records |
| audit | `AuditLog` | Event audit trail |

**Common Fields:**
- `id` (MongoDB ObjectId)
- `userId` (multi-tenant)
- `createdAt`, `updatedAt` (automatic timestamps)

### 3.2 Repository Classes

**Port Interfaces (Domain Layer):**
- `TransactionRepositoryPort`
- `BudgetRepositoryPort`
- `CategoryRepositoryPort`
- `UserRepositoryPort`

**MongoDB Repositories (Infrastructure):**
- `TransactionMongoRepository` extends `MongoRepository<Transaction, String>`
- `BudgetMongoRepository`
- `CategoryMongoRepository`
- `UserMongoRepository`
- `NotificationMongoRepository` (no port interface)
- `AuditLogMongoRepository` (no port interface)

**Repository Adapters (Adapter Pattern):**
- `TransactionRepositoryAdapter` implements `TransactionRepositoryPort`
- `BudgetRepositoryAdapter`
- `CategoryRepositoryAdapter`
- `UserRepositoryAdapter`

### 3.3 UseCase / Service Classes (Application Layer)
Annotated with `@Service`, constructor-injected dependencies

**Auth Module:**
- `LoginUseCase` - authenticate user
- `RegisterUserUseCase` - create new user account
- `RefreshTokenUseCase` - refresh expired JWT

**User Module:**
- `GetMyProfileUseCase` - retrieve current user
- `UpdateProfileUseCase` - modify profile
- `Enable2FAUseCase` - activate TOTP
- `ChangePasswordUseCase` - password reset
- `DeleteAccountUseCase` - account deletion

**Transaction Module:**
- `CreateTransactionUseCase` - record income/expense
- `UpdateTransactionUseCase` - modify transaction
- `DeleteTransactionUseCase` - remove transaction
- `SearchTransactionsUseCase` - query with filters
- `ComputeMonthSpentService` - calculate monthly spending

**Category Module:**
- `CreateCategoryUseCase`
- `UpdateCategoryUseCase`
- `DeleteCategoryUseCase`
- `ListCategoriesUseCase`

**Budget Module:**
- `BudgetEvaluationService` - evaluate spending against limits

**Reporting Module:**
- `GetDashboardSummaryUseCase` - aggregate financial summary

### 3.4 Controller Classes (Interfaces/REST Layer)
Annotated with `@RestController`, `@RequestMapping`

| Controller | Endpoints | Depends On |
|------------|-----------|-----------|
| `AuthController` | POST /auth/login, /auth/register, /auth/refresh | Auth UseCases |
| `UserController` | GET/PUT /users/profile, DELETE /users | User UseCases |
| `TransactionController` | POST/PUT/DELETE/GET /transactions | Transaction UseCases |
| `CategoryController` | POST/PUT/DELETE/GET /categories | Category UseCases |
| `ReportingController` | GET /reporting/dashboard | Reporting UseCases |
| `HealthController` | GET /health | None |

### 3.5 DTO Classes (Data Transfer Objects)
Used for API request/response serialization

- `TransactionDto` - request/response for transaction endpoints
- `CategoryDto` - request/response for category endpoints
- `UserDto` - request/response for user endpoints
- `AuthTokensDto` - response with access/refresh tokens
- `LoginRequest`, `RegisterRequest`, `RefreshRequest` - auth requests
- `DashboardSummaryDto` - dashboard data response

**Validation:** Uses Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Positive`)

### 3.6 Mapper Classes (Conversion)
MapStruct interface-based mappers

```
TransactionMapper: Transaction ↔ TransactionDto
UserMapper: User ↔ UserDto
CategoryMapper: Category ↔ CategoryDto
```

**Pattern:** `@Mapper` interface with default methods, Spring-managed

### 3.7 Event Classes (Domain Events)
Implement `DomainEvent` interface (record classes)

| Event | Triggers | Listeners |
|-------|----------|-----------|
| `TransactionCreatedEvent` | CreateTransactionUseCase | AuditEventHandler, BudgetNotificationHandler |
| `BudgetLimitReachedEvent` | BudgetEvaluationService | BudgetNotificationHandler, AuditEventHandler |
| `UserRegisteredEvent` | RegisterUserUseCase | AuditEventHandler |

**Publisher:** `SpringDomainEventPublisher` uses Spring's `ApplicationEventPublisher`

### 3.8 Configuration Classes
Spring configuration, annotated with `@Configuration`

- `SecurityConfig` - security setup, JWT filter
- `MongoConfig` - MongoDB auditing, converters
- `OpenApiConfig` - Swagger/OpenAPI documentation

### 3.9 Filter Classes (Spring Filters)

- `JwtAuthenticationFilter` extends `OncePerRequestFilter`
  - Validates JWT token on every request
  - Sets Spring Security `Authentication` context

### 3.10 Utility/Helper Classes

**Security:**
- `JwtService` - token generation, validation, claims extraction
- `CryptoService` - AES encryption/decryption
- `SecurityUtils` - get current userId from context
- `AppRoles` - role constants (ADMIN, USER, etc.)

**Domain Service:**
- `TotpService` (user module) - TOTP/2FA implementations

**Utils:**
- `DateUtils` - date conversion utilities

**Event Handler:**
- `AuditEventHandler` - listens to all domain events
- `BudgetNotificationHandler` - listens to budget events

### 3.11 Properties/Configuration Classes
`@ConfigurationProperties`

- `JwtProperties` - JWT secret, expiration time
- `EncryptionProperties` - encryption algorithm, key

### 3.12 Enum Classes

**Domain Enums:**
- `TransactionType` (INCOME, EXPENSE)
- `TransactionStatus` (...)
- `AppRoles` (ADMIN, USER, etc.)

---

## 4. CURRENT LAYER STRUCTURE

### **Architecture Layers:**

```
REST API Layer (Interfaces)
    ↓
Application Layer (UseCases, Mappers, DTOs)
    ↓
Domain Layer (Entities, Events, Repositories [Ports])
    ↓
Infrastructure Layer (Repository Adapters, Persistence, Security, Config)
```

### **Module Organization:**

**Each business module follows:**
```
module/
├── application/      - Use cases, mappers, DTOs, handlers
├── domain/           - Entities, events, repository PORTS
├── infrastructure/   - Repository adapters, persistence impl
```

**Cross-cutting concerns (Shared):**
```
shared/
├── base/             - BaseDocument
├── exceptions/       - Exception handlers, custom exceptions
├── utils/            - Utilities (date, logging, etc.)

infrastructure/
├── security/         - JWT, crypto, roles
├── events/           - Event pub/sub
├── mongo/            - Database config
├── openapi/          - API documentation
```

### **Dependency Direction (Hexagonal Architecture):**

```
interfaces/rest/ → modules/{module}/application/ → modules/{module}/domain/
                                                         ↓
                                            modules/{module}/infrastructure/

                    ↑              ↑
                    └──────────────┘
                  shared (utils, exceptions, base)
                  infrastructure (security, events, mongo config)
```

**Key Principle:** Domain layer has NO dependencies on infrastructure or frameworks

---

## 5. CROSS-PACKAGE DEPENDENCIES & IMPORTS

### 5.1 High-Impact Dependencies

#### **Transaction Module → Budget Module**
```java
// CreateTransactionUseCase imports:
import com.controlfinance.modules.budget.application.services.BudgetEvaluationService;

// On expense creation, evaluates if budget threshold exceeded
budgetEval.evaluateOnExpenseCreated(transaction, monthSpentAfter);
```
**Impact:** Tight coupling, budget service called directly in transaction creation
**Dependency Type:** Application Layer to Application Layer

#### **Budget Module → Notifications Module**
```java
// BudgetEvaluationService publishes event:
events.publish(new BudgetLimitReachedEvent(...));

// BudgetNotificationHandler listens:
@EventListener
public void on(BudgetLimitReachedEvent event) { ... }
```
**Impact:** Decoupled via events, asynchronous
**Dependency Type:** Domain Event (async)

#### **All Modules → Infrastructure Events**
```java
import com.controlfinance.infrastructure.events.DomainEventPublisher;
```
**Modules affected:** auth, budget, transactions
**Pattern:** Publish domain events via port

#### **All Modules → Shared Base**
```java
import com.controlfinance.shared.base.BaseDocument;
extends BaseDocument
```
**Scope:** All entities

#### **All Modules → Shared Exceptions**
```java
import com.controlfinance.shared.exceptions.*;
throw new BadRequestException(...);
```
**Scope:** All application layers

#### **UseCases → Infrastructure Security**
```java
// In UseCases:
import com.controlfinance.infrastructure.security.SecurityUtils;
String userId = SecurityUtils.getCurrentUserId();
```
**Modules:** auth, user, transactions, categories
**Pattern:** Get current tenant context

#### **Repositories → Infrastructure Mongo**
```java
// MongoRepository implementations use:
import org.springframework.data.mongodb.core.MongoTemplate;
```
**Scope:** All repository adapters

---

### 5.2 Import Dependency Matrix

| Source | Imports From | Reason | Type |
|--------|-------------|--------|------|
| **interfaces/rest/** | modules/{*}/application | UseCases, DTOs | Direct |
| **application/*UseCase** | domain/, infrastructure/*port | Business logic, events | Direct |
| **application/mapper** | domain/entities, dto | Entity-DTO mapping | Direct |
| **infrastructure/adapter** | domain/repositories, mongo | Port implementation | Direct |
| **handlers/@EventListener** | All module entities, domain events | Event processing | Direct |
| **domain/entities** | shared/base | BaseDocument inheritance | Direct |
| **All layers** | shared/exceptions, utils | Common utilities | Direct |

---

### 5.3 Problematic Dependencies (Potential Refactoring Points)

#### **Issue 1: Application-to-Application Dependency**
```
CreateTransactionUseCase → BudgetEvaluationService
```
**Problem:** Both are in application layer, but BudgetEvaluationService is called directly
**Refactoring Option:** 
- Move BudgetEvaluationService to domain level
- Create BudgetEvaluationPort interface
- Use event-driven approach instead

#### **Issue 2: No Repository Port for Notification/Audit**
```
AuditEventHandler → AuditLogMongoRepository (direct, no port)
BudgetNotificationHandler → NotificationMongoRepository (direct, no port)
```
**Problem:** These modules bypass port abstraction
**Improvement:** Define repository ports for consistency

#### **Issue 3: Module Interdependency**
```
Transactions ← → Budget (circular via events)
```
**Current:** Decoupled via events (good)
**Risk:** Future direct dependencies possible

---

## 6. PACKAGE ORGANIZATION SUMMARY

### **Organizational Pattern: Feature-Based Modularity**

```
Each domain feature (transactions, budget, users, etc.) is self-contained
with its own layers (application, domain, infrastructure).

Benefits:
✓ Clear module boundaries
✓ Easy to extract module as separate service
✓ Team ownership per module
✓ Scalable (new features = new module)

Challenges:
✗ Cross-module dependencies (transactions ↔ budget)
✗ Shared repository setup (audit, notifications)
✗ Event-driven complexity (eventual consistency)
```

### **Current Strengths**

1. **Clear Layer Separation**
   - Controllers → UseCases → Entities
   - Infrastructure separate from domain

2. **Multi-tenancy Built-in**
   - BaseDocument includes userId
   - Security context integration via SecurityUtils

3. **Event-Driven Architecture**
   - Audit trail automatic
   - Budget notifications async
   - Loose coupling between modules

4. **DDD Principles**
   - Repository ports (domain abstractions)
   - Value objects (BigDecimal for amounts)
   - Domain events
   - Ubiquitous language

5. **Type Safety**
   - Enums for TransactionType, TransactionStatus
   - MapStruct typed mappers
   - Record-based events

### **Areas for Potential Improvement**

1. **Repository Consistency**
   - Audit & Notification modules lack port interfaces
   - Consider adding for consistency

2. **Service Location**
   - BudgetEvaluationService in application layer
   - Consider domain service traits

3. **Cross-Module Coordination**
   - Direct dependency: Transactions → Budget service
   - Could be fully event-driven

4. **DTOs Organization**
   - No shared request/response wrappers
   - Each module has own types

5. **Mapper Performance**
   - MapStruct generates code at build time
   - Consider if mapping overhead is acceptable at scale

---

## 7. FILE COUNT SUMMARY BY LAYER

| Layer | Count | Percentage |
|-------|-------|-----------|
| Infrastructure | 13 | 15% |
| REST Interfaces | 6 | 7% |
| Domain Entities | 8 | 9% |
| Domain Repositories (Ports) | 5 | 6% |
| Domain Events | 4 | 5% |
| Domain Enums | 3 | 3% |
| Application UseCases | 18 | 21% |
| Application Services | 3 | 3% |
| Application Mappers | 3 | 3% |
| Application DTOs | 8 | 9% |
| Infrastructure Repositories | 11 | 13% |
| Infrastructure Handlers | 2 | 2% |
| Shared Base/Utils/Exceptions | 7 | 8% |
| **TOTAL** | **87** | **100%** |

---

## 8. DEPENDENCY GRAPH (Key Flows)

### **Authentication Flow**
```
AuthController
    ↓
LoginUseCase / RegisterUserUseCase
    ↓
UserRepositoryPort
    ↓
UserRepositoryAdapter
    ↓
UserMongoRepository (MongoDB)
    
Publishes: UserRegisteredEvent → AuditEventHandler
           JwtService generates token
```

### **Transaction Creation Flow**
```
TransactionController
    ↓
CreateTransactionUseCase
    ├→ TransactionMapper (DTO → Entity)
    ├→ TransactionRepositoryPort (save)
    ├→ BudgetEvaluationService (eval limits)
    │    ├→ BudgetRepositoryPort
    │    └→ Publishes BudgetLimitReachedEvent
    ├→ ComputeMonthSpentService (spending calc)
    └→ Publishes TransactionCreatedEvent
    
Listeners:
    - AuditEventHandler (audit trail)
    - BudgetNotificationHandler (if limit reached)
```

### **User Action Flow**
```
UserController
    ↓
UpdateProfileUseCase / Enable2FAUseCase
    ├→ SecurityUtils (get current userId)
    └→ UserRepositoryPort
    
Publishes: Domain events → AuditEventHandler
```

---

## Refactoring Considerations

If planning architectural refactoring:

1. **Define explicit boundaries** - mark public APIs per module
2. **Extract Notifications** - make independent service
3. **Consolidate Persistence** - unified repository pattern
4. **Async Processing** - consider message queue (Kafka/RabbitMQ) vs Spring Events
5. **API Contracts** - version endpoints, document changes
6. **Caching Layer** - Redis for category lookups, user profiles
7. **Event Sourcing** - for audit/compliance requirements

---

## Files for Further Investigation

**Core business logic:**
- [CreateTransactionUseCase](src/main/java/com/controlfinance/modules/transactions/application/usecases/CreateTransactionUseCase.java)
- [BudgetEvaluationService](src/main/java/com/controlfinance/modules/budget/application/services/BudgetEvaluationService.java)
- [TransactionRepositoryAdapter](src/main/java/com/controlfinance/modules/transactions/infrastructure/persistence/TransactionRepositoryAdapter.java)

**Security & infrastructure:**
- [JwtAuthenticationFilter](src/main/java/com/controlfinance/infrastructure/security/JwtAuthenticationFilter.java)
- [SecurityConfig](src/main/java/com/controlfinance/infrastructure/security/SecurityConfig.java)

**Event handling:**
- [AuditEventHandler](src/main/java/com/controlfinance/modules/audit/application/handlers/AuditEventHandler.java)
- [SpringDomainEventPublisher](src/main/java/com/controlfinance/infrastructure/events/SpringDomainEventPublisher.java)
