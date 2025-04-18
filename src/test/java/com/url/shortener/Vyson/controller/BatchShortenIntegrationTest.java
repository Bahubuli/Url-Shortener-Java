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
        valid1.setLongUrl("https://batch1.com");
        UrlRequest valid2 = new UrlRequest();
        valid2.setLongUrl("https://batch2.com");
        UrlRequest invalid = new UrlRequest();
        invalid.setLongUrl("badurl");
        BatchUrlRequest batch = new BatchUrlRequest();
        batch.setUrls(List.of(valid1, valid2, invalid));
        ResponseEntity<BatchUrlResponse> response = restTemplate.postForEntity(
                baseUrl + "/shorten/batch",
                new org.springframework.http.HttpEntity<>(batch, headers),
                BatchUrlResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        BatchUrlResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.getSuccessCount());
        assertEquals(1, body.getErrorCount());
        assertEquals(3, body.getResults().size());
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
}
