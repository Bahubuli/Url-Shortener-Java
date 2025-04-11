package com.url.shortener.Vyson.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponse {
    private String longUrl;
    private String shortUrl;
    private boolean success;
    private ErrorResponse error;

    // Nested class for error details
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        private String errorCode;
        private String errorMessage;
    }
}
