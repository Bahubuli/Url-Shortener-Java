package com.url.shortener.Vyson.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class UrlRedirectIntegrationTest extends UrlControllerIntegrationTestBase {
    @Test
    public void testRedirect() {
        String longUrl = "https://youtube.com";
        Map<String, String> requestBody = Map.of("longUrl", longUrl);
        ResponseEntity<String> shortenResponse = restTemplate.postForEntity(
                baseUrl + "/shorten", requestBody, String.class
        );
        assertEquals(HttpStatus.OK, shortenResponse.getStatusCode());
        String shortCode = shortenResponse.getBody();
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
