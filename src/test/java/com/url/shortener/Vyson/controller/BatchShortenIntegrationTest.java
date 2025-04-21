package com.url.shortener.Vyson.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.url.shortener.Vyson.dto.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BatchShortenIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    private String baseUrl;
    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port;
        restTemplate.getRestTemplate().setRequestFactory(new NonRedirectingClientHttpRequestFactory());
    }
    @Test
    public void testBatchShortenSuccessAndValidation() {
        String apiKey = "k1l2m3n4o5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest valid1 = new UrlRequest();
        valid1.setLongUrl("https://facebook.com");
        UrlRequest valid2 = new UrlRequest();
        valid2.setLongUrl("https://facebook2.com");
        valid2.setShortCode("1ec");
        BatchUrlRequest batch = new BatchUrlRequest();
        batch.setUrls(List.of(valid1, valid2));
        ResponseEntity<BatchUrlResponse> response = restTemplate.postForEntity(
                baseUrl + "/shorten/batch",
                new org.springframework.http.HttpEntity<>(batch, headers),
                BatchUrlResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BatchUrlResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.getSuccessCount());
        assertEquals(0, body.getErrorCount());
        assertEquals(2, body.getResults().size());
        assertEquals("https://facebook.com", body.getResults().get(0).getLongUrl());
        assertEquals("https://facebook2.com", body.getResults().get(1).getLongUrl());
        assertEquals("1ec", body.getResults().get(1).getShortUrl());
    }
    @Test
    public void testBatchShortenUnauthorized() {
        BatchUrlRequest batch = new BatchUrlRequest();
        UrlRequest valid = new UrlRequest();
        valid.setLongUrl("https://batchunauth.com");
        batch.setUrls(List.of(valid));
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/shorten/batch", batch, String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testBatchShortenSuccessWithBusinessTier() {
        // This API key is allowed to batch request
        String apiKey = "a1b2c3d4e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest valid1 = new UrlRequest();
        valid1.setLongUrl("https://facebook.com");
        UrlRequest valid2 = new UrlRequest();
        valid2.setLongUrl("https://facebook2.com");
        BatchUrlRequest batch = new BatchUrlRequest();
        batch.setUrls(List.of(valid1, valid2));
        ResponseEntity<BatchUrlResponse> response = restTemplate.postForEntity(
                baseUrl + "/shorten/batch",
                new org.springframework.http.HttpEntity<>(batch, headers),
                BatchUrlResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BatchUrlResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.getSuccessCount());
        assertEquals(0, body.getErrorCount());
        assertEquals(2, body.getResults().size());
        assertEquals("https://facebook.com", body.getResults().get(0).getLongUrl());
        assertEquals("https://facebook2.com", body.getResults().get(1).getLongUrl());
       
    }

    @Test
    public void testBatchShortenHobbyTierShowsUpgradeMessage() {
        // This API key is hobby tier and should not allow batch request
        String apiKey = "k1l2m3n4o5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest valid1 = new UrlRequest();
        valid1.setLongUrl("https://facebook.com");
        UrlRequest valid2 = new UrlRequest();
        valid2.setLongUrl("https://facebook2.com");
        valid2.setShortCode("1ec");
        BatchUrlRequest batch = new BatchUrlRequest();
        batch.setUrls(List.of(valid1, valid2));
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/shorten/batch",
                new org.springframework.http.HttpEntity<>(batch, headers),
                String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        if (response.getBody() != null) {
            assertTrue(response.getBody().contains("Please upgrade to business tier"));
        }
    }
}
