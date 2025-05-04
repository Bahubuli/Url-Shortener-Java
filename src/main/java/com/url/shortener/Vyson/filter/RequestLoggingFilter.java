package com.url.shortener.Vyson.filter;

import com.url.shortener.Vyson.modal.RequestLog;
import com.url.shortener.Vyson.repo.RequestLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*; // The core Filter API imports
import jakarta.servlet.http.HttpServletRequest; // Specific HTTP request type


import java.io.IOException;
import java.time.Instant; // For the timestamp

@Component // <--- Tells Spring to manage this as a bean and register it as a Filter
@Order(4) // <--- Gives this Filter a high priority (runs early in the chain)
public class RequestLoggingFilter implements Filter { // <--- Implements the Filter interface

    // --- Standard Logging Setup ---
    // We use SLF4J (a logging facade) and Logback (the default implementation in Spring Boot)
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Autowired
    private RequestLogRepository requestLogRepository;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // --- Extracting Request Details ---
        // ServletRequest is generic, cast to HttpServletRequest for HTTP details
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 1. Timestamp: Get current time
        Instant timestamp = Instant.now();

        // 2. HTTP method and URL: Get method (GET, POST, etc.) and the path/query
        String method = httpRequest.getMethod();
        String requestURI = httpRequest.getRequestURI(); // e.g., /shorten
        String queryString = httpRequest.getQueryString(); // e.g., url=...
        String url = requestURI + (queryString != null ? "?" + queryString : ""); // Combine

        // 3. User-Agent: Get the header sent by the client browser/app
        String userAgent = httpRequest.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            userAgent = "Unknown";
        }

        // 4. IP Address: Get the IP address of the client (may be proxy)
        String ipAddress = httpRequest.getRemoteAddr();


        // --- Logging the Details ---
        // Use the standard logger to output the information
        logger.info("Request Received - Timestamp: {}, Method: {}, URL: {}, User-Agent: {}, IP: {}",
                timestamp, method, url, userAgent, ipAddress);


        RequestLog logEntry = new RequestLog(timestamp, method, url, userAgent, ipAddress);
        try {
            // This is the line from the DATABASE logging example

            requestLogRepository.save(logEntry);
        } catch (Exception e) {
            // ... error handling ...
        }

        // --- Passing the Request Along ---
        // This is the CRUCIAL step! It tells the container to continue the request processing.
        // It passes the request and response objects to the *next* item in the FilterChain,
        // which could be another filter or, eventually, the Spring DispatcherServlet
        // that routes the request to your controller.
        chain.doFilter(request, response);

        // --- Optional: Logging AFTER the request is processed ---
        // Code here would run *after* the controller has finished and generated a response.
        // You could log response status, duration, etc. (requires casting response to HttpServletResponse)
        // HttpServletResponse httpResponse = (HttpServletResponse) response;
        // logger.info("Request Processed - Status: {}", httpResponse.getStatus());
    }

    // Init and Destroy are often empty unless specific setup/cleanup is needed
    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }
}