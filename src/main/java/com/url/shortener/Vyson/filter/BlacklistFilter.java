package com.url.shortener.Vyson.filter; // Adjust package to match your project

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled; // Import Scheduled
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct; // Import PostConstruct
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component // Make this a Spring bean
// No @RefreshScope needed for this file-based dynamic approach
@Order(0) // Keep the order low to run early
public class BlacklistFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(BlacklistFilter.class);

    // Inject the path to the blacklist file
    @Value("${app.security.api-key.blacklist-file}")
    private String blacklistFilePath;

    // Inject the interval for checking the file
    @Value("${app.security.api-key.blacklist-check-interval:60000}") // Default to 60s if not set
    private long checkInterval; // Interval in milliseconds

    @Autowired
    private ResourceLoader resourceLoader;

    private Resource blacklistResource;

    // Use a volatile Set to hold the blacklist.
    // volatile ensures thread-safe updates of the reference.
    private volatile Set<String> blacklistedApiKeys = Collections.emptySet(); // Initialize with empty set

    private FileTime lastModifiedTime; // To track file changes

    // PostConstruct runs after injection, for initial setup and loading
    @PostConstruct
    public void init() {
        // Support both classpath: and file: prefix
        if (blacklistFilePath.startsWith("classpath:")) {
            blacklistResource = resourceLoader.getResource(blacklistFilePath);
        } else if (blacklistFilePath.startsWith("file:")) {
            blacklistResource = resourceLoader.getResource(blacklistFilePath);
        } else {
            // Default to classpath if not specified
            blacklistResource = resourceLoader.getResource("classpath:" + blacklistFilePath);
        }
        // Initial load of the blacklist
        loadBlacklist();
        try {
            if (blacklistResource.exists()) {
                this.lastModifiedTime = FileTime.fromMillis(blacklistResource.lastModified());
                logger.info("Monitoring blacklist file for changes: {}", blacklistFilePath);
            } else {
                logger.warn("Blacklist file not found at startup: {}. Will monitor if created.", blacklistFilePath);
                this.lastModifiedTime = null;
            }
        } catch (IOException e) {
            logger.error("Error getting initial last modified time for blacklist file: {}", blacklistFilePath, e);
            this.lastModifiedTime = null;
        }
    }

    /**
     * Scheduled task to check the blacklist file for changes.
     * This method runs periodically based on the configured interval.
     */
    @Scheduled(fixedRateString = "${app.security.api-key.blacklist-check-interval:60000}")
    public void monitorAndReloadBlacklistFile() {
        // Always reload the blacklist file, regardless of lastModifiedTime
        logger.debug("Forcing reload of blacklist file...");
        loadBlacklist();
        try {
            if (blacklistResource.exists()) {
                this.lastModifiedTime = java.nio.file.attribute.FileTime.fromMillis(blacklistResource.lastModified());
            } else {
                this.lastModifiedTime = null;
            }
        } catch (IOException e) {
            logger.error("Error updating last modified time for blacklist file: {}", blacklistFilePath, e);
            this.lastModifiedTime = null;
        }
    }

    /**
     * Loads the blacklist from the configured file.
     * This method is called on startup and by the scheduled monitor.
     */
    private void loadBlacklist() {
        Set<String> newBlacklistedApiKeys = new HashSet<>();
        try {
            if (blacklistResource.exists()) {
                String content = new String(blacklistResource.getInputStream().readAllBytes());
                if (!content.trim().isEmpty()) {
                    newBlacklistedApiKeys = Arrays.stream(content.split(","))
                            .map(String::trim)
                            .filter(key -> !key.isEmpty())
                            .collect(Collectors.toSet());
                }
                this.blacklistedApiKeys = Collections.unmodifiableSet(newBlacklistedApiKeys);
                logger.info("Successfully loaded blacklist from {}. {} keys blacklisted.",
                        blacklistFilePath, blacklistedApiKeys.size());
            } else {
                logger.warn("Blacklist file not found: {}. No keys blacklisted.", blacklistFilePath);
                this.blacklistedApiKeys = Collections.emptySet();
            }
        } catch (IOException e) {
            logger.error("Failed to read blacklist file: {}", blacklistFilePath, e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String apiKey = httpRequest.getHeader("api_key");

        // 1. Check if the incoming API key is in the blacklist using the volatile Set
        // Only check if apiKey is not null/empty
        if (apiKey != null && !apiKey.trim().isEmpty() && this.blacklistedApiKeys.contains(apiKey.trim())) {
            logger.warn("Blocked request with blacklisted API Key: {} to {}", apiKey, httpRequest.getRequestURI());
            sendErrorResponse(httpResponse, HttpStatus.FORBIDDEN, "Your API key has been blocked.");
            return; // Stop processing
        }

        // 2. If the API key is not blacklisted, continue the filter chain.
        chain.doFilter(request, response);
    }

    // Helper method to send an error response
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        String jsonError = String.format("{\"status\":%d, \"error\":\"%s\", \"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), message);
        response.getWriter().write(jsonError);
        response.getWriter().flush();
    }

    // Keep the standard destroy method if needed for cleanup
    @Override
    public void destroy() {
        logger.info("BlacklistFilter destroyed");
    }
}
