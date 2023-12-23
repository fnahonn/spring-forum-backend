package com.fleo.javaforum.exception.handler;

import com.fleo.javaforum.payload.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.util.BindErrorUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @Autowired
    private MessageSource messageSource;
    private final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {

        log.error(e.getMessage(), e);

        final ErrorResponse error = ErrorResponse.builder()
                .message("ENTITY_NOT_FOUND")
                .errors(List.of(e.getMessage()))
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(Instant.now())
                .path(request.getDescription(false))
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        log.error("Bad request : Invalid arguments", e);

        List<String> errors = messageSource != null ?
                BindErrorUtils.resolve(e.getBindingResult().getAllErrors(), messageSource, LocaleContextHolder.getLocale()).values().stream().toList() :
                BindErrorUtils.resolve(e.getBindingResult().getAllErrors()).values().stream().toList();

        final ErrorResponse error = ErrorResponse.builder()
                .message("INVALID_ARGUMENTS")
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(Instant.now())
                .path(request.getDescription(false))
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(error);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException e, WebRequest request) {
        List<String> errors = messageSource != null ?
                BindErrorUtils.resolve(e.getAllErrors(), messageSource, LocaleContextHolder.getLocale()).values().stream().toList() :
                BindErrorUtils.resolve(e.getAllErrors()).values().stream().toList();

        ErrorResponse error = ErrorResponse.builder()
                .message("INVALID_ARGUMENTS")
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(Instant.now())
                .path(request.getDescription(false))
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(error);
    }
}
