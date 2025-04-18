package com.url.shortener.Vyson.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import com.url.shortener.Vyson.dto.UrlResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class UrlShortenIntegrationTest extends UrlControllerIntegrationTestBase {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testShortenUrl() {
        String apiKey = "a1b2c3d4e5";
        String longUrl = "https://exadmple.com";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        Map<String, String> requestBody = Map.of("longUrl", longUrl);
        ResponseEntity<UrlResponse> response = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UrlResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals(longUrl, body.getLongUrl());
        assertNotNull(body.getShortUrl());
    }

    @Test
    public void testShortenUrlMissingLongUrl() {
        String apiKey = "a1b2c3d4e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        Map<String, String> requestBody = Map.of();
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody, headers), String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testShortenUrlWithMalformedUrl() {
        String apiKey = "a1b2c3d4e5";
        String malformedUrl = "htp:/bad_url";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        Map<String, String> requestBody = Map.of("longUrl", malformedUrl);
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody, headers), String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testShortenUrlWithoutApiKey() {
        String longUrl = "https://exadmple.com";
        Map<String, String> requestBody = Map.of("longUrl", longUrl);
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/shorten", requestBody, String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testShortenUrlWithInvalidApiKey() {
        String longUrl = "https://exadmple.com";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", "invalidkey");
        Map<String, String> requestBody = Map.of("longUrl", longUrl);
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody, headers), String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testShortenUrlWithDuplicateLongUrlButDifferentShortCode() {
        String apiKey = "a1b2c3d4e5";
        String longUrl = "https://duplicate.com";
        String shortCode1 = "dupCode1";
        String shortCode2 = "dupCode2";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        // First request with custom shortCode1
        Map<String, Object> requestBody1 = Map.of("longUrl", longUrl, "shortCode", shortCode1);
        ResponseEntity<UrlResponse> firstResponse = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody1, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());
        // Second request with same longUrl but different shortCode2
        Map<String, Object> requestBody2 = Map.of("longUrl", longUrl, "shortCode", shortCode2);
        ResponseEntity<UrlResponse> secondResponse = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody2, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, secondResponse.getStatusCode());
        assertNotEquals(firstResponse.getBody().getShortUrl(), secondResponse.getBody().getShortUrl());
    }

    @Test
    public void testShortenUrlWithDuplicateShortCode() {
        String apiKey = "a1b2c3d4e5";
        String longUrl1 = "https://first.com";
        String longUrl2 = "https://second.com";
        String shortCode = "dupCode";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        // First request with shortCode
        Map<String, Object> requestBody1 = Map.of("longUrl", longUrl1, "shortCode", shortCode);
        ResponseEntity<UrlResponse> firstResponse = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody1, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());
        // Second request with same shortCode but different longUrl
        Map<String, Object> requestBody2 = Map.of("longUrl", longUrl2, "shortCode", shortCode);
        ResponseEntity<String> secondResponse = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody2, headers), String.class
        );
        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());
    }
}
