package com.burnoutinhos.burnoutinhos_api.model.dtos;

import com.burnoutinhos.burnoutinhos_api.model.enums.TodoType;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
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
