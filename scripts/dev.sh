#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
# Use the same database credentials as Docker Compose.
if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi

case "${1:-help}" in
  db)
    docker compose up -d --wait postgres
    # Existing volumes may predate inquiry_db. Create missing databases without
    # deleting data, then let each service run its own Flyway migrations.
    docker compose exec -T postgres sh -c 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d postgres' <<'SQL'
SELECT format('CREATE DATABASE %I', name)
FROM (VALUES ('auth_db'), ('property_db'), ('inquiry_db')) AS databases(name)
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = name)
\gexec
SQL
    ;;
  auth|property|inquiry|gateway)
    # macOS: IntelliJ's bundled tools also work when CLI tools aren't on PATH.
    idea_home='/Applications/IntelliJ IDEA.app/Contents'
    if ! command -v mvn >/dev/null && [[ -x "$idea_home/plugins/maven-plugin/lib/maven3/bin/mvn" ]]; then
      export PATH="$idea_home/plugins/maven-plugin/lib/maven3/bin:$PATH"
    fi
    if [[ -z "${JAVA_HOME:-}" ]] && ! java -version >/dev/null 2>&1 && [[ -x "$idea_home/jbr/Contents/Home/bin/java" ]]; then
      export JAVA_HOME="$idea_home/jbr/Contents/Home"
    fi
    command -v mvn >/dev/null || { echo 'Java 21 ve Maven 3.9+ gerekli. Maven PATH üzerinde olmalı.' >&2; exit 1; }
    service="$1-service"
    [[ "$1" != gateway ]] || service=api-gateway
    export JWT_KEYS_DIR="${JWT_KEYS_DIR:-$ROOT/.local/keys}"
    export JWT_PUBLIC_KEY_PATH="${JWT_PUBLIC_KEY_PATH:-$JWT_KEYS_DIR/public.pem}"
    export APP_MEDIA_DIR="${APP_MEDIA_DIR:-$ROOT/.local/media}"
    if [[ "$1" == gateway && ! -f "$JWT_PUBLIC_KEY_PATH" ]]; then
      echo 'Önce auth servisini başlat: bash scripts/dev.sh auth' >&2
      exit 1
    fi
    cd "$ROOT/$service"
    exec mvn --no-transfer-progress spring-boot:run -Dspring-boot.run.profiles=local
    ;;
  frontend)
    command -v npm >/dev/null || { echo 'Node.js 22 ve npm gerekli.' >&2; exit 1; }
    # Root .env may contain the Docker-only api-gateway hostname.
    export API_INTERNAL_URL="${LOCAL_API_URL:-http://localhost:8080}"
    export NEXT_PUBLIC_SITE_URL="${LOCAL_SITE_URL:-http://localhost:3000}"
    cd "$ROOT/frontend"
    [[ -d node_modules ]] || npm install
    exec npm run dev
    ;;
  *)
    echo 'Kullanım: bash scripts/dev.sh {db|auth|property|inquiry|gateway|frontend}'
    echo 'Önce db, sonra her servisi ayrı terminalde çalıştır. Gateway auth sonrasında başlatılmalı.'
    [[ "${1:-help}" == help ]]
    ;;
esac
