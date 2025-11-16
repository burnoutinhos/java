package com.burnoutinhos.burnoutinhos_api.service.messagery;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubConsumerAsyncClient;
import com.azure.messaging.eventhubs.models.EventPosition;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import com.burnoutinhos.burnoutinhos_api.model.Suggestion;
import com.burnoutinhos.burnoutinhos_api.model.dtos.TodoEventDTO;
import com.burnoutinhos.burnoutinhos_api.service.AppUserService;
import com.burnoutinhos.burnoutinhos_api.service.SuggestionService;
import com.burnoutinhos.burnoutinhos_api.service.ai.OpenAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Serviço para consumir eventos do Azure Event Hub e processar sugestões de IA.
 * Versão simplificada sem checkpoint store.
 */
@Service
@Slf4j
public class EventHubConsumerService {

    @Value("${azure.eventhubs.connection-string}")
    private String connectionString;

    @Value("${azure.eventhubs.todo-topic:todos}")
    private String eventHubName;

    @Value("${azure.eventhubs.consumer-group:$Default}")
    private String consumerGroup;

    @Value("${azure.eventhubs.enabled:true}")
    private boolean enabled;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private AppUserService appUserService;

    private final ObjectMapper objectMapper;
    private EventHubConsumerAsyncClient consumerClient;
    private final List<Disposable> subscriptions = new ArrayList<>();

    public EventHubConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void start() {
        if (!enabled) {
            log.info("Event Hub Consumer está desabilitado");
            return;
        }

        log.info(
            "Inicializando Event Hub Consumer para tópico: {}",
            eventHubName
        );

        try {
            consumerClient = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .consumerGroup(consumerGroup)
                .buildAsyncConsumerClient();

            // Subscreve em todas as partições
            Disposable subscription = consumerClient
                .getPartitionIds()
                .flatMap(partitionId -> {
                    log.info("Subscrevendo na partição: {}", partitionId);
                    return consumerClient.receiveFromPartition(
                        partitionId,
                        EventPosition.latest()
                    );
                })
                .flatMap(
                    partitionEvent ->
                        // Processa cada evento em uma thread separada (boundedElastic scheduler)
                        Mono.fromCallable(() -> {
                            try {
                                String json = partitionEvent
                                    .getData()
                                    .getBodyAsString();
                                log.info(
                                    "Evento recebido da partição {}: {}",
                                    partitionEvent
                                        .getPartitionContext()
                                        .getPartitionId(),
                                    json
                                );

                                TodoEventDTO todoEvent = objectMapper.readValue(
                                    json,
                                    TodoEventDTO.class
                                );

                                // Gera sugestão usando IA (execução bloqueante permitida aqui)
                                String suggestionText =
                                    openAIService.generateSuggestionForTodo(
                                        todoEvent
                                    );

                                // Cria a suggestion no banco
                                Suggestion suggestion = new Suggestion();
                                suggestion.setSuggestion(suggestionText);

                                if (todoEvent.getUserId() != null) {
                                    AppUser user = appUserService.findById(
                                        todoEvent.getUserId()
                                    );
                                    suggestion.setUser(user);
                                }

                                suggestionService.save(suggestion);
                                log.info(
                                    "✅ Suggestion criada com sucesso para todo ID: {}",
                                    todoEvent.getId()
                                );

                                return true;
                            } catch (Exception e) {
                                log.error(
                                    "❌ Erro ao processar evento: {}",
                                    e.getMessage(),
                                    e
                                );
                                return false;
                            }
                        }).subscribeOn(Schedulers.boundedElastic()) // Thread pool para operações bloqueantes
                )
                .retryWhen(
                    reactor.util.retry.Retry.backoff(
                        Long.MAX_VALUE,
                        Duration.ofSeconds(5)
                    )
                        .maxBackoff(Duration.ofMinutes(1))
                        .doBeforeRetry(signal ->
                            log.warn(
                                "Reconectando ao Event Hub após erro. Tentativa: {}",
                                signal.totalRetries()
                            )
                        )
                )
                .subscribe(
                    result -> {}, // Nada a fazer no sucesso
                    error ->
                        log.error(
                            "❌ Erro fatal no consumer: {}",
                            error.getMessage(),
                            error
                        ),
                    () -> log.info("Consumer finalizado")
                );

            subscriptions.add(subscription);
            log.info("✅ Event Hub Consumer iniciado com sucesso");
        } catch (Exception e) {
            log.error(
                "❌ Erro ao inicializar Event Hub Consumer: {}",
                e.getMessage(),
                e
            );
        }
    }

    @PreDestroy
    public void stop() {
        log.info("Parando Event Hub Consumer...");
        subscriptions.forEach(subscription -> {
            if (!subscription.isDisposed()) {
                subscription.dispose();
            }
        });
        if (consumerClient != null) {
            consumerClient.close();
            log.info("Event Hub Consumer parado");
        }
    }
}
