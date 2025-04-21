package com.url.shortener.Vyson.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerMapping;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final HandlerMapping resourceHandlerMapping;

    public GlobalExceptionHandler(HandlerMapping resourceHandlerMapping) {
        this.resourceHandlerMapping = resourceHandlerMapping;
    }

    @ExceptionHandler(DuplicateUrlException.class)
    public ResponseEntity<String> handleDuplicateUrl(DuplicateUrlException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict status code
                .body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundUrl(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404 Not Found status code
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex) {
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
    @ExceptionHandler(ExpiredException.class)
    public ResponseEntity<String> handleExpired(ExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(ex.getMessage());
    }
}
