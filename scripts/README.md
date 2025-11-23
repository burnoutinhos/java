# Scripts do Banco de Dados

Este diretório contém scripts para criação e gerenciamento do banco de dados do projeto Burnoutinhos API, tanto para ambiente local (Docker) quanto Azure.

## 📁 Arquivos

### Scripts SQL

#### `script-bd.sql`
Script principal de criação do banco de dados e tabelas.

**Características:**
- ✅ Verifica se as tabelas já existem antes de criar (`IF NOT EXISTS`)
- ✅ Cria todas as tabelas necessárias com constraints e índices
- ✅ Inclui dados iniciais (usuário admin padrão)
- ✅ Compatível com SQL Server

**Tabelas criadas:**
1. **app_user** - Usuários do sistema
2. **app_user_roles** - Roles/permissões dos usuários
3. **todo** - Tarefas dos usuários
4. **notification** - Notificações
5. **suggestion** - Sugestões da IA para tarefas
6. **time_block** - Blocos de tempo (cronômetros/temporizadores)
7. **t_gp_mottu_token_push** - Tokens de notificação push

### Scripts de Ambiente Local (Docker)

#### `init-db.sh`
Script interativo e completo para inicialização do banco de dados local.

**Características:**
- ✅ Verifica se Docker está rodando
- ✅ Aguarda SQL Server estar pronto (com retries)
- ✅ Executa o script SQL automaticamente
- ✅ Mostra informações de conexão
- ✅ Output colorido e informativo

**Uso:**
```bash
docker-compose up -d
cd scripts
./init-db.sh
```

#### `verify-db.sh`
Script de verificação completa do banco de dados local.

**O que verifica:**
- ✅ Container está rodando
- ✅ Banco de dados existe
- ✅ Todas as tabelas foram criadas
- ✅ Contagem de registros
- ✅ Usuário admin existe com permissões
- ✅ Índices importantes foram criados
- ✅ Conectividade com o banco

**Uso:**
```bash
cd scripts
./verify-db.sh
```

#### `run-script.sh`
Script shell simples para executar o SQL manualmente no container local do SQL Server.

**Uso:**
```bash
cd scripts
./run-script.sh
```

### Scripts de Ambiente Azure

#### `script-infra.sh`
Script principal de provisionamento da infraestrutura Azure.

**O que faz:**
- ✅ Cria Resource Groups
- ✅ Provisiona SQL Server e Database no Azure
- ✅ Configura firewall do SQL Server
- ✅ **Executa automaticamente o script-bd.sql após criar o banco**
- ✅ Provisiona Application Insights
- ✅ Provisiona Event Hubs
- ✅ Cria App Service Plan e Web App
- ✅ Configura variáveis de ambiente

**Uso:**
```bash
cd scripts
./script-infra.sh
```

**Nota:** Requer `sqlcmd` instalado. O script tentará executar `script-bd.sql` automaticamente após criar o banco de dados.

#### `apply-schema-azure.sh`
Script dedicado para aplicar o schema SQL no Azure SQL Database.

**Características:**
- ✅ Verifica requisitos (sqlcmd instalado)
- ✅ Testa conexão com o banco antes de executar
- ✅ Executa script-bd.sql no Azure
- ✅ Valida tabelas criadas
- ✅ Mensagens de erro detalhadas

**Uso:**
```bash
cd scripts
./apply-schema-azure.sh
```

**Quando usar:**
- Se o `script-infra.sh` não conseguiu executar o SQL automaticamente
- Para reaplicar o schema após mudanças
- Para verificar se o schema está correto no Azure

## 🚀 Como Usar

### Ambiente Local (Docker)

#### Opção 1: Script Interativo (Recomendado)

```bash
# 1. Subir o SQL Server
docker-compose up -d

# 2. Executar inicialização
cd scripts
./init-db.sh

# 3. Verificar
./verify-db.sh
```

#### Opção 2: Executar Manualmente

```bash
# Subir o SQL Server
docker-compose up -d

# Aguardar estar pronto
sleep 30

# Executar script
docker exec -it burnoutinhos-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost \
  -U sa \
  -P verYs3cret \
  -i /scripts/script-bd.sql
```

### Ambiente Azure

#### Opção 1: Provisionamento Completo

```bash
# Executa provisionamento completo + schema SQL
cd scripts
./script-infra.sh
```

O script automaticamente:
1. Cria toda a infraestrutura Azure
2. Executa `script-bd.sql` no banco criado

#### Opção 2: Apenas Aplicar Schema (banco já existe)

```bash
cd scripts
./apply-schema-azure.sh
```

**Pré-requisitos para Azure:**
- Azure CLI instalado e autenticado (`az login`)
- sqlcmd instalado
- Permissões adequadas na subscription Azure

### Instalar sqlcmd

#### Ubuntu/Debian
```bash
curl https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -
curl https://packages.microsoft.com/config/ubuntu/$(lsb_release -rs)/prod.list | sudo tee /etc/apt/sources.list.d/mssql-release.list
sudo apt-get update
sudo ACCEPT_EULA=Y apt-get install -y mssql-tools unixodbc-dev
echo 'export PATH="$PATH:/opt/mssql-tools/bin"' >> ~/.bashrc
source ~/.bashrc
```

