package com.url.shortener.Vyson.exception;

public class ExpiredException extends RuntimeException {
    public ExpiredException(String message) {
        super(message);
    }
}
