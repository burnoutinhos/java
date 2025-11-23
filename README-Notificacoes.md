# Sistema de Notificações

## Descrição
Este projeto implementa um sistema de notificações que permite enviar e gerenciar notificações para usuários de forma eficiente e escalável.

## Características

- 📧 Suporte para múltiplos canais (email, SMS, push notifications)
- 🔔 Notificações em tempo real
- 📊 Histórico de notificações enviadas
- ⚙️ Configuração de preferências de notificação por usuário
- 🔄 Sistema de retry para falhas no envio
- 📱 Suporte para notificações agrupadas

## Instalação

```bash
npm install
```

## Configuração

1. Configure as variáveis de ambiente no arquivo `.env`:

```env
NOTIFICATION_SERVICE_URL=your_service_url
EMAIL_API_KEY=your_email_api_key
SMS_API_KEY=your_sms_api_key
PUSH_API_KEY=your_push_api_key
```

2. Configure os templates de notificação em `config/templates.json`

## Uso

### Enviar uma notificação simples

```javascript
const notificationService = require('./services/notification');

await notificationService.send({
  userId: '123',
  type: 'email',
  subject: 'Bem-vindo!',
  message: 'Obrigado por se cadastrar.'
});
```

### Enviar notificação para múltiplos canais

```javascript
await notificationService.sendMultiChannel({
  userId: '123',
  channels: ['email', 'push'],
  template: 'welcome',
  data: {
    userName: 'João'
  }
});
```

## API

### POST /notifications/send

Envia uma notificação para um usuário.

**Parâmetros:**
- `userId` (string): ID do usuário
- `type` (string): Tipo de notificação (email, sms, push)
- `message` (string): Conteúdo da mensagem
- `priority` (string): Prioridade (low, medium, high)

### GET /notifications/:userId

Retorna o histórico de notificações de um usuário.

### PUT /notifications/preferences/:userId

Atualiza as preferências de notificação do usuário.

## Contribuindo

Contribuições são bem-vindas! Por favor, abra uma issue ou pull request.

## Licença

MIT
