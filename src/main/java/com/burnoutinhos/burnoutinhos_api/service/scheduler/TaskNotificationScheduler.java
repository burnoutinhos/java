package com.burnoutinhos.burnoutinhos_api.service.scheduler;

import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.Notification;
import com.burnoutinhos.burnoutinhos_api.model.Todo;
import com.burnoutinhos.burnoutinhos_api.model.dtos.TodoEventDTO;
import com.burnoutinhos.burnoutinhos_api.repository.AppUserRepository;
import com.burnoutinhos.burnoutinhos_api.repository.NotificationRepository;
import com.burnoutinhos.burnoutinhos_api.repository.TodoRepository;
import com.burnoutinhos.burnoutinhos_api.service.ai.OpenAIService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduler que verifica tarefas pendentes a cada hora e envia notificações
 * para usuários com tarefas próximas do prazo ou tarefas para hoje.
 */
@Service
@Slf4j
public class TaskNotificationScheduler {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OpenAIService openAIService;

    /**
     * Executa a cada 1 hora (3600000 ms).
     * Verifica tarefas pendentes e envia notificações aos usuários.
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void checkPendingTasksAndNotify() {
        log.info("🔔 Iniciando verificação de tarefas pendentes...");

        try {
            List<AppUser> allUsers = appUserRepository.findAll();
            log.info("📊 Verificando tarefas de {} usuários", allUsers.size());

            int totalNotifications = 0;

            for (AppUser user : allUsers) {
                int userNotifications = processUserTasks(user);
                totalNotifications += userNotifications;
            }

            log.info(
                "✅ Verificação concluída. Total de {} notificações enviadas.",
                totalNotifications
            );
        } catch (Exception e) {
            log.error(
                "❌ Erro ao processar verificação de tarefas: {}",
                e.getMessage(),
                e
            );
        }
    }

    /**
     * Processa as tarefas de um usuário específico e cria notificações.
     *
     * @param user Usuário cujas tarefas serão processadas
     * @return Número de notificações criadas para este usuário
     */
    private int processUserTasks(AppUser user) {
        int notificationCount = 0;

        try {
            // Busca tarefas não concluídas do usuário
            List<Todo> pendingTasks = todoRepository.findByUserAndIsCompleted(
                user,
                false
            );

            if (pendingTasks.isEmpty()) {
                log.debug(
                    "Usuário {} não possui tarefas pendentes",
                    user.getEmail()
                );
                return 0;
            }

            // Verifica se há tarefas para hoje
            List<Todo> tasksForToday = pendingTasks
                .stream()
                .filter(this::isTaskForToday)
                .toList();

            if (!tasksForToday.isEmpty()) {
                createSimpleNotification(
                    user,
                    String.format(
                        "📅 Você tem %d tarefa(s) para hoje!",
                        tasksForToday.size()
                    )
                );
                notificationCount++;
                log.info(
                    "✉️ Notificação de tarefas do dia enviada para {}",
                    user.getEmail()
                );
            }

            // Verifica tarefas próximas de concluir (próximas 2 horas)
            List<Todo> tasksNearDeadline = pendingTasks
                .stream()
                .filter(this::isTaskNearDeadline)
                .toList();

            for (Todo task : tasksNearDeadline) {
                // Gera mensagem da IA para ajudar o usuário
                String aiMessage = generateAIHelpMessage(task);

                createNotificationWithAI(user, task, aiMessage);
                notificationCount++;

                log.info(
                    "🤖 Notificação com IA enviada para {} sobre tarefa '{}'",
                    user.getEmail(),
                    task.getName()
                );
            }
        } catch (Exception e) {
            log.error(
                "❌ Erro ao processar tarefas do usuário {}: {}",
                user.getEmail(),
                e.getMessage(),
                e
            );
        }

        return notificationCount;
    }

    /**
     * Verifica se a tarefa é para hoje.
     *
     * @param task Tarefa a ser verificada
     * @return true se a tarefa tem início ou fim hoje
     */
    private boolean isTaskForToday(Todo task) {
        LocalDate today = LocalDate.now();

        if (task.getStart() != null) {
            LocalDate startDate = task.getStart().toLocalDate();
            if (startDate.isEqual(today)) {
                return true;
            }
        }

        if (task.getEnd() != null) {
            LocalDate endDate = task.getEnd().toLocalDate();
            if (endDate.isEqual(today)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica se a tarefa está próxima do prazo de conclusão (próximas 2 horas).
     *
     * @param task Tarefa a ser verificada
     * @return true se a tarefa termina nas próximas 2 horas
     */
    private boolean isTaskNearDeadline(Todo task) {
        if (task.getEnd() == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursLater = now.plusHours(2);

        return (
            task.getEnd().isAfter(now) &&
            task.getEnd().isBefore(twoHoursLater)
        );
    }

    /**
     * Gera uma mensagem de ajuda da IA para a tarefa.
     *
     * @param task Tarefa para gerar a mensagem
     * @return Mensagem gerada pela IA
     */
    private String generateAIHelpMessage(Todo task) {
        try {
            TodoEventDTO todoDTO = new TodoEventDTO();
            todoDTO.setId(task.getId());
            todoDTO.setName(task.getName());
            todoDTO.setDescription(task.getDescription());
            todoDTO.setType(task.getType());

            String aiSuggestion = openAIService.generateSuggestionForTodo(
                todoDTO
            );

            return String.format(
                "⏰ A tarefa '%s' está próxima do prazo! 💡 Dica da IA: %s",
                task.getName(),
                aiSuggestion
            );
        } catch (Exception e) {
            log.warn(
                "Falha ao gerar mensagem da IA para tarefa {}: {}",
                task.getId(),
                e.getMessage()
            );
            return String.format(
                "⏰ A tarefa '%s' está próxima do prazo! Não esqueça de completá-la.",
                task.getName()
            );
        }
    }

    /**
     * Cria uma notificação simples para o usuário.
     *
     * @param user Usuário que receberá a notificação
     * @param message Mensagem da notificação
     */
    private void createSimpleNotification(AppUser user, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    /**
     * Cria uma notificação com mensagem da IA para o usuário.
     *
     * @param user Usuário que receberá a notificação
     * @param task Tarefa relacionada à notificação
     * @param aiMessage Mensagem gerada pela IA
     */
    private void createNotificationWithAI(
        AppUser user,
        Todo task,
        String aiMessage
    ) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(aiMessage);
        notificationRepository.save(notification);
    }
}