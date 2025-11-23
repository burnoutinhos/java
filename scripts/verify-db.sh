#!/bin/bash

# ============================================
# Script de Verificação do Banco de Dados
# Projeto: Burnoutinhos API
# ============================================

set -e

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configurações
DB_PASSWORD="verYs3cret"
DB_USER="sa"
DB_NAME="burnoutinhos_db"

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║        VERIFICAÇÃO DO BANCO DE DADOS - Burnoutinhos        ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Função para executar query SQL
execute_query() {
    docker exec burnoutinhos-sqlserver /opt/mssql-tools/bin/sqlcmd \
        -S localhost \
        -U $DB_USER \
        -P $DB_PASSWORD \
        -d $DB_NAME \
        -Q "$1" \
        -h -1 \
        -W 2>&1
}

# Verificar se container está rodando
echo -e "${CYAN}🔍 Verificando container SQL Server...${NC}"
if docker ps | grep -q "burnoutinhos-sqlserver"; then
    echo -e "${GREEN}✓ Container está rodando${NC}"
else
    echo -e "${RED}✗ Container não está rodando!${NC}"
    echo "Execute: docker-compose up -d"
    exit 1
fi

echo ""

# Verificar se banco existe
echo -e "${CYAN}🔍 Verificando banco de dados...${NC}"
DB_EXISTS=$(docker exec burnoutinhos-sqlserver /opt/mssql-tools/bin/sqlcmd \
    -S localhost \
    -U $DB_USER \
    -P $DB_PASSWORD \
    -Q "SELECT COUNT(*) FROM sys.databases WHERE name = '$DB_NAME'" \
    -h -1 \
    -W 2>&1 | tr -d '[:space:]')

if [ "$DB_EXISTS" = "1" ]; then
    echo -e "${GREEN}✓ Banco '$DB_NAME' existe${NC}"
else
    echo -e "${RED}✗ Banco '$DB_NAME' não encontrado!${NC}"
    exit 1
fi

echo ""

# Verificar tabelas
echo -e "${CYAN}🔍 Verificando tabelas criadas...${NC}"

EXPECTED_TABLES=(
    "app_user"
    "app_user_roles"
    "todo"
    "notification"
    "suggestion"
    "time_block"
    "t_gp_mottu_token_push"
)

TABLE_COUNT=0
MISSING_TABLES=()

for table in "${EXPECTED_TABLES[@]}"; do
    EXISTS=$(execute_query "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '$table'" | tr -d '[:space:]')
    
    if [ "$EXISTS" = "1" ]; then
        echo -e "${GREEN}  ✓ $table${NC}"
        TABLE_COUNT=$((TABLE_COUNT + 1))
    else
        echo -e "${RED}  ✗ $table${NC}"
        MISSING_TABLES+=("$table")
    fi
done

echo ""
echo -e "${CYAN}📊 Resumo: $TABLE_COUNT/${#EXPECTED_TABLES[@]} tabelas encontradas${NC}"

if [ ${#MISSING_TABLES[@]} -gt 0 ]; then
    echo -e "${YELLOW}⚠️  Tabelas faltando: ${MISSING_TABLES[*]}${NC}"
    echo "Execute o script de inicialização: ./scripts/init-db.sh"
    exit 1
fi

echo ""

# Contar registros em cada tabela
echo -e "${CYAN}🔍 Contando registros...${NC}"

for table in "${EXPECTED_TABLES[@]}"; do
    COUNT=$(execute_query "SELECT COUNT(*) FROM $table" | tr -d '[:space:]' | grep -o '[0-9]*' | head -1)
    echo -e "${BLUE}  📋 $table: $COUNT registro(s)${NC}"
done

echo ""

# Verificar usuário admin
echo -e "${CYAN}🔍 Verificando usuário admin...${NC}"
ADMIN_EXISTS=$(execute_query "SELECT COUNT(*) FROM app_user WHERE email = 'admin@burnoutinhos.com'" | tr -d '[:space:]')

if [ "$ADMIN_EXISTS" = "1" ]; then
    echo -e "${GREEN}✓ Usuário admin existe${NC}"
    ADMIN_ROLES=$(execute_query "SELECT COUNT(*) FROM app_user_roles ar JOIN app_user u ON ar.app_user_id = u.id WHERE u.email = 'admin@burnoutinhos.com' AND ar.roles LIKE '%ADMIN%'" | tr -d '[:space:]')
    
    if [ "$ADMIN_ROLES" -ge "1" ]; then
        echo -e "${GREEN}✓ Usuário admin tem permissões de administrador${NC}"
    else
        echo -e "${YELLOW}⚠️  Usuário admin não tem role ADMIN${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Usuário admin não encontrado${NC}"
fi

echo ""

# Verificar índices importantes
echo -e "${CYAN}🔍 Verificando índices...${NC}"

INDEXES=(
    "idx_todo_user_id"
    "idx_todo_is_completed"
    "idx_notification_user_id"
    "idx_time_block_user_id"
)

INDEX_COUNT=0

for index in "${INDEXES[@]}"; do
    EXISTS=$(execute_query "SELECT COUNT(*) FROM sys.indexes WHERE name = '$index'" | tr -d '[:space:]')
    
    if [ "$EXISTS" -ge "1" ]; then
        echo -e "${GREEN}  ✓ $index${NC}"
        INDEX_COUNT=$((INDEX_COUNT + 1))
    else
        echo -e "${YELLOW}  ⚠️  $index não encontrado${NC}"
    fi
done

echo ""

# Testar conexão
echo -e "${CYAN}🔍 Testando conectividade...${NC}"
CONNECTIVITY=$(docker exec burnoutinhos-sqlserver /opt/mssql-tools/bin/sqlcmd \
    -S localhost \
    -U $DB_USER \
    -P $DB_PASSWORD \
    -Q "SELECT @@VERSION" \
    -h -1 2>&1)

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Conexão ao banco de dados está funcionando${NC}"
else
    echo -e "${RED}✗ Erro ao conectar ao banco de dados${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"

if [ ${#MISSING_TABLES[@]} -eq 0 ] && [ "$TABLE_COUNT" -eq "${#EXPECTED_TABLES[@]}" ]; then
    echo -e "${GREEN}║              ✅ BANCO DE DADOS OK!                         ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${GREEN}🎉 Todas as verificações passaram com sucesso!${NC}"
    echo ""
    echo -e "${CYAN}📊 Informações de Conexão:${NC}"
    echo -e "   Host:     localhost"
    echo -e "   Port:     1433"
    echo -e "   Database: $DB_NAME"
    echo -e "   User:     $DB_USER"
    echo ""
    echo -e "${CYAN}👤 Usuário Admin:${NC}"
    echo -e "   Email:    admin@burnoutinhos.com"
    echo -e "   Password: admin123"
    echo ""
    exit 0
else
    echo -e "${YELLOW}║           ⚠️  VERIFICAÇÃO INCOMPLETA                       ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${YELLOW}⚠️  Algumas verificações falharam.${NC}"
    echo "Execute o script de inicialização: ./scripts/init-db.sh"
    echo ""
    exit 1
fi