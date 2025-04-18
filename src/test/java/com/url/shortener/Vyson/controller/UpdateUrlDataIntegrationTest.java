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
public class UpdateUrlDataIntegrationTest {
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
    public void testUpdateUrlDataSuccessAndUnauthorized() {
        String apiKey = "a1b2c3d4e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest req = new UrlRequest();
        req.setLongUrl("https://update.com");
        ResponseEntity<UrlResponse> createResp = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(req, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        String shortUrl = createResp.getBody().getShortUrl();
        ResponseEntity<UrlDataDTO[]> allResp = restTemplate.exchange(
                baseUrl + "/allUrls", org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(headers), UrlDataDTO[].class
        );
        assertEquals(HttpStatus.OK, allResp.getStatusCode());
        Long id = null;
        for (UrlDataDTO dto : allResp.getBody()) {
            if (dto.getShortUrl().equals(shortUrl)) {
                id = dto.getId();
                break;
            }
        }
        assertNotNull(id);
        UpdateUrlDataRequest updateReq = new UpdateUrlDataRequest();
        updateReq.setId(id);
        updateReq.setLongUrl("https://update.com/changed");
        updateReq.setActive(true);
        ResponseEntity<UrlResponse> updateResp = restTemplate.exchange(
                baseUrl + "/urlData", org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateReq, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.OK, updateResp.getStatusCode());
        assertEquals("https://update.com/changed", updateResp.getBody().getLongUrl());
        ResponseEntity<String> unauthResp = restTemplate.exchange(
                baseUrl + "/urlData", org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateReq), String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, unauthResp.getStatusCode());
    }

    @Test
    public void testUpdateOnlyLongUrl() {
        String apiKey = "a1b2c3d4e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest req = new UrlRequest();
        req.setLongUrl("https://update-long.com");
        ResponseEntity<UrlResponse> createResp = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(req, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        String shortUrl = createResp.getBody().getShortUrl();
        Long id = getIdByShortUrl(shortUrl, headers);
        UpdateUrlDataRequest updateReq = new UpdateUrlDataRequest();
        updateReq.setId(id);
        updateReq.setLongUrl("https://update-long.com/changed");
        ResponseEntity<UrlResponse> updateResp = restTemplate.exchange(
                baseUrl + "/urlData", org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateReq, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.OK, updateResp.getStatusCode());
        assertEquals("https://update-long.com/changed", updateResp.getBody().getLongUrl());
    }

    @Test
    public void testUpdateShortCode() {
        String apiKey = "a1b2c3d4e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest req = new UrlRequest();
        req.setLongUrl("https://update-shortcode.com");
        ResponseEntity<UrlResponse> createResp = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(req, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        String shortUrl = createResp.getBody().getShortUrl();
        Long id = getIdByShortUrl(shortUrl, headers);
        UpdateUrlDataRequest updateReq = new UpdateUrlDataRequest();
        updateReq.setId(id);
        updateReq.setLongUrl("https://update-shortcode.com");
        updateReq.setShortCode("1eTzzz");
        ResponseEntity<UrlResponse> updateResp = restTemplate.exchange(
                baseUrl + "/urlData", org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateReq, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.OK, updateResp.getStatusCode());
        assertEquals("1eTzzz", updateResp.getBody().getShortUrl());
    }

    @Test
    public void testUpdateExpiryDate() {
        String apiKey = "a1b2c3d4e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest req = new UrlRequest();
        req.setLongUrl("https://update-expiry.com");
        ResponseEntity<UrlResponse> createResp = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(req, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        String shortUrl = createResp.getBody().getShortUrl();
        Long id = getIdByShortUrl(shortUrl, headers);
        UpdateUrlDataRequest updateReq = new UpdateUrlDataRequest();
        updateReq.setId(id);
        updateReq.setLongUrl("https://update-expiry.com");
        updateReq.setExpiryDate(java.time.Instant.parse("2026-01-01T00:00:00Z"));
        ResponseEntity<UrlResponse> updateResp = restTemplate.exchange(
                baseUrl + "/urlData", org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateReq, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.OK, updateResp.getStatusCode());
        assertEquals("2026-01-01T00:00:00Z", updateResp.getBody().getExpiryDate().toString());
    }

    @Test
    public void testUpdateActiveStatus() {
        String apiKey = "a1b2c3d4e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest req = new UrlRequest();
        req.setLongUrl("https://update-active.com");
        ResponseEntity<UrlResponse> createResp = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(req, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        String shortUrl = createResp.getBody().getShortUrl();
        Long id = getIdByShortUrl(shortUrl, headers);
        UpdateUrlDataRequest updateReq = new UpdateUrlDataRequest();
        updateReq.setId(id);
        updateReq.setLongUrl("https://update-active.com");
        updateReq.setActive(false);
        ResponseEntity<UrlResponse> updateResp = restTemplate.exchange(
                baseUrl + "/urlData", org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateReq, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.OK, updateResp.getStatusCode());
        assertFalse(updateResp.getBody().getSuccess()); // Assuming success=false means inactive
    }

    @Test
    public void testUnauthorizedUpdate() {
        String apiKey = "a1b2c3d4e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest req = new UrlRequest();
        req.setLongUrl("https://unauth-update.com");
        ResponseEntity<UrlResponse> createResp = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(req, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        String shortUrl = createResp.getBody().getShortUrl();
        Long id = getIdByShortUrl(shortUrl, headers);
        UpdateUrlDataRequest updateReq = new UpdateUrlDataRequest();
        updateReq.setId(id);
        updateReq.setLongUrl("https://unauth-update.com/changed");
        ResponseEntity<String> unauthResp = restTemplate.exchange(
                baseUrl + "/urlData", org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateReq), String.class
        );
        assertEquals(HttpStatus.UNAUTHORIZED, unauthResp.getStatusCode());
    }

    @Test
    public void testInvalidShortCode() {
        String apiKey = "a1b2c3d4e5";
        HttpHeaders headers = new HttpHeaders();
        headers.set("api_key", apiKey);
        UrlRequest req = new UrlRequest();
        req.setLongUrl("https://invalid-shortcode.com");
        ResponseEntity<UrlResponse> createResp = restTemplate.postForEntity(
                baseUrl + "/shorten", new org.springframework.http.HttpEntity<>(req, headers), UrlResponse.class
        );
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        String shortUrl = createResp.getBody().getShortUrl();
        Long id = getIdByShortUrl(shortUrl, headers);
        UpdateUrlDataRequest updateReq = new UpdateUrlDataRequest();
        updateReq.setId(id);
        updateReq.setLongUrl("https://invalid-shortcode.com");
        updateReq.setShortCode("invalid code!"); // Invalid short code
        ResponseEntity<String> invalidResp = restTemplate.exchange(
                baseUrl + "/urlData", org.springframework.http.HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(updateReq, headers), String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, invalidResp.getStatusCode());
    }

    // Helper method to get ID by shortUrl
    private Long getIdByShortUrl(String shortUrl, HttpHeaders headers) {
        ResponseEntity<UrlDataDTO[]> allResp = restTemplate.exchange(
                baseUrl + "/allUrls", org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(headers), UrlDataDTO[].class
        );
        assertEquals(HttpStatus.OK, allResp.getStatusCode());
        for (UrlDataDTO dto : allResp.getBody()) {
            if (dto.getShortUrl().equals(shortUrl)) {
                return dto.getId();
            }
        }
        fail("ID not found for shortUrl: " + shortUrl);
        return null;
    }
}
