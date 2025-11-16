package com.burnoutinhos.burnoutinhos_api.service.messagery;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.burnoutinhos.burnoutinhos_api.model.dtos.TodoEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Serviço para publicar eventos no Azure Event Hub.
 */
@Service
@Slf4j
public class EventHubProducerService {

    @Value("${azure.eventhubs.connection-string}")
    private String connectionString;

    @Value("${azure.eventhubs.todo-topic:todos}")
    private String eventHubName;

    private EventHubProducerClient producerClient;
    private final ObjectMapper objectMapper;

    public EventHubProducerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        log.info(
            "Inicializando Event Hub Producer para tópico: {}",
            eventHubName
        );
        this.producerClient = new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .buildProducerClient();
    }

    /**
     * Publica um evento de Todo no Event Hub.
     */
    public void publishTodoEvent(TodoEventDTO todoEvent) {
        try {
            String json = objectMapper.writeValueAsString(todoEvent);
            EventDataBatch batch = producerClient.createBatch();
            batch.tryAdd(new EventData(json));

            producerClient.send(batch);
            log.info(
                "Evento de Todo publicado com sucesso. ID: {}",
                todoEvent.getId()
            );
        } catch (Exception e) {
            log.error("Erro ao publicar evento de Todo: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao publicar evento", e);
        }
    }

    @PreDestroy
    public void close() {
        if (producerClient != null) {
            producerClient.close();
            log.info("Event Hub Producer fechado");
        }
    }
}
