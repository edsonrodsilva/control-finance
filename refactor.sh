#!/bin/bash
set -e

BASE="src/main/java/com/controlfinance"

# Criar novo diretório de API
mkdir -p $BASE/api/rest

# Mover controllers para api/rest
git mv $BASE/interfaces/rest/AuthController.java $BASE/api/rest/
git mv $BASE/interfaces/rest/CategoryController.java $BASE/api/rest/
git mv $BASE/interfaces/rest/HealthController.java $BASE/api/rest/
git mv $BASE/interfaces/rest/ReportingController.java $BASE/api/rest/
git mv $BASE/interfaces/rest/TransactionController.java $BASE/api/rest/
git mv $BASE/interfaces/rest/UserController.java $BASE/api/rest/

# Remover diretório vazio
rmdir $BASE/interfaces/rest
rmdir $BASE/interfaces

# Renomear shared para common
git mv $BASE/shared $BASE/common

# Reorganizar infrastructure shared (security, events, etc)
mkdir -p $BASE/infrastructure/config
mkdir -p $BASE/infrastructure/security
mkdir -p $BASE/infrastructure/events
mkdir -p $BASE/infrastructure/mongo
mkdir -p $BASE/infrastructure/openapi

echo "✓ Estrutura de diretórios criada"
