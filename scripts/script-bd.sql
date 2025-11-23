-- ============================================
-- Script de Criação de Tabelas
-- Projeto: Burnoutinhos API
-- Banco: SQL Server
-- ============================================

-- ============================================
-- Tabela: app_user
-- Descrição: Armazena os usuários do sistema
-- ============================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[app_user]') AND type in (N'U'))
BEGIN
    CREATE TABLE app_user (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        email NVARCHAR(255) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        language NVARCHAR(50) CHECK (language IN ('PTBR', 'EN', 'ES')),
        profile_image NVARCHAR(500)
    );
    
    PRINT 'Tabela app_user criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela app_user já existe.';
END
GO

-- ============================================
-- Tabela: app_user_roles
-- Descrição: Armazena as roles/permissões dos usuários
-- ============================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[app_user_roles]') AND type in (N'U'))
BEGIN
    CREATE TABLE app_user_roles (
        app_user_id BIGINT NOT NULL,
        roles NVARCHAR(255) NOT NULL,
        CONSTRAINT fk_app_user_roles_user FOREIGN KEY (app_user_id) 
            REFERENCES app_user(id) ON DELETE CASCADE
    );
    
    CREATE INDEX idx_app_user_roles_user_id ON app_user_roles(app_user_id);
    
    PRINT 'Tabela app_user_roles criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela app_user_roles já existe.';
END
GO

-- ============================================
-- Tabela: todo
-- Descrição: Armazena as tarefas dos usuários
-- ============================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[todo]') AND type in (N'U'))
BEGIN
    CREATE TABLE todo (
        todo_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        description NVARCHAR(MAX),
        start DATETIME2,
        end_time DATETIME2,
        is_completed BIT DEFAULT 0,
        type NVARCHAR(50) CHECK (type IN ('TODO', 'FOCUS_MODE', 'REST')),
        user_id BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT fk_todo_user FOREIGN KEY (user_id) 
            REFERENCES app_user(id) ON DELETE CASCADE
    );
    
    CREATE INDEX idx_todo_user_id ON todo(user_id);
    CREATE INDEX idx_todo_is_completed ON todo(is_completed);
    CREATE INDEX idx_todo_type ON todo(type);
    CREATE INDEX idx_todo_end_time ON todo(end_time);
    
    PRINT 'Tabela todo criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela todo já existe.';
END
GO

-- ============================================
-- Tabela: notification
-- Descrição: Armazena as notificações dos usuários
-- ============================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[notification]') AND type in (N'U'))
BEGIN
    CREATE TABLE notification (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        message NVARCHAR(MAX) NOT NULL,
        user_id BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT fk_notification_user FOREIGN KEY (user_id) 
            REFERENCES app_user(id) ON DELETE CASCADE
    );
    
    CREATE INDEX idx_notification_user_id ON notification(user_id);
    CREATE INDEX idx_notification_created_at ON notification(created_at);
    
    PRINT 'Tabela notification criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela notification já existe.';
END
GO

-- ============================================
-- Tabela: suggestion
-- Descrição: Armazena sugestões da IA para tarefas
-- ============================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[suggestion]') AND type in (N'U'))
BEGIN
    CREATE TABLE suggestion (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        suggestion NVARCHAR(1500),
        todo_id BIGINT,
        user_id BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT fk_suggestion_todo FOREIGN KEY (todo_id) 
            REFERENCES todo(todo_id) ON DELETE CASCADE,
        CONSTRAINT fk_suggestion_user FOREIGN KEY (user_id) 
            REFERENCES app_user(id) ON DELETE NO ACTION
    );
    
    CREATE INDEX idx_suggestion_todo_id ON suggestion(todo_id);
    CREATE INDEX idx_suggestion_user_id ON suggestion(user_id);
    
    PRINT 'Tabela suggestion criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela suggestion já existe.';
END
GO

-- ============================================
-- Tabela: time_block
-- Descrição: Armazena blocos de tempo (cronômetros/temporizadores)
-- ============================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[time_block]') AND type in (N'U'))
BEGIN
    CREATE TABLE time_block (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(255) NOT NULL,
        time_count INT,
        max INT,
        start INT,
        type NVARCHAR(50) CHECK (type IN ('CRONOMETER', 'TEMPORIZER')),
        todo_id BIGINT,
        user_id BIGINT NOT NULL,
        created_at DATETIME2 DEFAULT GETDATE(),
        updated_at DATETIME2 DEFAULT GETDATE(),
        CONSTRAINT fk_time_block_todo FOREIGN KEY (todo_id) 
            REFERENCES todo(todo_id) ON DELETE CASCADE,
        CONSTRAINT fk_time_block_user FOREIGN KEY (user_id) 
            REFERENCES app_user(id) ON DELETE NO ACTION
    );
    
    CREATE INDEX idx_time_block_todo_id ON time_block(todo_id);
    CREATE INDEX idx_time_block_user_id ON time_block(user_id);
    CREATE INDEX idx_time_block_type ON time_block(type);
    
    PRINT 'Tabela time_block criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela time_block já existe.';
END
GO

-- ============================================
-- Tabela: t_gp_mottu_token_push
-- Descrição: Armazena tokens de notificação push
-- ============================================
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[t_gp_mottu_token_push]') AND type in (N'U'))
BEGIN
    CREATE TABLE t_gp_mottu_token_push (
        id_token_push BIGINT IDENTITY(1,1) PRIMARY KEY,
        token NVARCHAR(500) NOT NULL,
        user_id BIGINT NOT NULL,
        CONSTRAINT fk_push_token_user FOREIGN KEY (user_id) 
            REFERENCES app_user(id) ON DELETE CASCADE
    );
    
    CREATE INDEX idx_push_token_user_id ON t_gp_mottu_token_push(user_id);
    CREATE INDEX idx_push_token_token ON t_gp_mottu_token_push(token);
    
    PRINT 'Tabela t_gp_mottu_token_push criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela t_gp_mottu_token_push já existe.';
END
GO

-- ============================================
-- Índices adicionais para otimização
-- ============================================

-- Índice composto para buscas frequentes de todos por usuário e status
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_todo_user_completed' AND object_id = OBJECT_ID('todo'))
BEGIN
    CREATE INDEX idx_todo_user_completed ON todo(user_id, is_completed);
    PRINT 'Índice idx_todo_user_completed criado.';
END
GO

-- Índice para buscas de notificações por usuário e data
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_notification_user_date' AND object_id = OBJECT_ID('notification'))
BEGIN
    CREATE INDEX idx_notification_user_date ON notification(user_id, created_at DESC);
    PRINT 'Índice idx_notification_user_date criado.';
END
GO