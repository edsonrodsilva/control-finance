# Clean Architecture & DDD - Guia de Boas Práticas

Este documento fornece diretrizes para manter a arquitetura limpa e organizada durante a evolução do projeto control-finance.

## Padrão de Arquitetura

O projeto segue **Clean Architecture** combinada com **Domain-Driven Design (DDD)** em uma estrutura **Hexagonal (Ports & Adapters)**.

### Dependências Entre Camadas

```text
HTTP Request
    ↓
[api/rest] Controllers
    ↓
[application] UseCases & Mappers
    ↓
[domain] Entities & Repository Ports (interfaces)
    ↓
[infrastructure] Repository Adapters & Services
    ↓
Database / External Services
```

**Regra de Ouro:** Sempre importar de cima para baixo. Nunca de baixo para cima.

## Estrutura de um Novo Contexto

Ao criar uma nova feature ou bounded context:

```plaintext
src/main/java/com/controlfinance/modules/{contexto}/
├── application/
│   ├── dto/
│   │   ├── {Entity}Dto.java           ← DTO de saída
│   │   ├── {Entity}Request.java       ← DTO de entrada (opcional)
│   │   └── ...
│   ├── mapper/
│   │   └── {Entity}Mapper.java        ← MapStruct mapper
│   └── usecases/
│       ├── Create{Entity}UseCase.java
│       ├── Update{Entity}UseCase.java
│       ├── Delete{Entity}UseCase.java
│       └── ...
├── domain/
│   ├── entities/
│   │   └── {Entity}.java              ← Estende BaseDocument
│   ├── repositories/
│   │   └── {Entity}RepositoryPort.java ← Interface (contrato)
│   ├── events/
│   │   └── {Entity}CreatedEvent.java  ← Eventos de domínio (se necessário)
│   ├── services/
│   │   └── {Domain}Service.java       ← Serviços puros de domínio
│   └── enums/
│       └── {Entity}Status.java        ← Enumeradores do domínio
└── infrastructure/
    └── persistence/
        ├── {Entity}MongoRepository.java  ← Spring Data interface
        └── {Entity}RepositoryAdapter.java ← Implementação do Port
```

    Observação: neste projeto, o diretório `modules/` representa os bounded contexts de domínio
    (auth, user, transactions etc.). O nome pode ser renomeado para `domain/` no futuro, mas
    mantivemos `modules/` para preservar histórico e estabilidade do pacote.

## Nomenclatura

| O quê | Padrão | Exemplo |
|-------|--------|---------|
| UseCase | `{Ação}{Entidade}UseCase` | `CreateTransactionUseCase` |
| Repository Port | `{Entidade}RepositoryPort` | `TransactionRepositoryPort` |
| Repository Adapter | `{Entidade}RepositoryAdapter` | `TransactionRepositoryAdapter` |
| MongoDB Spring Data | `{Entidade}MongoRepository` | `TransactionMongoRepository` |
| Entidade | Singular | `Transaction`, não `Transactions` |
| DTO Saída | `{Entidade}Dto` | `TransactionDto` |
| DTO Entrada | `{Entidade}Request` | `CreateTransactionRequest` |
| Mapper | `{Entidade}Mapper` | `TransactionMapper` |
| Enum | `{Entidade}{Componente}` | `TransactionStatus`, `TransactionType` |
| Evento | `{Entidade}{Ação}Event` | `TransactionCreatedEvent` |
| Service Domínio | `{Domínio}Service` | `BudgetEvaluationService` |
| Controller | `{Entidade}Controller` | `TransactionController` |
| Rota | `/api/v1/{recurso}` | `/api/v1/transactions` |

## Responsabilidades por Camada

### 1. API (api/rest/)

**O quê faz:** Recebe HTTP requests e retorna HTTP responses

**Responsabilidades:**

- Validação de entrada (@Valid)
- Autenticação e autorização
- Serialização/desserialização de JSON
- Status HTTP corretos

**O que NÃO faz:**

- Lógica de negócio
- Acesso direto a banco de dados
- Orquestração de use cases complexos

**Exemplo:**
```java
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
  @PostMapping
  public ResponseEntity<TransactionDto> create(@Valid @RequestBody TransactionDto dto) {
    return ResponseEntity.ok(createUseCase.execute(dto));
  }
}
```

### 2. Application (*/application/)

**O quê faz:** Orquestra a lógica de aplicação (use cases)

**Responsabilidades:**

- Implementar casos de uso
- Chamar services de domínio
- Mapear DTOs ↔ Entidades (MapStruct)
- Transações (@Transactional)

**Padrão UseCase:**
```java
@Service
@RequiredArgsConstructor
public class CreateTransactionUseCase {
  private final TransactionRepositoryPort repository;
  private final TransactionMapper mapper;

  public TransactionDto execute(TransactionDto dto) {
    // 1. Mapear DTO → Entity
    Transaction tx = mapper.toEntity(dto);
    
    // 2. Chamar repository
    Transaction saved = repository.save(tx);
    
    // 3. Mapear Entity → DTO
    return mapper.toDto(saved);
  }
}
```

### 3. Domain (*/domain/)

**O quê faz:** Encapsula as regras de negócio

**Responsabilidades:**

- Definir entidades
- Validações de negócio
- Contratos (Repository Ports)
- Eventos de domínio
- Value Objects

**O que NÃO faz:**

- Persistência (MongoDB)
- HTTP
- Spring annotations (exceto @Document, @Id, @Field)

**Exemplo de Entidade:**
```java
@Document(collection = "transactions")
public class Transaction extends BaseDocument {
  private TransactionType type;
  private BigDecimal amount;
  private String description;
  
  // Validações de negócio
  public void validateAmount() {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Amount must be positive");
    }
  }
}
```

