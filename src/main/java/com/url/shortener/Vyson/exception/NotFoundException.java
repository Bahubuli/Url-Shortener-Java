package com.url.shortener.Vyson.exception; // Note the package name

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}