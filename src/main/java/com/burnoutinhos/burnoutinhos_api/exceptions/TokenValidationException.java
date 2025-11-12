package com.burnoutinhos.burnoutinhos_api.exceptions;

public class TokenValidationException extends RuntimeException {

    public TokenValidationException(String message) {
        super(message);
    }
}
