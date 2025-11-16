package com.burnoutinhos.burnoutinhos_api.model.dtos;

import com.burnoutinhos.burnoutinhos_api.model.Todo;
import com.burnoutinhos.burnoutinhos_api.model.enums.TodoType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para serialização de eventos de Todo no Event Hub.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoEventDTO {

    private Long id;
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;

    private boolean isCompleted;
    private TodoType type;
    private Long userId;

    /**
     * Converte um Todo em TodoEventDTO.
     */
    public static TodoEventDTO fromTodo(Todo todo) {
        return new TodoEventDTO(
            todo.getId(),
            todo.getName(),
            todo.getDescription(),
            todo.getStart(),
            todo.getEnd(),
            todo.isCompleted(),
            todo.getType(),
            todo.getUser() != null ? todo.getUser().getId() : null
        );
    }
}
