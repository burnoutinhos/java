#!/bin/bash
set -e  # para parar caso dê erro

# ============================
# VARIÁVEIS
# ============================
RESOURCE_GROUP_NAME="rg-burnoutinhos"
WEBAPP_NAME="burnoutinhos-api"
APP_SERVICE_PLAN="burnoutinhos-api-plan"
LOCATION="brazilsouth"
RUNTIME="JAVA:21-java21"

RG_DB_NAME="rg-sql-burnoutinhos"
DB_USERNAME="burnoutinhos-admin"
DB_NAME="burnoutinhos-db"
DB_PASSWORD="Bur@N0utinhos!#" # Senha definida pelo usuário
SERVER_NAME="sql-server-burnoutinhos-eastus2"

GITHUB_REPO_NAME="burnoutinhos/burnoutinhos"
BRANCH="main"
APP_INSIGHTS_NAME="ai-burnoutinhos-api"

# Variáveis do Event Hubs
EVENTHUBS_NAMESPACE="evh-ns-burnoutinhos"
EVENTHUB_NAME="burnoutinhos-events"
EVENTHUBS_SKU="Basic" # Basic, Standard, Premium
EVENTHUBS_RG_NAME=$RESOURCE_GROUP_NAME # Usaremos o mesmo RG do App Service

# ============================
# PROVIDERS E EXTENSÕES (Geralmente seguros para rodar sempre)
# ============================
az provider register --namespace Microsoft.Web
az provider register --namespace Microsoft.Insights
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.ServiceLinker
az provider register --namespace Microsoft.Sql
az provider register --namespace Microsoft.EventHub

az extension add --name application-insights || true

# ============================
# GRUPOS DE RECURSOS
# ============================

# Verificar e criar RG do banco de dados
if [ -n "$(az group show --name $RG_DB_NAME --query name -o tsv 2>/dev/null)" ]; then
  echo "Grupo de recursos $RG_DB_NAME já existe. Pulando a criação."
else
  echo "Criando grupo de recursos $RG_DB_NAME..."
  az group create --name $RG_DB_NAME --location eastus2
fi

# Verificar e criar RG do App/EventHubs
if [ -n "$(az group show --name $RESOURCE_GROUP_NAME --query name -o tsv 2>/dev/null)" ]; then
  echo "Grupo de recursos $RESOURCE_GROUP_NAME já existe. Pulando a criação."
else
  echo "Criando grupo de recursos $RESOURCE_GROUP_NAME..."
  az group create --name $RESOURCE_GROUP_NAME --location "$LOCATION"
fi


# ============================
# BANCO DE DADOS SQL
# ============================

# Verificar e criar SQL Server
if [ -n "$(az sql server show --name $SERVER_NAME --resource-group $RG_DB_NAME --query name -o tsv 2>/dev/null)" ]; then
  echo "SQL Server $SERVER_NAME já existe. Pulando a criação."
else
  echo "Criando SQL Server $SERVER_NAME..."
  az sql server create \
    --name $SERVER_NAME \
    --resource-group $RG_DB_NAME \
    --location eastus2 \
    --admin-user $DB_USERNAME \
    --admin-password $DB_PASSWORD \
    --enable-public-network true
fi

# Verificar e criar SQL DB
if [ -n "$(az sql db show --name $DB_NAME --server $SERVER_NAME --resource-group $RG_DB_NAME --query name -o tsv 2>/dev/null)" ]; then
    echo "Banco de dados $DB_NAME já existe. Pulando a criação."
else
    echo "Criando banco de dados $DB_NAME..."
    az sql db create \
      --resource-group $RG_DB_NAME \
      --server $SERVER_NAME \
      --name $DB_NAME \
      --service-objective Basic \
      --backup-storage-redundancy Local \
      --zone-redundant false
fi

# Liberar firewall (apenas testes!) - Este comando é idempotente para a mesma regra
az sql server firewall-rule create \
  --resource-group $RG_DB_NAME \
  --server $SERVER_NAME \
  --name liberaGeral \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 255.255.255.255 || echo "Regra de firewall já existe ou falhou ao criar."

# ============================
# APPLICATION INSIGHTS
# ============================

# Verificar e criar App Insights
if [ -n "$(az monitor app-insights component show --app $APP_INSIGHTS_NAME --resource-group $RESOURCE_GROUP_NAME --query name -o tsv 2>/dev/null)" ]; then
    echo "Application Insights $APP_INSIGHTS_NAME já existe. Pulando a criação."
else
    echo "Criando Application Insights $APP_INSIGHTS_NAME..."
    az monitor app-insights component create \
      --app $APP_INSIGHTS_NAME \
      --location "$LOCATION" \
      --resource-group $RESOURCE_GROUP_NAME \
      --application-type web
fi

CONNECTION_STRING=$(az monitor app-insights component show \
  --app $APP_INSIGHTS_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --query connectionString \
  --output tsv)

# ============================
# AZURE EVENT HUBS
# ============================
# ============================
# AZURE EVENT HUBS (FREE TIER)
# ============================

