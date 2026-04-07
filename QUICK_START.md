# 🚀 Guia Rápido - Após Refatoração

## O que mudou?

### Movimentações Principais
```
interfaces/rest/   →  api/rest/          (Controllers HTTP)
shared/            →  common/            (Exceptions, Base, Utils)
```

### Nova Estrutura
```
api/rest/                    ← Controllers
{contexto}/application/      ← UseCases, DTOs, Mappers
{contexto}/domain/           ← Entidades, Repository Ports, Eventos
{contexto}/infrastructure/   ← MongoDB Adapters
infrastructure/              ← Compartilhado (Security, Events, Config)
common/                      ← Compartilhado (Base, Exceptions, Utils)
```

---

## ✅ Build & Execução

```bash
# Verificar que compila
./mvnw clean compile

# Build completo
./mvnw clean package

# Rodar testes
./mvnw test

# Executar aplicação
docker compose up -d              # MongoDB
./mvnw spring-boot:run

# Swagger UI
http://localhost:8080/swagger-ui.html
```

---

## 🎯 Como Adicionar Uma Nova Feature

### 1. Criar UseCase

**Arquivo:** `src/main/java/com/controlfinance/{contexto}/application/usecases/{Acao}{Entidade}UseCase.java`

```java
@Service
@RequiredArgsConstructor
public class CreateInvestmentUseCase {
  private final InvestmentRepositoryPort repository;
  private final InvestmentMapper mapper;

  public InvestmentDto execute(InvestmentDto dto) {
    Investment inv = mapper.toEntity(dto);
    Investment saved = repository.save(inv);
    return mapper.toDto(saved);
  }
}
```

### 2. Criar Entidade

**Arquivo:** `src/main/java/com/controlfinance/{contexto}/domain/entities/{Entidade}.java`

```java
@Document(collection = "investments")
@Data @NoArgsConstructor @AllArgsConstructor
public class Investment extends BaseDocument {
  private String name;
  private BigDecimal amount;
  private InvestmentType type;
}
```

### 3. Criar Repository Port

**Arquivo:** `src/main/java/com/controlfinance/{contexto}/domain/repositories/{Entidade}RepositoryPort.java`

```java
public interface InvestmentRepositoryPort {
  Investment save(Investment inv);
  Optional<Investment> findById(String id);
  List<Investment> findByUserId(String userId);
}
```

### 4. Criar Repository Adapter

**Arquivo:** `src/main/java/com/controlfinance/{contexto}/infrastructure/persistence/{Entidade}RepositoryAdapter.java`

```java
@Repository
@RequiredArgsConstructor
public class InvestmentRepositoryAdapter implements InvestmentRepositoryPort {
  private final InvestmentMongoRepository mongoRepository;
  
  @Override
  public Investment save(Investment inv) {
    return mongoRepository.save(inv);
  }
  //... implementar outros métodos
}
```

### 5. Criar DTO e Mapper

**DTO:** `{contexto}/application/dto/InvestmentDto.java`  
**Mapper:** MapStruct com `@Mapper(componentModel = "spring")`

### 6. Criar Controller

**Arquivo:** `api/rest/{Entidade}Controller.java`

```java
@RestController
@RequestMapping("/api/v1/investments")
@RequiredArgsConstructor
public class InvestmentController {
  private final CreateInvestmentUseCase create;
  
  @PostMapping
  public ResponseEntity<InvestmentDto> create(@Valid @RequestBody InvestmentDto dto) {
    return ResponseEntity.ok(create.execute(dto));
  }
}
```

### 7. Adicionar Testes

**Unit Test:** `src/test/java/com/controlfinance/{contexto}/application/usecases/{Acao}{Entidade}UseCaseTest.java`

**Integration Test:** `src/test/java/com/controlfinance/{contexto}/infrastructure/persistence/{Entidade}RepositoryAdapterIT.java`

---

## 📖 Documentação para Cada Padrão

| Necessidade | Consulte |
|---|---|
Entender a arquitetura geral | README.md - Seção "Arquitetura"
Saber onde colocar uma classe | PACKAGE_STRUCTURE.md
Boas práticas detalhadas | ARCHITECTURE.md
Detalhes da refatoração | REFACTORING_SUMMARY.md
Exemplo de novo contexto | Veja: `modules/categories/` ou `modules/transactions/`

---

## 🚫 Evitar Esses Erros

```java
// ❌ ERRADO: Controller acessando MongoDB diretamente
public class InvestmentController {
  @Autowired
  private InvestmentMongoRepository repo;  // ❌ Nunca faça isso
}

// ❌ ERRADO: Domain importando de Application
import com.controlfinance.investments.application.*;  // ❌ Nunca

// ❌ ERRADO: UseCase chamando outro UseCase
new CreateInvestmentUseCase(...).execute();  // ❌ Injete via construtor

// ❌ ERRADO: Lógica de negócio no Controller
@PostMapping
public String create(...) {
  if (amount < 0) {  // ❌ Isso deve estar em domain/
    throw new Exception(...);
  }
}
```

---

## ✅ Fazer Assim

```java
// ✅ CORRETO: Controller injeta UseCase
public class InvestmentController {
  private final CreateInvestmentUseCase create;  // ✅ Injeta
}

// ✅ CORRETO: Domain com regras isoladas
@Data
public class Investment extends BaseDocument {
  public void validateAmount() {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("Amount must be positive");
    }
  }
}

// ✅ CORRETO: UseCase chamando Services de domínio
public class CreateInvestmentUseCase {
  public InvestmentDto execute(InvestmentDto dto) {
    Investment inv = mapper.toEntity(dto);
    inv.validateAmount();  // ✅ Chamar método de domínio
    return mapper.toDto(repository.save(inv));
  }
}
```

