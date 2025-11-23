#!/bin/bash

# ============================================
# Script de Inicialização do Banco de Dados
# Projeto: Burnoutinhos API
# ============================================

set -e

echo "🚀 Iniciando configuração do banco de dados..."

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configurações
DB_PASSWORD="verYs3cret"
DB_USER="sa"
MAX_RETRIES=30
RETRY_INTERVAL=2

# Função para verificar se o SQL Server está pronto
check_sqlserver() {
    docker exec $(docker ps -qf "ancestor=mcr.microsoft.com/mssql/server:latest") \
        /opt/mssql-tools/bin/sqlcmd \
        -S localhost \
        -U $DB_USER \
        -P $DB_PASSWORD \
        -Q "SELECT 1" > /dev/null 2>&1
    return $?
}

# Função para executar o script SQL
execute_sql_script() {
    echo -e "${BLUE}📄 Executando script de criação do banco de dados...${NC}"
    
    docker exec $(docker ps -qf "ancestor=mcr.microsoft.com/mssql/server:latest") \
        /opt/mssql-tools/bin/sqlcmd \
        -S localhost \
        -U $DB_USER \
        -P $DB_PASSWORD \
        -i /docker-entrypoint-initdb.d/script-bd.sql
    
    return $?
}

# Verificar se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker não está rodando!${NC}"
    echo "Por favor, inicie o Docker e tente novamente."
    exit 1
fi

# Verificar se o container SQL Server está rodando
CONTAINER_ID=$(docker ps -qf "ancestor=mcr.microsoft.com/mssql/server:latest")

if [ -z "$CONTAINER_ID" ]; then
    echo -e "${RED}❌ Container SQL Server não está rodando!${NC}"
    echo "Execute 'docker-compose up -d' primeiro."
    exit 1
fi

echo -e "${GREEN}✓ Container SQL Server encontrado: $CONTAINER_ID${NC}"

# Aguardar SQL Server estar pronto
echo -e "${YELLOW}⏳ Aguardando SQL Server estar pronto...${NC}"

RETRY_COUNT=0
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if check_sqlserver; then
        echo -e "${GREEN}✓ SQL Server está pronto!${NC}"
        break
    fi
    
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo -e "${YELLOW}   Tentativa $RETRY_COUNT de $MAX_RETRIES...${NC}"
    sleep $RETRY_INTERVAL
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo -e "${RED}❌ Timeout: SQL Server não ficou pronto após $MAX_RETRIES tentativas.${NC}"
    echo "Verifique os logs: docker logs $CONTAINER_ID"
    exit 1
fi

# Executar o script SQL
if execute_sql_script; then
    echo ""
    echo -e "${GREEN}╔════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║  ✅ Banco de dados criado com sucesso! ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${BLUE}📊 Informações de conexão:${NC}"
    echo -e "   Host:     localhost"
    echo -e "   Port:     1433"
    echo -e "   Database: burnoutinhos_db"
    echo -e "   User:     sa"
    echo -e "   Password: $DB_PASSWORD"
    echo ""
    echo -e "${BLUE}👤 Usuário Admin Padrão:${NC}"
    echo -e "   Email:    admin@burnoutinhos.com"
    echo -e "   Password: admin123"
    echo -e "   ${YELLOW}⚠️  Altere a senha após o primeiro acesso!${NC}"
    echo ""
    echo -e "${GREEN}🎉 Pronto para usar!${NC}"
    exit 0
else
    echo ""
    echo -e "${RED}╔════════════════════════════════════════╗${NC}"
    echo -e "${RED}║  ❌ Erro ao executar o script SQL      ║${NC}"
    echo -e "${RED}╚════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${YELLOW}💡 Dicas para resolver:${NC}"
    echo "   1. Verifique os logs: docker logs $CONTAINER_ID"
    echo "   2. Verifique se o arquivo script-bd.sql existe"
    echo "   3. Tente executar manualmente o script"
    echo ""
    exit 1
fi