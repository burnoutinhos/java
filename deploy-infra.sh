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
DB_PASSWORD="Bur@N0utinhos!#"
SERVER_NAME="sql-server-burnoutinhos-eastus2"

GITHUB_REPO_NAME="burnoutinhos/burnoutinhos"
BRANCH="main"
APP_INSIGHTS_NAME="ai-burnoutinhos-api"

# ============================
# PROVIDERS E EXTENSÕES (Geralmente seguros para rodar sempre)
# ============================
az provider register --namespace Microsoft.Web
az provider register --namespace Microsoft.Insights
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.ServiceLinker
az provider register --namespace Microsoft.Sql

az extension add --name application-insights || true

# ============================
# GRUPOS DE RECURSOS
# ============================

# Verificar e criar RG do banco de dados
if [ $(az group show --name $RG_DB_NAME --query name -o tsv) ]; then
  echo "Grupo de recursos $RG_DB_NAME já existe. Pulando a criação."
else
  echo "Criando grupo de recursos $RG_DB_NAME..."
  az group create --name $RG_DB_NAME --location eastus2
fi

# Verificar e criar RG do App
if [ $(az group show --name $RESOURCE_GROUP_NAME --query name -o tsv) ]; then
  echo "Grupo de recursos $RESOURCE_GROUP_NAME já existe. Pulando a criação."
else
  echo "Criando grupo de recursos $RESOURCE_GROUP_NAME..."
  az group create --name $RESOURCE_GROUP_NAME --location "$LOCATION"
fi


# ============================
# BANCO DE DADOS SQL
# ============================

# Verificar e criar SQL Server
if [ $(az sql server show --name $SERVER_NAME --resource-group $RG_DB_NAME --query name -o tsv) ]; then
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

# Verificar e criar SQL DB (Este comando é idempotente, então pode ser rodado sempre ou verificado)
# Vamos verificar para seguir o padrão
if [ $(az sql db show --name $DB_NAME --server $SERVER_NAME --resource-group $RG_DB_NAME --query name -o tsv) ]; then
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
if [ $(az monitor app-insights component show --app $APP_INSIGHTS_NAME --resource-group $RESOURCE_GROUP_NAME --query name -o tsv) ]; then
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
# APP SERVICE PLAN + WEBAPP
# ============================

# Verificar e criar App Service Plan
if [ $(az appservice plan show --name $APP_SERVICE_PLAN --resource-group $RESOURCE_GROUP_NAME --query name -o tsv) ]; then
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
if [ $(az webapp show --name $WEBAPP_NAME --resource-group $RESOURCE_GROUP_NAME --query name -o tsv) ]; then
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
echo "Configurando variáveis de ambiente do Web App..."
SPRING_DATASOURCE_URL="jdbc:sqlserver://$SERVER_NAME.database.windows.net:1433;database=$DB_NAME"

az webapp config appsettings set \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --settings \
    APPLICATIONINSIGHTS_CONNECTION_STRING="$CONNECTION_STRING" \
    ApplicationInsightsAgent_EXTENSION_VERSION="~3" \
    XDT_MicrosoftApplicationInsights_Mode="Recommended" \
    XDT_MicrosoftApplicationInsights_PreemptSdk="1" \
    SPRING_DATASOURCE_USERNAME=$DB_USERNAME \
    SPRING_DATASOURCE_PASSWORD=$DB_PASSWORD \
    SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL

# Reiniciar o Web App (idempotente/seguro)
az webapp restart --name $WEBAPP_NAME --resource-group $RESOURCE_GROUP_NAME

# Conectar App ao Application Insights (idempotente)
az monitor app-insights component connect-webapp \
    --app $APP_INSIGHTS_NAME \
    --web-app $WEBAPP_NAME \
    --resource-group $RESOURCE_GROUP_NAME

# ============================
# DEPLOY VIA GITHUB ACTIONS (idempotente)
# ============================
echo "Configurando GitHub Actions..."
az webapp deployment github-actions add \
  --name $WEBAPP_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --repo $GITHUB_REPO_NAME \
  --branch $BRANCH \
  --login-with-github

echo "✅ Script de configuração/verificação concluído com sucesso!"