**Repository Port (Interface):**
```java
public interface TransactionRepositoryPort {
  Transaction save(Transaction tx);
  Optional<Transaction> findById(String id);
  List<Transaction> findByUserId(String userId);
  void delete(String id);
}
```

### 4. Infrastructure (infrastructure/)

#### Em cada contexto (*/infrastructure/persistence/)

- Implementações de Repository Ports
- Queries MongoDB customizadas
- Mapeamento ORM (se necessário)

#### Compartilhado (/infrastructure/)

- **security/** - JWT, SecurityConfig, JwtService, CryptoService
- **events/** - Event listeners (auditoria, notificações)
- **mongo/** - Configuração MongoDB
- **openapi/** - Swagger UI
- **config/** - Beans gerais

**Exemplo de Repository Adapter:**
```java
@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {
  private final TransactionMongoRepository mongoRepository;
  private final TransactionMapper mapper;

  @Override
  public Transaction save(Transaction tx) {
    TransactionDocument doc = mapper.toDocument(tx);
    TransactionDocument saved = mongoRepository.save(doc);
    return mapper.toDomain(saved);
  }
}
```

### 5. Common (common/)

**Responsabilidades:**

- `base/` - BaseDocument (multi-tenant)
- `exceptions/` - Exceções globais, GlobalExceptionHandler
- `utils/` - Funções compartilhadas, constantes

Importado por todas as camadas.

## Diretrizes Específicas

### Multi-Tenancy

Todas as entidades devem herdar de `BaseDocument`:

```java
@Document(collection = "transactions")
public class Transaction extends BaseDocument {
  // userId vem do BaseDocument
  // Sempre filtrar por userId nas queries
}
```

No repository:
```java
List<Transaction> findByUserIdAndType(String userId, TransactionType type);
```

### Mapeamento com MapStruct

Crie interfaces com `@Mapper`:

```java
@Mapper(componentModel = "spring")
public interface TransactionMapper {
  TransactionDto toDto(Transaction entity);
  Transaction toEntity(TransactionDto dto);
  
  @Mapping(target = "userId", ignore = true)
  void updateEntityFromDto(TransactionDto dto, @MappingTarget Transaction entity);
}
```

### Eventos de Domínio

Defina eventos importantes:

```java
@Getter
public class TransactionCreatedEvent extends ApplicationEvent {
  private final String transactionId;
  private final String userId;

  public TransactionCreatedEvent(Object source, String transactionId, String userId) {
    super(source);
    this.transactionId = transactionId;
    this.userId = userId;
  }
}
```

Publique em UseCases:
```java
applicationEventPublisher.publishEvent(
  new TransactionCreatedEvent(this, saved.getId(), saved.getUserId())
);
```

Ouça com listeners:
```java
@Component
@RequiredArgsConstructor
public class AuditEventHandler {
  @EventListener
  public void onTransactionCreated(TransactionCreatedEvent event) {
    // Gravar auditoria
  }
}
```

### Tratamento de Exceções

Use exceções de negócio em `common/exceptions/`:

```java
// ✅ Bom
if (user == null) {
  throw new NotFoundException("User not found");
}

// ❌ Ruim
if (user == null) {
  throw new EntityNotFoundException();
}
```

Cenários tratados pelo `GlobalExceptionHandler`:

- `BadRequestException` → 400
- `NotFoundException` → 404
- `UnauthorizedException` → 401
- `ForbiddenException` → 403
- Validação → 422

### Testes

Organize mirrors de `src/main`:

```plaintext
src/test/java/com/controlfinance/{contexto}/
├── application/usecases/
│   └── Create{Entity}UseCaseTest.java (Unit Test)
├── domain/
│   └── {Entity}Test.java
└── infrastructure/persistence/
    └── {Entity}RepositoryAdapterIT.java (Integration Test)
```

Sufixos:

- `Test` = Unit Test (mock)
- `IT` = Integration Test (real DB container)

## Checklist para Nova Feature

- [ ] Criar estrutura de pacotes (domain, application, infrastructure)
- [ ] Implementar entidade estendendo `BaseDocument`
- [ ] Criar Repository Port (interface em domain/repositories/)
- [ ] Criar Repository Adapter (implementação em infrastructure/persistence/)
- [ ] Criar DTOs e Mapper (MapStruct)
- [ ] Implementar UseCases
- [ ] Criar Controller em `api/rest/`
- [ ] Adicionar testes unitários dos UseCases
- [ ] Adicionar testes de integração do Repository
- [ ] Documentar API em Swagger
- [ ] Atualizar README com endpoints

## Boas Práticas Gerais

✅ **FAÇA:**
- Injetar via construtor
- Usar `@RequiredArgsConstructor` com Lombok
- Nomes de classes descritivos e únicos
- Um arquivo por classe (raras exceções)
- Commits bem descritivos
- Manter `.git/` limpo

❌ **NÃO FAÇA:**
- Usar `new` em camadas de aplicação (injete)
- Controllers chamando repositories diretamente
- Lógica de negócio em controllers
- Entidades com métodos de persistência
- UseCases chamando outros UseCases
- Importações circulares
- Scripts SQL hard-coded (use collections MongoDB bem modeladas)

## Ferramentas Úteis

```bash
# Compilar
./mvnw clean compile

# Testes
./mvnw test

# Build completo
./mvnw clean package

# Verificar imports não usados
./mvnw dependency:analyze

# Listar dependências
./mvnw dependency:tree

# Swagger UI
http://localhost:8080/swagger-ui.html

# Health Check
http://localhost:8080/actuator/health
```

---

Para dúvidas sobre a arquitetura, consulte os padrões já implementados nos contextos existentes: `auth`, `transactions`, `categories`, `budget`.
