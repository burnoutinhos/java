# 🚀 Guia Rápido - Banco de Dados

## Comandos Essenciais

### 1️⃣ Iniciar o Banco de Dados

```bash
# Subir o SQL Server
docker-compose up -d

# Aguardar estar pronto (30 segundos)
sleep 30

# Executar script de criação do banco
cd scripts
./init-db.sh
```

### 2️⃣ Verificar se Está Tudo OK

```bash
cd scripts
./verify-db.sh
```

### 3️⃣ Parar o Banco de Dados

```bash
docker-compose down
```

### 4️⃣ Resetar o Banco de Dados (Apaga Tudo!)

```bash
# Para e remove volumes
docker-compose down -v

# Sobe novamente
docker-compose up -d

# Aguarda e recria o banco
sleep 30
cd scripts
./init-db.sh
```

---

## 📊 Informações de Conexão

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

---

## 👤 Usuário Admin Padrão

- **Email:** `admin@burnoutinhos.com`
- **Senha:** `admin123`
- **Role:** `ROLE_ADMIN`

⚠️ **Importante:** Altere esta senha em produção!

---

## 📁 Scripts Disponíveis

| Script | Descrição |
|--------|-----------|
| `init-db.sh` | Inicializa o banco automaticamente ✨ |
| `verify-db.sh` | Verifica se tudo está OK ✅ |
| `run-script.sh` | Executa o SQL manualmente 🔧 |
| `script-bd.sql` | Script SQL principal 📄 |

---

## 🆘 Problemas Comuns

### Container não inicia
```bash
# Verificar se a porta 1433 está livre
sudo lsof -i :1433

# Verificar logs
docker logs burnoutinhos-sqlserver
```

### Script não executou
```bash
# Executar manualmente
cd scripts
./init-db.sh
```

### Banco não aparece
```bash
# Conectar e verificar
docker exec -it burnoutinhos-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P verYs3cret \
  -Q "SELECT name FROM sys.databases"
```

---

## 📚 Documentação Completa

- [DATABASE-SETUP.md](../DATABASE-SETUP.md) - Guia completo
- [README.md](./README.md) - Documentação detalhada dos scripts

---

**✨ Pronto! Seu banco de dados está configurado e funcionando!**