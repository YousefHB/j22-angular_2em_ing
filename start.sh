#!/bin/bash
# Script de démarrage pour Render

echo "🚀 Starting ShopFlow Backend..."
echo "📦 Java Version:"
java -version

echo "🔧 Environment: ${SPRING_PROFILES_ACTIVE:-dev}"
echo "🌐 Port: ${PORT:-8084}"

# Démarrer l'application avec les bonnes options
exec java \
  -Xmx512m \
  -Dserver.port=${PORT:-8084} \
  -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} \
  -jar target/shopflow-0.0.1-SNAPSHOT.jar
