package com.burnoutinhos.burnoutinhos_api.exceptions;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class BadRequestException extends RuntimeException {

    private BindingResult bindingResult;

    public BadRequestException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }
}
