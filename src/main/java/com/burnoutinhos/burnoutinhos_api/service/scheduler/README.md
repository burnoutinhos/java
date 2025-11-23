# Task Notification Scheduler

## Descrição

O `TaskNotificationScheduler` é um serviço agendado que verifica automaticamente as tarefas pendentes de todos os usuários a cada hora e envia notificações inteligentes.

## Funcionalidades

### 1. Verificação Periódica
- **Frequência**: A cada 1 hora (3.600.000 ms)
- **Execução automática**: Inicia automaticamente quando a aplicação é iniciada

### 2. Tipos de Notificações

#### Notificação de Tarefas para Hoje
- Enviada quando o usuário tem tarefas agendadas para o dia atual
- Mensagem: `"📅 Você tem X tarefa(s) para hoje!"`
- Critério: Tarefas com data de início ou fim igual à data atual

#### Notificação de Tarefas Próximas do Prazo
- Enviada quando uma tarefa está próxima de terminar
- Inclui uma **mensagem personalizada da IA** com dicas de produtividade
- Critério: Tarefas que terminam nas **próximas 2 horas**
- Mensagem: `"⏰ A tarefa '[nome]' está próxima do prazo! 💡 Dica da IA: [sugestão]"`

## Como Funciona

### Fluxo de Execução

1. **Busca de Usuários**: Carrega todos os usuários do sistema
2. **Para cada usuário**:
   - Busca todas as tarefas não concluídas (`isCompleted = false`)
   - Verifica se há tarefas para hoje
   - Verifica se há tarefas próximas do prazo (2 horas)
3. **Geração de Notificações**:
   - Para tarefas do dia: Cria notificação simples
   - Para tarefas próximas do prazo: Gera sugestão com IA e cria notificação
4. **Persistência**: Salva todas as notificações no banco de dados

### Critérios de Verificação

#### Tarefa para Hoje
```java
- task.start.date == hoje OU
- task.end.date == hoje
```

#### Tarefa Próxima do Prazo
```java
- task.end está entre agora e (agora + 2 horas)
```

## Integração com IA

O scheduler utiliza o `OpenAIService` para gerar mensagens personalizadas de ajuda:

- Analisa o nome, descrição e tipo da tarefa
- Gera uma sugestão prática e motivadora
- Limita a resposta para manter a notificação concisa
- Em caso de falha, usa uma mensagem padrão

## Configuração

### Habilitar Scheduling

A anotação `@EnableScheduling` está habilitada em `BurnoutinhosApiApplication.java`:

```java
@SpringBootApplication
@EnableScheduling
public class BurnoutinhosApiApplication {
    // ...
}
```

### Ajustar Frequência

Para alterar o intervalo de execução, modifique o parâmetro `fixedRate`:

```java
@Scheduled(fixedRate = 3600000) // 1 hora em milissegundos
```

Exemplos:
- 30 minutos: `1800000`
- 2 horas: `7200000`
- 6 horas: `21600000`

### Ajustar Janela de Prazo

Para alterar o período de antecedência das notificações de prazo, modifique o método `isTaskNearDeadline()`:

```java
LocalDateTime twoHoursLater = now.plusHours(2); // Altere o valor aqui
```

## Logs

O scheduler utiliza SLF4J para logging com os seguintes níveis:

- **INFO**: Início/fim de execução e estatísticas
- **DEBUG**: Detalhes de processamento por usuário
- **WARN**: Falhas na geração de mensagens da IA
- **ERROR**: Erros críticos no processamento

### Exemplos de Logs

```
🔔 Iniciando verificação de tarefas pendentes...
📊 Verificando tarefas de 15 usuários
✉️ Notificação de tarefas do dia enviada para user@example.com
🤖 Notificação com IA enviada para user@example.com sobre tarefa 'Reunião importante'
✅ Verificação concluída. Total de 23 notificações enviadas.
```

## Dependências

- `TodoRepository`: Acesso às tarefas
- `AppUserRepository`: Acesso aos usuários
- `NotificationRepository`: Persistência de notificações
- `OpenAIService`: Geração de mensagens com IA

## Transações

O método principal `checkPendingTasksAndNotify()` é anotado com `@Transactional` para garantir:
- Consistência dos dados
- Rollback em caso de erro
- Isolamento das operações

## Considerações de Performance

- Execução assíncrona não bloqueia outras operações
- Processamento em lote de todos os usuários
- Logs detalhados para monitoramento
- Tratamento de exceções por usuário (falha em um não afeta os outros)

## Melhorias Futuras

Possíveis extensões do scheduler:

1. **Notificações Personalizadas**: Permitir usuários configurarem horários preferidos
2. **Filtros Avançados**: Notificar apenas sobre tarefas de determinados tipos
3. **Limitar Frequência**: Evitar spam de notificações para o mesmo usuário
4. **Métricas**: Coletar estatísticas sobre taxa de conclusão após notificações
5. **Push Notifications**: Integrar com serviços de notificação mobile
6. **Diferentes Janelas de Tempo**: Notificações para diferentes prazos (1 dia, 1 semana, etc)

## Testes

Para testar o scheduler manualmente:

1. Crie tarefas com diferentes datas
2. Aguarde a execução automática ou reinicie a aplicação
3. Verifique a tabela `notification` no banco de dados
4. Observe os logs para confirmar a execução

## Desabilitar o Scheduler

Para desabilitar temporariamente, remova `@EnableScheduling` de `BurnoutinhosApiApplication.java` ou comente a anotação `@Scheduled` no método.