# Free Tier só funciona no primeiro namespace Standard da conta
EVENTHUBS_SKU="Standard"

# Verificar e criar Event Hubs Namespace (FREE TIER)
if [ -n "$(az eventhubs namespace show --name $EVENTHUBS_NAMESPACE --resource-group $EVENTHUBS_RG_NAME --query name -o tsv 2>/dev/null)" ]; then
    echo "Event Hubs Namespace $EVENTHUBS_NAMESPACE já existe. Pulando a criação."
else
    echo "Criando Event Hubs Namespace (FREE TIER) $EVENTHUBS_NAMESPACE..."
    az eventhubs namespace create \
        --name $EVENTHUBS_NAMESPACE \
        --resource-group $EVENTHUBS_RG_NAME \
        --location "$LOCATION" \
        --sku Standard \
        --capacity 1 \
        --enable-auto-inflate false \
        --tags free-tier=true
fi

# Verificar e criar Event Hub (Tópico)
if [ -n "$(az eventhubs eventhub show --name $EVENTHUB_NAME --namespace-name $EVENTHUBS_NAMESPACE --resource-group $EVENTHUBS_RG_NAME --query name -o tsv 2>/dev/null)" ]; then
    echo "Event Hub $EVENTHUB_NAME já existe. Pulando a criação."
else
    az eventhubs eventhub create --resource-group $EVENTHUBS_RG_NAME --namespace-name $EVENTHUBS_NAMESPACE --name $EVENTHUB_NAME --partition-count 1
fi


# Obter a Connection String primária para o App Service
EVENTHUBS_CS=$(az eventhubs namespace authorization-rule keys list \
    --resource-group $EVENTHUBS_RG_NAME \
    --namespace-name $EVENTHUBS_NAMESPACE \
    --name RootManageSharedAccessKey \
    --query primaryConnectionString \
    --output tsv)



# ============================
# APP SERVICE PLAN + WEBAPP
# ============================

# Verificar e criar App Service Plan
if [ -n "$(az appservice plan show --name $APP_SERVICE_PLAN --resource-group $RESOURCE_GROUP_NAME --query name -o tsv 2>/dev/null)" ]; then
    echo "App Service Plan $APP_SERVICE_PLAN já existe. Pulando a criação."
else
    echo "Criando App Service Plan $APP_SERVICE_PLAN..."
    az appservice plan create \
      --name $APP_SERVICE_PLAN \
      --resource-group $RESOURCE_GROUP_NAME \
      --location "$LOCATION" \
      --sku F1 \
      --is-linux
fi


# Verificar e criar Web App
if [ -n "$(az webapp show --name $WEBAPP_NAME --resource-group $RESOURCE_GROUP_NAME --query name -o tsv 2>/dev/null)" ]; then
    echo "Web App $WEBAPP_NAME já existe. Pulando a criação."
else
    echo "Criando Web App $WEBAPP_NAME..."
    az webapp create \
      --name $WEBAPP_NAME \
      --resource-group $RESOURCE_GROUP_NAME \
      --plan $APP_SERVICE_PLAN \
      --runtime "$RUNTIME"
fi


# Habilitar autenticação SCM (idempotente)
az resource update \
  --resource-group $RESOURCE_GROUP_NAME \
  --namespace Microsoft.Web \
  --resource-type basicPublishingCredentialsPolicies \
  --name scm \
  --parent sites/$WEBAPP_NAME \
  --set properties.allow=true

# ============================
# CONFIGURAR VARIÁVEIS DO APP (Idempotente - sempre sobrescreve)
# ============================
echo "Configurando variáveis de ambiente do Web App, incluindo Event Hubs..."
SPRING_DATASOURCE_URL="jdbc:sqlserver://$SERVER_NAME.database.windows.net:1433;database=$DB_NAME"

# Variáveis para o Event Hubs (usadas pela dependência Spring Cloud Azure)
az webapp config appsettings set \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --settings \
    APPLICATIONINSIGHTS_CONNECTION_STRING="$CONNECTION_STRING" \
    ApplicationInsightsAgent_EXTENSION_VERSION="~3" \
    XDT_MicrosoftApplicationInsights_Mode="Recommended" \
    XDT_MicrosoftApplicationInsights_PreemptSdk="1" \
    SPRING_DATASOURCE_USERNAME="$DB_USERNAME" \
    SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD" \
    SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
    AZURE_EVENTHUBS_CONNECTION_STRING="$EVENTHUBS_CS" \
    AZURE_EVENTHUBS_EVENTHUB_NAME="$EVENTHUB_NAME"

# Reiniciar o Web App (idempotente/seguro)
az webapp restart --name $WEBAPP_NAME --resource-group $RESOURCE_GROUP_NAME

# Conectar App ao Application Insights (idempotente)
az monitor app-insights component connect-webapp \
    --app $APP_INSIGHTS_NAME \
    --web-app $WEBAPP_NAME \
    --resource-group $RESOURCE_GROUP_NAME

echo "✅ Script de configuração/verificação concluído com sucesso!"