---

## 🔍 Estrutura em Uma Linha

```
HTTP Request → Controller → UseCase → Repository Port → MongoDB ↔ Entity
               (api/rest) (application)                         (domain)
```

---

## 📞 Comandos Maven Úteis

```bash
# Apenas compilar (sem jar)
./mvnw clean compile

# Build + jar
./mvnw clean package

# Rodar testes apenas
./mvnw test

# Rodar testes e coverage
./mvnw test jacoco:report

# Rodar testes de integração apenas
./mvnw test -Dtest=*IT

# Verificar dependências não utilizadas
./mvnw dependency:analyze

# Listar todas as dependências
./mvnw dependency:tree

# Format do código
./mvnw spotless:apply
```

---

## 📂 Estrutura Mínima de Novo Contexto

```
mkdir -p src/main/java/com/controlfinance/investments/{application,domain,infrastructure}
mkdir -p src/main/java/com/controlfinance/investments/{application/usecases,application/dto}
mkdir -p src/main/java/com/controlfinance/investments/domain/{entities,repositories}
mkdir -p src/main/java/com/controlfinance/investments/infrastructure/persistence

# Criar após mkdir:
# 1. {Entidade}.java em domain/entities/
# 2. {Entidade}RepositoryPort.java em domain/repositories/
# 3. {Entidade}MongoRepository.java em infrastructure/persistence/
# 4. {Entidade}RepositoryAdapter.java em infrastructure/persistence/
# 5. {Entidade}Dto.java em application/dto/
# 6. {Entidade}Mapper.java em application/mapper/
# 7. Create{Entidade}UseCase.java em application/usecases/
# 8. {Entidade}Controller.java em ../api/rest/
```

---

## 🎓 Padrões de Nomenclatura

| Tipo | Padrão | Exemplo |
|---|---|---|
UseCase | `{Ação}{Entidade}UseCase` | `CreateInvestmentUseCase` |
Entidade | Singular, CamelCase | `Investment` |
DTO | `{Entidade}Dto` / `{Entidade}Request` | `InvestmentDto`, `InvestmentRequest` |
Mapper | `{Entidade}Mapper` | `InvestmentMapper` |
Enum | `{Entidade}{Atributo}` | `InvestmentType`, `InvestmentStatus` |
Repository Port | `{Entidade}RepositoryPort` | `InvestmentRepositoryPort` |
Repository Adapter | `{Entidade}RepositoryAdapter` | `InvestmentRepositoryAdapter` |
MongoDB Repo | `{Entidade}MongoRepository` | `InvestmentMongoRepository` |
Controller | `{Entidade}Controller` | `InvestmentController` |
Rota | `/api/v1/{recurso}` | `/api/v1/investments` |
Evento | `{Entidade}{Ação}Event` | `InvestmentCreatedEvent` |

---

## 🧪 Exemplo: Escrevendo um Test

### Unit Test (Teste o UseCase sem bd)

```java
@Test
public void shouldCreateInvestment() {
  // Arrange
  InvestmentDto input = new InvestmentDto("Apple", "100.00", STOCK);
  InvestmentDto expected = new InvestmentDto("1", "Apple", "100.00", STOCK);
  
  when(mapper.toEntity(input)).thenReturn(/* entity */);
  when(repository.save(/* entity */)).thenReturn(/* saved entity */);
  when(mapper.toDto(/* saved */)).thenReturn(expected);
  
  // Act
  InvestmentDto result = useCase.execute(input);
  
  // Assert
  assertEquals(expected.getName(), result.getName());
}
```

### Integration Test (Com MongoDB real)

```java
@SpringBootTest
@Testcontainers
class InvestmentRepositoryAdapterIT {
  @Container
  static MongoDBContainer mongo = new MongoDBContainer("mongo:7");
  
  @Test
  void shouldSaveAndFindInvestment() {
    Investment inv = new Investment("Apple", "100.00", STOCK);
    Investment saved = adapter.save(inv);
    
    Optional<Investment> found = adapter.findById(saved.getId());
    assertTrue(found.isPresent());
  }
}
```

---

## 🌟 Comandos Frequentes

```bash
# Renderizar estrutura de pacotes (Linux)
tree -I 'target|__pycache__' src/main/java/com/controlfinance

# Ver commits recentes
git log --oneline -10

# Ver mudanças não commitadas
git status

# Fazer novo commit
git add . && git commit -m "feat: ..."

# Build e testar antes de push
./mvnw clean verify && echo "✅ OK"
```

---

## ❓ Dúvidas Frequentes

**P: Posso colocar lógica de negócio no Controller?**  
R: Não. Controller apenas valida entrada, chama UseCase e retorna resposta.

**P: UseCase pode chamar outro UseCase?**  
R: Não. Use Services de domínio para compartilhar lógica.

**P: Entidade pode ter @Autowired?**  
R: Não. Mantém domínio independente de Spring.

**P: Onde pongo a validação?**  
R: Em `domain/services/` ou no próprio constructor/métodos da Entidade.

**P: Como faço multi-tenancy?**  
R: Todas as entidades herdam de `BaseDocument` que tem `userId`. Sempre filtrar por usuario

.

---

**Última atualização**: 7 de Abril de 2026
