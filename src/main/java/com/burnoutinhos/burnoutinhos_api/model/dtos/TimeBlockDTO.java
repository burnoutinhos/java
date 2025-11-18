package com.burnoutinhos.burnoutinhos_api.model.dtos;

import com.burnoutinhos.burnoutinhos_api.model.enums.TimeBlockType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeBlockDTO {

    @Size(min = 3)
    private String name;

    private Integer timeCount;

    private TimeBlockType type;

    @Nullable
    private Integer max;

    @Nullable
    private Integer start;

    @Nullable
    private Long todoId;
}