#### macOS
```bash
brew tap microsoft/mssql-release https://github.com/Microsoft/homebrew-mssql-release
brew update
HOMEBREW_NO_ENV_FILTERING=1 ACCEPT_EULA=Y brew install mssql-tools
```

## 🔧 Estrutura do Banco de Dados

### Relacionamentos

```
app_user
├── app_user_roles (1:N)
├── todo (1:N)
│   ├── suggestion (1:N)
│   └── time_block (1:N)
├── notification (1:N)
├── suggestion (1:N)
├── time_block (1:N)
└── t_gp_mottu_token_push (1:N)
```

### Índices Criados

**Otimizações de performance:**

- `idx_todo_user_id` - Buscar todos por usuário
- `idx_todo_is_completed` - Filtrar por status
- `idx_todo_user_completed` - Busca combinada (usuário + status)
- `idx_todo_end_time` - Buscar por prazo (usado pelo scheduler)
- `idx_notification_user_date` - Notificações ordenadas por data
- E outros índices para chaves estrangeiras

## 🔐 Usuário Administrador Padrão

O script cria um usuário administrador inicial:

- **Email:** `admin@burnoutinhos.com`
- **Senha:** `admin123` (deve ser alterada após primeiro acesso!)
- **Role:** `ROLE_ADMIN`

> ⚠️ **IMPORTANTE:** A senha deve ser criptografada com BCrypt antes do uso em produção!

## 📊 Informações de Conexão

### Local (Docker)

| Parâmetro | Valor |
|-----------|-------|
| **Host** | `localhost` |
| **Port** | `1433` |
| **Database** | `burnoutinhos_db` |
| **Username** | `sa` |
| **Password** | `verYs3cret` |

**JDBC URL:**
```
jdbc:sqlserver://localhost:1433;databaseName=burnoutinhos_db;encrypt=false
```

### Azure

| Parâmetro | Valor |
|-----------|-------|
| **Host** | `sql-server-burnoutinhos-eastus2.database.windows.net` |
| **Port** | `1433` |
| **Database** | `burnoutinhos-db` |
| **Username** | `burnoutinhos-admin` |
| **Password** | `Bur@N0utinhos!#` |

**JDBC URL:**
```
jdbc:sqlserver://sql-server-burnoutinhos-eastus2.database.windows.net:1433;database=burnoutinhos-db
```

## 🔄 Executar Novamente o Script

O script é **idempotente**, ou seja, pode ser executado múltiplas vezes sem problemas:

- Se a tabela já existe, ela não será recriada
- Se o banco de dados já existe, ele não será recriado
- Se o usuário admin já existe, ele não será duplicado

## 🐛 Troubleshooting

### Local (Docker)

#### Container não inicia
```bash
# Verificar se a porta 1433 está livre
sudo lsof -i :1433

# Verificar logs
docker logs burnoutinhos-sqlserver
```

#### Script não executou
```bash
# Use o script interativo
cd scripts
./init-db.sh

# Ou execute manualmente
./run-script.sh
```

### Azure

#### Erro de conexão
```bash
# Verificar se o firewall está bloqueando
# Adicionar seu IP às regras do firewall
az sql server firewall-rule create \
  --resource-group rg-sql-burnoutinhos \
  --server sql-server-burnoutinhos-eastus2 \
  --name AllowMyIP \
  --start-ip-address $(curl -s https://api.ipify.org) \
  --end-ip-address $(curl -s https://api.ipify.org)
```

#### sqlcmd não encontrado
```bash
# O script mostrará instruções de instalação
# Instale conforme o seu sistema operacional (veja seção acima)
```

#### Script SQL falhou no script-infra.sh
```bash
# Execute manualmente o script de aplicação do schema
cd scripts
./apply-schema-azure.sh
```

## 📝 Modificando o Script

Se você precisar adicionar novas tabelas ou modificar as existentes:

1. Edite o arquivo `script-bd.sql`
2. Mantenha sempre o padrão `IF NOT EXISTS` para garantir idempotência
3. Adicione índices apropriados para otimização
4. Execute o script novamente (local ou Azure)

## 📚 Documentação Adicional

- [DATABASE-SETUP.md](../DATABASE-SETUP.md) - Guia completo de setup
- [QUICK-START.md](./QUICK-START.md) - Guia rápido com comandos
- [.commands.txt](./.commands.txt) - Comandos úteis para copy/paste

## 🔗 Integração com a Aplicação

As configurações do banco no `application.properties` devem corresponder:

**Para Local:**
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=burnoutinhos_db;encrypt=false
spring.datasource.username=sa
spring.datasource.password=verYs3cret
spring.jpa.hibernate.ddl-auto=validate
```

**Para Azure:**
```properties
spring.datasource.url=jdbc:sqlserver://sql-server-burnoutinhos-eastus2.database.windows.net:1433;database=burnoutinhos-db
spring.datasource.username=burnoutinhos-admin
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
```

> **Nota:** Use `ddl-auto=validate` em produção para garantir que o schema do banco corresponda às entidades JPA.

## 📚 Referências

- [SQL Server Docker Hub](https://hub.docker.com/_/microsoft-mssql-server)
- [Azure SQL Database Documentation](https://learn.microsoft.com/en-us/azure/azure-sql/)
- [SQL Server Documentation](https://learn.microsoft.com/en-us/sql/sql-server/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)