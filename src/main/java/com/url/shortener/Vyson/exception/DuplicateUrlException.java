package com.url.shortener.Vyson.exception; // Note the package name

public class DuplicateUrlException extends RuntimeException {
    public DuplicateUrlException(String message) {
        super(message);
    }
}