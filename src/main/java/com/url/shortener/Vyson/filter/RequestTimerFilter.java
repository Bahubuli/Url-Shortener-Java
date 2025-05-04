package com.url.shortener.Vyson.filter; // *** Adjust package to match your project ***

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// --- Choose imports based on your Spring Boot version ---
// For Spring Boot 3.0 or newer:
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;


import java.io.IOException;

@Component // Make this a Spring bean so it can be injected and managed
// @Order is not strictly needed here if using FilterRegistrationBean,
// but can be kept for clarity if you have other filters managed by @Component.
// Give it a high order number so it runs AFTER other filters like logging, blacklist, auth.
@Order(10) // Example order, adjust based on your chain. Higher number = runs later.
public class RequestTimerFilter implements Filter {

    // Logger for this filter
    private static final Logger logger = LoggerFactory.getLogger(RequestTimerFilter.class);

    // Define the name for the custom response header
    private static final String RESPONSE_TIME_HEADER = "X-Response-Time-Millis";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Cast the generic request and response to HTTP types
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        ContentCachingResponseWrapper wrapped = new ContentCachingResponseWrapper(httpResponse);


        // --- 1. Capture the start time (using nanoseconds for precision) ---
        long startTime = System.nanoTime();
        // Log the start of the filter processing (using DEBUG level)
        logger.debug("RequestTimerFilter started for {}", httpRequest.getRequestURI());

        try {
            // --- 2. Allow the request to proceed through the rest of the chain ---
            // This is the critical call that passes the request to the next filter
            // or the target endpoint/controller. The code after this line
            // will execute only after the entire downstream processing is complete.
            chain.doFilter(request, wrapped);
        } finally {
            // --- 3. Capture the end time (this code runs after chain.doFilter() returns) ---
            long endTime = System.nanoTime();
            // Log the end of the filter processing (using DEBUG level)
            logger.debug("RequestTimerFilter finished for {}", httpRequest.getRequestURI());

            long durationMillis = (System.nanoTime() - startTime) / 1_000_000;
            wrapped.setHeader(RESPONSE_TIME_HEADER, String.valueOf(durationMillis));
            wrapped.copyBodyToResponse();

            // Log the final duration (using DEBUG level)
            logger.debug("Request for {} took {} ms", httpRequest.getRequestURI(), durationMillis);
        }

        // Note: No further chain.doFilter() call here. The request has already gone
        // through the chain in the try block. The finally block is just for post-processing.
    }

    // Standard Filter lifecycle method - called once on filter initialization
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("RequestTimerFilter initialized");
    }

    // Standard Filter lifecycle method - called once on filter destruction
    @Override
    public void destroy() {
        logger.info("RequestTimerFilter destroyed");
    }
}
