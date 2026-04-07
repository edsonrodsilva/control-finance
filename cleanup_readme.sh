#!/bin/bash

# Remove as últimas linhas problemáticas e adiciona uma conclusão limpa
head -n 391 README.md > README_temp.md

cat >> README_temp.md << 'ENDOFREADME'
}
```

## Testes

O projeto possui testes de integração com MongoDB real usando Testcontainers.

Siga este modelo para adicionar testes de integração em novos módulos:

```bash
./mvnw test                    # Rodar todos os testes
./mvnw test -Dtest=*IT        # Apenas testes de integração (IT)
./mvnw test -Dtest=*Test      # Apenas unit tests
```

## Arquitetura e Boas Práticas

Para guias detalhados sobre como manter a organização do projeto, estrutura de pacotes, nomenclatura e padrões, consulte [ARCHITECTURE.md](ARCHITECTURE.md).

Este documento cobre:
- Fluxo de dependências entre camadas
- Estrutura de um novo contexto de negócio
- Responsabilidades de cada camada
- Verificação automática da quali dade de arquitetura

## Estado Atual

Este repositório oferece uma fundação funcional para um backend financeiro modular, bem estruturado segundo Clean Architecture e DDD.

**Componentes implementados:**
- ✅ Autenticação JWT + 2FA (TOTP)
- ✅ Gestão de Usuários
- ✅ Categorias de Transações (com subcategorias)
- ✅ Transações (CRUD + filtros avançados)
- ✅ Orçamentos e avaliação de limites
- ✅ Relatórios (Dashboard financeiro)
- ✅ Auditoria baseada em eventos
- ✅ Notificações por eventos de orçamento
- ✅ OpenAPI/Swagger UI
- ✅ Testes de integração

**Componentes planejados (structure-only):**
- 🔜 Ativos (Assets)
- 🔜 Fluxo de Caixa (Cashflow)
- 🔜 Dívidas (Debts)
- 🔜 Investimentos (Investments)
- 🔜 Projetos (Projects)

## Próximos Passos

1. **Completar módulos planejados** implementando application, domain e infrastructure
2. **Aumentar cobertura de testes** dos módulos já implementados
3. **Adicionar validações de negócio** mais complexas nas entidades
4. **Integrar com APIs externas** (bancos, serviços de cambio, etc.)
5. **Implementar cache** (Redis) para otimizações
6. **Agregar relatórios avançados** e análises financeiras

---

**Última atualização:** 7 de Abril de 2026
ENDOFREADME

mv README_temp.md README.md
echo "✓ README limpo e atualizado"
