package com.burnoutinhos.burnoutinhos_api.model.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {

    private Integer code;
    private String message;
    private List<FieldErrorResponseDTO> fieldErrorResponseDTO;
}
