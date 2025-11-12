package com.burnoutinhos.burnoutinhos_api.exceptions;

public class UserNotAuthorizedException extends RuntimeException {

    public UserNotAuthorizedException(String message) {
        super(message);
    }
}
