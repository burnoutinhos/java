# 🗄️ Configuração do Banco de Dados

Guia rápido para configurar e inicializar o banco de dados do projeto Burnoutinhos API.

## 📋 Pré-requisitos

- Docker e Docker Compose instalados
- Porta 1433 disponível

## 🚀 Início Rápido

### Opção 1: Script de Inicialização Interativo (Recomendado)

Primeiro inicie o Docker Compose, depois execute o script de inicialização:

```bash
# Inicie os containers
docker-compose up -d

# Execute o script de inicialização
cd scripts
./init-db.sh
```

Este script irá:
- ✅ Verificar se o Docker está rodando
- ✅ Verificar se o SQL Server está saudável
- ✅ Executar o script SQL automaticamente
- ✅ Mostrar informações de conexão

### Opção 2: Execução Manual

Se preferir executar manualmente:

```bash
# Primeiro inicie os containers
docker-compose up -d

# Aguarde o SQL Server estar pronto (cerca de 30 segundos)
sleep 30

# Execute o script manualmente
docker exec -it burnoutinhos-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost \
  -U sa \
  -P verYs3cret \
  -i /scripts/script-bd.sql
```

## 📊 Informações de Conexão

| Parâmetro | Valor |
|-----------|-------|
| **Host** | `localhost` |
| **Port** | `1433` |
| **Database** | `burnoutinhos_db` |
| **Username** | `sa` |
| **Password** | `verYs3cret` |

### String de Conexão JDBC

```
jdbc:sqlserver://localhost:1433;databaseName=burnoutinhos_db;encrypt=false
```

## 🗃️ Estrutura do Banco

O script cria automaticamente as seguintes tabelas:

1. **app_user** - Usuários do sistema
2. **app_user_roles** - Permissões dos usuários
3. **todo** - Tarefas dos usuários
4. **notification** - Notificações
5. **suggestion** - Sugestões da IA
6. **time_block** - Blocos de tempo (cronômetros/temporizadores)
7. **t_gp_mottu_token_push** - Tokens de notificação push

### Diagrama de Relacionamentos

```
app_user (1) ─────┬──────── (N) todo
                  │              │
                  │              ├── (N) suggestion
                  │              └── (N) time_block
                  │
                  ├──────── (N) notification
                  ├──────── (N) suggestion
                  ├──────── (N) time_block
                  ├──────── (N) t_gp_mottu_token_push
                  └──────── (N) app_user_roles
```

## 👤 Usuário Administrador Padrão

O script cria automaticamente um usuário administrador:

| Campo | Valor |
|-------|-------|
| **Email** | `admin@burnoutinhos.com` |
| **Senha** | `admin123` |
| **Role** | `ROLE_ADMIN` |

> ⚠️ **IMPORTANTE:** Altere esta senha após o primeiro acesso em ambiente de produção!

## 🔧 Comandos Úteis

### Verificar status do container

```bash
docker ps | grep burnoutinhos-sqlserver
```

### Ver logs do SQL Server

```bash
docker logs burnoutinhos-sqlserver
```

### Conectar no SQL Server via CLI

```bash
docker exec -it burnoutinhos-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost \
  -U sa \
  -P verYs3cret
```

### Listar tabelas criadas

```sql
USE burnoutinhos_db;
GO

SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;
GO
```

### Resetar o banco de dados

```bash
# Parar e remover containers
docker-compose down -v

# Subir novamente
docker-compose up -d

# Execute o script de inicialização
cd scripts
./init-db.sh
```

## 🐛 Troubleshooting

### Problema: Porta 1433 já está em uso

```bash
# Verificar o que está usando a porta
sudo lsof -i :1433

# Ou no Windows
netstat -ano | findstr :1433

# Opção 1: Pare o serviço que está usando a porta
# Opção 2: Altere a porta no compose.yaml
```

### Problema: Script não foi executado

```bash
# Use o script interativo (recomendado)
cd scripts
./init-db.sh

# Ou execute manualmente
./run-script.sh
```

### Problema: Erro de autenticação

Verifique se a senha está correta no `compose.yaml`:
- `SA_PASSWORD=verYs3cret`
- `MSSQL_SA_PASSWORD=verYs3cret`

### Problema: Container não fica saudável

```bash
# Verifique os logs
docker logs burnoutinhos-sqlserver

# Aguarde mais tempo (pode levar até 1 minuto)
docker-compose ps

# Verifique healthcheck
docker inspect burnoutinhos-sqlserver | grep -A 10 Health
```

### Problema: Tabelas não aparecem

```bash
# Verifique se está no banco correto
docker exec -it burnoutinhos-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P verYs3cret \
  -Q "SELECT name FROM sys.databases"

# Execute o script manualmente se necessário
```

## 🔄 Executar Novamente o Script

O script é **idempotente** (pode ser executado múltiplas vezes):

```bash
# Não há problema em executar novamente
cd scripts
./init-db.sh
```

As verificações `IF NOT EXISTS` garantem que:
- ✅ Tabelas existentes não serão recriadas
- ✅ Dados não serão duplicados
- ✅ Não haverá erros de conflito

## 🔐 Segurança em Produção

Antes de ir para produção:

1. **Altere a senha do SA:**
   ```sql
   ALTER LOGIN sa WITH PASSWORD = 'NovaS3nh@Forte!';
   ```

2. **Crie um usuário específico para a aplicação:**
   ```sql
   CREATE LOGIN burnoutinhos_user WITH PASSWORD = 'S3nh@Forte!';
   CREATE USER burnoutinhos_user FOR LOGIN burnoutinhos_user;
   GRANT SELECT, INSERT, UPDATE, DELETE ON DATABASE::burnoutinhos_db TO burnoutinhos_user;
   ```

3. **Altere a senha do usuário admin:**
   - Faça login com `admin@burnoutinhos.com`
   - Altere para uma senha forte e criptografada

4. **Configure SSL/TLS:**
   ```
   encrypt=true;trustServerCertificate=false
   ```

## 📚 Documentação Adicional

- [Scripts SQL detalhados](./scripts/README.md)
- [Scheduler de Notificações](./src/main/java/com/burnoutinhos/burnoutinhos_api/service/scheduler/README.md)
- [SQL Server Documentation](https://learn.microsoft.com/en-us/sql/sql-server/)

## 💡 Próximos Passos

Após configurar o banco:

1. Configure o `application.properties`:
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=burnoutinhos_db;encrypt=false
   spring.datasource.username=sa
   spring.datasource.password=verYs3cret
   spring.jpa.hibernate.ddl-auto=validate
   ```

2. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Teste a conexão acessando:
   ```
   http://localhost:8080/swagger-ui.html
   ```

---

✅ **Pronto!** Seu banco de dados está configurado e pronto para uso! 🎉