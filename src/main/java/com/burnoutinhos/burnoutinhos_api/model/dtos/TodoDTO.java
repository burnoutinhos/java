package com.burnoutinhos.burnoutinhos_api.model.dtos;

import com.burnoutinhos.burnoutinhos_api.model.enums.TodoType;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoDTO {

    @Size(min = 3)
    private String name;

    private LocalDateTime start;
    private LocalDateTime end;

    @Nullable
    private String description;

    private TodoType type;

    @Nullable
    private Long suggestionId;

    private boolean isCompleted = false;
}
