#!/bin/bash

# ============================================
# Script para executar o script SQL no SQL Server
# ============================================

echo "🚀 Aguardando SQL Server iniciar..."

# Aguardar 30 segundos para o SQL Server estar pronto
sleep 30

echo "📄 Executando script de criação do banco de dados..."

# Executar o script SQL
docker exec -it $(docker ps -qf "ancestor=mcr.microsoft.com/mssql/server:latest") \
  /opt/mssql-tools/bin/sqlcmd \
  -S localhost \
  -U sa \
  -P verYs3cret \
  -i /docker-entrypoint-initdb.d/script-bd.sql

if [ $? -eq 0 ]; then
    echo "✅ Script executado com sucesso!"
else
    echo "❌ Erro ao executar o script."
    exit 1
fi