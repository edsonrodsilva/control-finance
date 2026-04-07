#!/bin/bash
set -e

BASE="src/main/java/com/controlfinance/infrastructure"

# Mover arquivos de security
if [ -d "$BASE/security" ]; then
  git mv $BASE/security/*.java $BASE/security/ 2>/dev/null || true
fi

# Mover arquivos de events
if [ -d "$BASE/events" ]; then
  git mv $BASE/events/*.java $BASE/events/ 2>/dev/null || true
fi

# Mover arquivos de mongo
if [ -d "$BASE/mongo" ]; then
  git mv $BASE/mongo/*.java $BASE/mongo/ 2>/dev/null || true
fi

# Mover arquivos de openapi
if [ -d "$BASE/openapi" ]; then
  git mv $BASE/openapi/*.java $BASE/openapi/ 2>/dev/null || true
fi

# Criar diretório config e mover a classe de configuração principal
mkdir -p $BASE/config

# Listar arquivos em infrastructure para ver o que precisa ser movido
echo "Arquivos em infrastructure:"
find $BASE -maxdepth 1 -name "*.java" | head -20

echo "✓ Infraestrutura reorganizada"
