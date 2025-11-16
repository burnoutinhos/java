package com.burnoutinhos.burnoutinhos_api.service.ai;

import com.burnoutinhos.burnoutinhos_api.model.dtos.TodoEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Serviço para integração com OpenAI usando Spring AI.
 */
@Service
@Slf4j
public class OpenAIService {

    private final ChatClient chatClient;
    private static final int MAX_SUGGESTION_LENGTH = 1500;

    @Autowired
    public OpenAIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
            .defaultOptions(
                OpenAiChatOptions.builder()
                    .withMaxTokens(150) // Limita tokens da resposta
                    .build()
            )
            .build();
    }

    /**
     * Gera uma sugestão baseada em um Todo usando OpenAI.
     *
     * @param todo DTO do todo para gerar sugestão
     * @return Texto da sugestão gerada pela IA
     */
    public String generateSuggestionForTodo(TodoEventDTO todo) {
        log.info("Gerando sugestão de IA para todo: {}", todo.getName());

        String prompt = buildPrompt(todo);

        try {
            String suggestion = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

            // Garante que não excede o tamanho máximo
            if (
                suggestion != null &&
                suggestion.length() > MAX_SUGGESTION_LENGTH
            ) {
                log.warn(
                    "Sugestão muito longa ({}), truncando para {} caracteres",
                    suggestion.length(),
                    MAX_SUGGESTION_LENGTH
                );
                suggestion =
                    suggestion.substring(0, MAX_SUGGESTION_LENGTH - 3) + "...";
            }

            log.info(
                "✅ Sugestão gerada com sucesso para todo ID: {}",
                todo.getId()
            );
            return suggestion;
        } catch (Exception e) {
            log.error(
                "❌ Erro ao gerar sugestão com OpenAI: {}",
                e.getMessage(),
                e
            );
            return "Não foi possível gerar uma sugestão no momento. Por favor, tente novamente mais tarde.";
        }
    }

    private String buildPrompt(TodoEventDTO todo) {
        return String.format(
            "Você é um assistente especializado em produtividade. " +
                "Analise a seguinte tarefa e forneça uma sugestão prática (máximo de dois parágrafos e 3 frases) " +
                "para ajudar a pessoa a completá-la:\n\n" +
                "Tarefa: %s\n" +
                "Descrição: %s\n" +
                "Tipo: %s\n\n" +
                "Responda de forma concisa e motivadora.",
            todo.getName(),
            todo.getDescription() != null
                ? todo.getDescription()
                : "Sem descrição",
            todo.getType() != null ? todo.getType() : "Não especificado"
        );
    }
}
