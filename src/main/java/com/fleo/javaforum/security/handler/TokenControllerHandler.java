package com.fleo.javaforum.security.handler;

import com.fleo.javaforum.payload.response.ErrorResponse;
import com.fleo.javaforum.security.exception.TokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class TokenControllerHandler {

    @ExceptionHandler(TokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleRefreshTokenException(TokenException ex, WebRequest request) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .errors(List.of("Invalid Token"))
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .build();
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }
}
