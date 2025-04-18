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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetAllUrlsIntegrationTest {
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
    public void testGetAllUrlsUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/allUrls", String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    @Test
    public void testGetAllUrlsSuccess() {
        String apiKey = "k1l2m3n4o5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        ResponseEntity<UrlDataDTO[]> response = restTemplate.exchange(
                baseUrl + "/allUrls", org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(headers), UrlDataDTO[].class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
