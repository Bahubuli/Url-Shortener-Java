package com.url.shortener.Vyson.filter; // Adjust package to match your project

import com.url.shortener.Vyson.modal.User; // Assuming your User entity is here
import com.url.shortener.Vyson.repo.UserRepository; // Assuming your UserRepository is here

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
// import org.springframework.web.server.ResponseStatusException; // No longer needed if handling response directly

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@Component // Make this a Spring bean
// @Order is not strictly needed here if using FilterRegistrationBean,
// but can be kept for clarity if you have other filters managed by @Component
@Order(3) // Give it a higher order than the logging filter (e.g., 2)
public class ApiKeyAuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthFilter.class);

    // Inject the UserRepository to find the user by API key
    private final UserRepository userRepository;

    // Use constructor injection for the repository
    public ApiKeyAuthFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the API key from the header (assuming header name is "api_key")
        String apiKey = httpRequest.getHeader("api_key");

        // --- API Key Presence and Validity Check ---
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.warn("API Key missing in request to {}", httpRequest.getRequestURI());
            sendErrorResponse(httpResponse, HttpStatus.UNAUTHORIZED, "API key is missing");
            return; // Stop processing
        }

        Optional<User> userOptional = userRepository.findByApiKey(apiKey);

        if (!userOptional.isPresent()) {
            logger.warn("Invalid API Key provided: {} for request to {}", apiKey, httpRequest.getRequestURI());
            sendErrorResponse(httpResponse, HttpStatus.UNAUTHORIZED, "Invalid API key");
            return; // Stop processing
        }

        // Get the authenticated User object
        User authenticatedUser = userOptional.get();
        logger.debug("API Key validated successfully for user: {}", authenticatedUser.getName()); // Log user if needed

        // --- Tier Check for Specific Endpoints (like /shorten/batch) ---
        // Check if the request is for the batch endpoint AND the user is 'hobby' tier
        // You need to adjust the path check to match the exact path your FilterRegistrationBean uses for this filter
        // For example, if the FilterRegistrationBean maps this filter to "/shorten/batch",
        // you can check httpRequest.getRequestURI().equals("/shorten/batch")
        // Or, if this filter is mapped to a broader pattern like "/api/*" but you only want the tier check
        // for specific paths, you'd check the URI here.
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.equals("/shorten/batch") && "hobby".equals(authenticatedUser.getTier())) {
            logger.warn("Hobby tier user ({}) attempted batch operation at {}", authenticatedUser.getName(), requestURI);
            sendErrorResponse(httpResponse, HttpStatus.FORBIDDEN, "Please upgrade to business tier in order to perform batch shortening operation");
            return; // Stop processing
        }
        // Add checks for other endpoints/tiers if needed

        // --- If all checks pass ---
        // Store the authenticated User object in the request attributes
        httpRequest.setAttribute("authenticatedUser", authenticatedUser);

        // Continue the filter chain
        chain.doFilter(request, response);

        // Code here would run AFTER the request is processed by the controller
    }

    // Helper method to send an error response
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json"); // Or text/plain, depending on desired response format
        // You might want a more structured error response body in a real application
        String jsonError = String.format("{\"status\":%d, \"error\":\"%s\", \"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), message);
        response.getWriter().write(jsonError);
        response.getWriter().flush();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("ApiKeyAuthFilter initialized");
    }

    @Override
    public void destroy() {
        logger.info("ApiKeyAuthFilter destroyed");
    }
}
