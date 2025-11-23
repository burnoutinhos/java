#!/bin/bash

# ============================================
# Script para Aplicar Schema SQL no Azure
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

echo -e "${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║        Aplicar Schema SQL no Azure SQL Database               ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

# ============================
# VARIÁVEIS (mesmas do script-infra.sh)
# ============================
SERVER_NAME="sql-server-burnoutinhos-eastus2"
DB_USERNAME="burnoutinhos-admin"
DB_NAME="burnoutinhos-db"
DB_PASSWORD="${DB_PASSWORD:-Bur@N0utinhos!#}"

# Obter diretório do script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_SCRIPT_PATH="$SCRIPT_DIR/script-bd.sql"

# ============================
# VALIDAÇÕES
# ============================

echo -e "${CYAN}🔍 Verificando requisitos...${NC}"

# Verificar se o arquivo SQL existe
if [ ! -f "$SQL_SCRIPT_PATH" ]; then
    echo -e "${RED}❌ Arquivo script-bd.sql não encontrado!${NC}"
    echo "Esperado em: $SQL_SCRIPT_PATH"
    exit 1
fi

echo -e "${GREEN}✓ Arquivo SQL encontrado${NC}"

# Verificar se sqlcmd está instalado
if ! command -v sqlcmd &> /dev/null; then
    echo -e "${RED}❌ sqlcmd não está instalado!${NC}"
    echo ""
    echo -e "${YELLOW}Para instalar no Ubuntu/Debian:${NC}"
    echo "  curl https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -"
    echo "  curl https://packages.microsoft.com/config/ubuntu/\$(lsb_release -rs)/prod.list | sudo tee /etc/apt/sources.list.d/mssql-release.list"
    echo "  sudo apt-get update"
    echo "  sudo ACCEPT_EULA=Y apt-get install -y mssql-tools unixodbc-dev"
    echo "  echo 'export PATH=\"\$PATH:/opt/mssql-tools/bin\"' >> ~/.bashrc"
    echo "  source ~/.bashrc"
    echo ""
    echo -e "${YELLOW}Para instalar no macOS:${NC}"
    echo "  brew tap microsoft/mssql-release https://github.com/Microsoft/homebrew-mssql-release"
    echo "  brew update"
    echo "  HOMEBREW_NO_ENV_FILTERING=1 ACCEPT_EULA=Y brew install mssql-tools"
    echo ""
    exit 1
fi

echo -e "${GREEN}✓ sqlcmd instalado${NC}"

# ============================
# INFORMAÇÕES DE CONEXÃO
# ============================
echo ""
echo -e "${CYAN}📊 Informações de Conexão:${NC}"
echo "  Server:   $SERVER_NAME.database.windows.net"
echo "  Database: $DB_NAME"
echo "  User:     $DB_USERNAME"
echo ""

# ============================
# TESTAR CONEXÃO
# ============================
echo -e "${CYAN}🔌 Testando conexão com o banco de dados...${NC}"

if sqlcmd -S "$SERVER_NAME.database.windows.net" \
         -U "$DB_USERNAME" \
         -P "$DB_PASSWORD" \
         -d "$DB_NAME" \
         -Q "SELECT 1" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Conexão estabelecida com sucesso${NC}"
else
    echo -e "${RED}❌ Falha ao conectar ao banco de dados!${NC}"
    echo ""
    echo -e "${YELLOW}Possíveis causas:${NC}"
    echo "  1. Credenciais incorretas"
    echo "  2. Firewall bloqueando sua conexão"
    echo "  3. Servidor não existe ou está offline"
    echo ""
    echo -e "${YELLOW}Para liberar seu IP no firewall:${NC}"
    echo "  az sql server firewall-rule create \\"
    echo "    --resource-group rg-sql-burnoutinhos \\"
    echo "    --server $SERVER_NAME \\"
    echo "    --name AllowMyIP \\"
    echo "    --start-ip-address \$(curl -s https://api.ipify.org) \\"
    echo "    --end-ip-address \$(curl -s https://api.ipify.org)"
    echo ""
    exit 1
fi

# ============================
# EXECUTAR SCRIPT SQL
# ============================
echo ""
echo -e "${CYAN}📄 Executando script SQL...${NC}"
echo "  Arquivo: $SQL_SCRIPT_PATH"
echo ""

# Executar com output detalhado
sqlcmd -S "$SERVER_NAME.database.windows.net" \
       -U "$DB_USERNAME" \
       -P "$DB_PASSWORD" \
       -d "$DB_NAME" \
       -i "$SQL_SCRIPT_PATH" \
       -b

EXIT_CODE=$?

echo ""

if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              ✅ SCHEMA APLICADO COM SUCESSO!                   ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${CYAN}🎉 Tabelas criadas/verificadas no banco $DB_NAME${NC}"
    echo ""
    
    # Verificar tabelas criadas
    echo -e "${CYAN}📊 Verificando tabelas criadas...${NC}"
    sqlcmd -S "$SERVER_NAME.database.windows.net" \
           -U "$DB_USERNAME" \
           -P "$DB_PASSWORD" \
           -d "$DB_NAME" \
           -Q "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_NAME" \
           -h -1
    
    echo ""
    echo -e "${GREEN}✨ Pronto para uso!${NC}"
    exit 0
else
    echo -e "${RED}╔════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}║              ❌ ERRO AO EXECUTAR SCRIPT SQL                    ║${NC}"
    echo -e "${RED}╚════════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${YELLOW}💡 Dicas para resolver:${NC}"
    echo "  1. Verifique os logs acima para detalhes do erro"
    echo "  2. Verifique se o banco de dados existe"
    echo "  3. Verifique as permissões do usuário"
    echo "  4. Tente executar o script manualmente:"
    echo "     sqlcmd -S $SERVER_NAME.database.windows.net -U $DB_USERNAME -P '[senha]' -d $DB_NAME -i $SQL_SCRIPT_PATH"
    echo ""
    exit 1
fi