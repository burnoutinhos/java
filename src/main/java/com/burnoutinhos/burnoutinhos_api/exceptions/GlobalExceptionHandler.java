package com.burnoutinhos.burnoutinhos_api.exceptions;

import com.burnoutinhos.burnoutinhos_api.model.dtos.ErrorResponseDTO;
import com.burnoutinhos.burnoutinhos_api.model.dtos.FieldErrorResponseDTO;
import io.jsonwebtoken.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(
        { TokenValidationException.class, SignatureException.class }
    )
    public ResponseEntity<Object> handleTokenError(Exception e) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resposta);
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ResponseEntity<Object> handleUserNotAuthorizedError(
        UserNotAuthorizedException e
    ) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsError(
        UserAlreadyExistsException e
    ) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(
        UsernameNotFoundException e
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ApiFetchErrorException.class)
    public ResponseEntity<String> handleApiFetchError(
        ApiFetchErrorException e
    ) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            e.getMessage()
        );
    }

    @ExceptionHandler(ConversionErrorException.class)
    public ResponseEntity<String> handleConversionError(
        ConversionErrorException e
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            e.getMessage()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
        BadRequestException ex
    ) {
        List<FieldErrorResponseDTO> errors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.add(
                new FieldErrorResponseDTO(fe.getField(), fe.getDefaultMessage())
            );
        }
        return ResponseEntity.badRequest().body(
            new ErrorResponseDTO(400, ex.getMessage(), errors)
        );
    }
}
