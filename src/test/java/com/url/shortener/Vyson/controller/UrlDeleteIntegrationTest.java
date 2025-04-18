package com.url.shortener.Vyson.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import com.url.shortener.Vyson.dto.UrlResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UrlDeleteIntegrationTest extends UrlControllerIntegrationTestBase {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testDeleteExistingUrl() throws Exception {
        // First, create a short URL to delete
        String apiKey = "k1l2m3n4o5";
        String longUrl = "https://example.com/delete";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        Map<String, String> requestBody = Map.of("longUrl", longUrl);
        ResponseEntity<UrlResponse> shortenResponse = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(requestBody, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, shortenResponse.getStatusCode());
        String shortCode = shortenResponse.getBody().getShortUrl();
        assertNotNull(shortCode);

        // Now, delete the short URL
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                RequestEntity.delete(URI.create(baseUrl + "/delete?shortCode=" + shortCode))
                        .header("api_key", apiKey)
                        .build(),
                String.class
        );
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertTrue(deleteResponse.getBody().toLowerCase().contains("deleted"));
    }

    @Test
    public void testDeleteNonExistentShortCode() {
        String apiKey = "k1l2m3n4o5";
        String nonExistentShortCode = "nonexistent123";
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                RequestEntity.delete(URI.create(baseUrl + "/delete?shortCode=" + nonExistentShortCode))
                        .header("api_key", apiKey)
                        .build(),
                String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, deleteResponse.getStatusCode());
        assertNotNull(deleteResponse.getBody());
        assertTrue(deleteResponse.getBody().toLowerCase().contains("does not exist"));
    }

    @Test
    public void testDeleteWithoutApiKey() {
        String shortCode = "anycode";
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                RequestEntity.delete(URI.create(baseUrl + "/delete?shortCode=" + shortCode)).build(),
                String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, deleteResponse.getStatusCode());
    }

    @Test
    public void testDeleteWithInvalidApiKey() {
        String shortCode = "anycode";
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                RequestEntity.delete(URI.create(baseUrl + "/delete?shortCode=" + shortCode))
                        .header("api_key", "invalidkey")
                        .build(),
                String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, deleteResponse.getStatusCode());
    }

    @Test
    public void testDeleteWithMissingShortCode() {
        String apiKey = "k1l2m3n4o5";
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                RequestEntity.delete(URI.create(baseUrl + "/delete"))
                        .header("api_key", apiKey)
                        .build(),
                String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, deleteResponse.getStatusCode());
    }
}
