package com.url.shortener.Vyson.controller;

import com.url.shortener.Vyson.dto.UrlResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class UrlRedirectIntegrationTest extends UrlControllerIntegrationTestBase {
    @Test
    public void testRedirect() {
        String longUrl = "https://youtubkkexx.com";
        String apiKey = "a1b2c3d4e5";
        Map<String, String> requestBody = Map.of("longUrl", longUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);

        // Expect a UrlResponse DTO, not a String
        ResponseEntity<UrlResponse> shortenResponse = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, shortenResponse.getStatusCode());
        com.url.shortener.Vyson.dto.UrlResponse urlResponse = shortenResponse.getBody();
        assertNotNull(urlResponse, "UrlResponse must not be null.");
        String shortCode = urlResponse.getShortUrl();
        assertNotNull(shortCode, "Short code must not be null.");
        ResponseEntity<Void> redirectResponse = restTemplate.getForEntity(
                baseUrl + "/redirect?code=" + shortCode, Void.class
        );
        assertEquals(HttpStatus.FOUND, redirectResponse.getStatusCode());
        assertNotNull(redirectResponse.getHeaders().getLocation(), "Location header should be present.");
        assertEquals(longUrl, redirectResponse.getHeaders().getLocation().toString());
    }

    @Test
    public void testRedirectNonExistentShortCode() {
        String nonExistentShortCode = "abcdef";
        ResponseEntity<Void> response = restTemplate.getForEntity(
                baseUrl + "/redirect?code=" + nonExistentShortCode, Void.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
