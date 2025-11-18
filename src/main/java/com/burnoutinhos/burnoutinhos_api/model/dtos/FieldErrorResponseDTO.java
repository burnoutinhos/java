package com.burnoutinhos.burnoutinhos_api.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldErrorResponseDTO {

    private String field;
    private String message;
}
