package com.url.shortener.Vyson.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UrlControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private String baseUrl;

	@BeforeEach
	public void setUp() {
		baseUrl = "http://localhost:" + port;
		// Set custom request factory to disable automatic redirects
		restTemplate.getRestTemplate().setRequestFactory(new NonRedirectingClientHttpRequestFactory());
	}

	/**
	 * Tests that a valid long URL is successfully shortened.
	 */
	@Test
	public void testShortenUrl() {
		String longUrl = "https://example.com";
		Map<String, String> requestBody = Map.of("longUrl", longUrl);

		ResponseEntity<String> response = restTemplate.postForEntity(
				baseUrl + "/shorten", requestBody, String.class
		);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		String shortCode = response.getBody();
		assertNotNull(shortCode, "Expected a non-null short code in the response.");
	}

	/**
	 * Tests that a valid short code correctly redirects to the original URL.
	 */
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

		// Now the response should be the raw 302 (or FOUND) with the Location header intact.
		assertEquals(HttpStatus.FOUND, redirectResponse.getStatusCode());
		assertNotNull(redirectResponse.getHeaders().getLocation(), "Location header should be present.");
		assertEquals(longUrl, redirectResponse.getHeaders().getLocation().toString());
	}

	/**
	 * Tests that a missing 'longUrl' in the request results in a bad request.
	 * Adjust the expected status if your controller returns a different status code.
	 */
	@Test
	public void testShortenUrlMissingLongUrl() {
		Map<String, String> requestBody = Map.of(); // Empty map, no longUrl provided

		ResponseEntity<String> response = restTemplate.postForEntity(
				baseUrl + "/shorten", requestBody, String.class
		);

		// Assuming the controller returns a 400 Bad Request for missing parameters.
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	/**
	 * Tests that a malformed URL is handled appropriately.
	 * Depending on your controller logic, this may return BAD_REQUEST or another error.
	 */
	@Test
	public void testShortenUrlWithMalformedUrl() {
		String malformedUrl = "htp:/bad_url";
		Map<String, String> requestBody = Map.of("longUrl", malformedUrl);

		ResponseEntity<String> response = restTemplate.postForEntity(
				baseUrl + "/shorten", requestBody, String.class
		);

		// Assuming the controller returns a 400 Bad Request for a malformed URL.
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	/**
	 * Tests that a redirect is not performed for a non-existent short code.
	 * This should return an error status, e.g., NOT_FOUND.
	 */
	@Test
	public void testRedirectNonExistentShortCode() {
		String nonExistentShortCode = "abcdef"; // assuming this code does not exist

		ResponseEntity<Void> response = restTemplate.getForEntity(
				baseUrl + "/redirect?code=" + nonExistentShortCode, Void.class
		);

		// Assuming the controller returns 404 NOT_FOUND when the short code is not found.
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
}
