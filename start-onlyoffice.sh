#!/bin/bash

# Остановить и очистить старые контейнеры
echo "Остановка старых контейнеров..."
docker-compose down -v
docker rm -f onlyoffice 2>/dev/null || true

# Очистить ненужные volumes
docker volume prune -f

# Запустить новые контейнеры
echo "Запуск контейнеров..."
docker-compose up -d

echo "Ожидание запуска OnlyOffice (60 секунд)..."
sleep 60

# Проверить логи
echo "=== Логи OnlyOffice ==="
docker-compose logs --tail=50 onlyoffice

echo ""
echo "=== Проверка здоровья ==="
# Проверить с хоста
curl -f http://localhost:8083/healthcheck || echo "Ошибка healthcheck"

# Проверить изнутри сети Docker
echo ""
echo "=== Проверка из сети Docker ==="
docker run --rm --network=docker-compose_portal_net curlimages/curl \
  curl -s http://onlyoffice/healthcheck

# Проверить API
echo ""
echo "=== Проверка API ==="
curl -s http://localhost:8083/web-apps/apps/api/documents/api.js | head -5

echo ""
echo "=== Готово ==="
echo "Portal: http://localhost:8082"
echo "OnlyOffice: http://localhost:8083"
echo "OnlyOffice в сети Docker: http://onlyoffice"