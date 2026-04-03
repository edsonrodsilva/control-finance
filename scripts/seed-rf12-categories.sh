#!/usr/bin/env bash
set -euo pipefail

# Seed de categorias e subcategorias para o requisito RF12.
# Uso:
#   USER_ID=user-123 ./scripts/seed-rf12-categories.sh
# Variaveis opcionais:
#   MONGODB_URI (default: mongodb://localhost:27017)
#   DB_NAME     (default: control_finance)
#
# Fallback:
# - Se mongosh nao estiver no host, tenta executar via container mongo do docker compose.

MONGODB_URI="${MONGODB_URI:-mongodb://localhost:27017}"
DB_NAME="${DB_NAME:-control_finance}"
USER_ID="${USER_ID:-69cda31a781512693650973f}"

resolve_compose_cmd() {
  if command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
    echo "docker compose"
    return 0
  fi

  if command -v docker-compose >/dev/null 2>&1; then
    echo "docker-compose"
    return 0
  fi

  return 1
}

run_mongosh() {
  local js="$1"

  if command -v mongosh >/dev/null 2>&1; then
    mongosh "${MONGODB_URI}/${DB_NAME}" --quiet --eval "${js}"
    return 0
  fi

  local compose_cmd
  if ! compose_cmd="$(resolve_compose_cmd)"; then
    echo "Erro: mongosh nao encontrado no PATH e Docker Compose indisponivel." >&2
    echo "Instale mongosh ou inicie o stack com docker compose." >&2
    return 1
  fi

  # Garante que o servico mongo exista e esteja ativo antes do exec.
  if [[ -z "$(${compose_cmd} ps -q mongo)" ]]; then
    ${compose_cmd} up -d mongo >/dev/null
  fi

  ${compose_cmd} exec -T mongo mongosh "mongodb://localhost:27017/${DB_NAME}" --quiet --eval "${js}"
}

escaped_user_id="${USER_ID//\\/\\\\}"
escaped_user_id="${escaped_user_id//\'/\\\'}"

seed_js="$(cat <<'JS'
const userId = '__USER_ID__';
const now = new Date();

const catalog = [
  { name: 'Alimentacao', type: 'expense', subs: ['Supermercado', 'Restaurante', 'Padaria', 'Delivery', 'Cafe'] },
  { name: 'Saude', type: 'expense', subs: ['Farmacia', 'Consultas', 'Exames', 'Plano de saude', 'Odontologia'] },
  { name: 'Moradia', type: 'expense', subs: ['Aluguel', 'Condominio', 'Luz', 'Agua', 'Gas', 'Internet'] },
  { name: 'Transporte', type: 'expense', subs: ['Combustivel', 'Transporte publico', 'App de transporte', 'Estacionamento', 'Pedagio'] },
  { name: 'Educacao', type: 'expense', subs: ['Mensalidade', 'Cursos', 'Livros', 'Material escolar'] },
  { name: 'Lazer', type: 'expense', subs: ['Cinema', 'Streaming', 'Viagens', 'Eventos', 'Hobbies'] },
  { name: 'Assinaturas', type: 'expense', subs: ['Software', 'Streaming', 'Servicos online'] },
  { name: 'Impostos e taxas', type: 'expense', subs: ['IPTU', 'IPVA', 'Taxas bancarias', 'Multas'] },
  { name: 'Seguros', type: 'expense', subs: ['Seguro auto', 'Seguro residencial', 'Seguro vida'] },
  { name: 'Renda', type: 'income', subs: ['Salario', 'Freelance', 'Pro-labore', '13 salario', 'Bonus'] },
  { name: 'Investimentos', type: 'income', subs: ['Dividendos', 'Juros', 'Renda fixa', 'Venda de ativos'] },
  { name: 'Outros', type: 'both', subs: ['Transferencia', 'Ajuste', 'Presente', 'Doacao'] }
];

function upsertCategory(cat) {
  const categoryDoc = db.categories.findOneAndUpdate(
    { userId, name: cat.name },
    { $set: { name: cat.name, type: cat.type, userId, updatedAt: now }, $setOnInsert: { createdAt: now } },
    { upsert: true, returnDocument: 'after' }
  );

  cat.subs.forEach((subName) => {
    db.subcategories.findOneAndUpdate(
      { userId, categoryId: categoryDoc._id.toString(), name: subName },
      {
        $set: {
          userId,
          categoryId: categoryDoc._id.toString(),
          name: subName,
          updatedAt: now
        },
        $setOnInsert: { createdAt: now }
      },
      { upsert: true }
    );
  });
}

catalog.forEach(upsertCategory);

const totalCategories = db.categories.countDocuments({ userId });
const totalSubcategories = db.subcategories.countDocuments({ userId });

print('Seed RF12 concluido.');
print('userId=' + userId);
print('categories=' + totalCategories);
print('subcategories=' + totalSubcategories);
JS
)"

seed_js="${seed_js/__USER_ID__/${escaped_user_id}}"

run_mongosh "${seed_js}"