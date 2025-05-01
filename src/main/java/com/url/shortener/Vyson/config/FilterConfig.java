package com.url.shortener.Vyson.config;



import com.url.shortener.Vyson.filter.RequestLoggingFilter; // Import your filter class
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List; // Use List from java.util

@Configuration // This class provides Spring Bean definitions
public class FilterConfig {

    // Inject the filter instance managed by Spring (@Component makes it a bean)
    private final RequestLoggingFilter requestLoggingFilter;

    public FilterConfig(RequestLoggingFilter requestLoggingFilter) {
        this.requestLoggingFilter = requestLoggingFilter;
    }

    @Bean // This method defines a Spring Bean for filter registration
    public FilterRegistrationBean<RequestLoggingFilter> loggingFilterRegistration() {
        FilterRegistrationBean<RequestLoggingFilter> registration = new FilterRegistrationBean<>();

        // 1. Set the filter instance that this registration applies to
        registration.setFilter(requestLoggingFilter);

        // 2. *** Define the URL patterns where this filter should run ***
        // You can list multiple patterns. Ant-style paths are supported.
        List<String> urlPatterns = Arrays.asList(
                "/allUrls/*"     // Apply to all paths under /api/

        );
        registration.setUrlPatterns(urlPatterns);

        // 3. Set the order of the filter (lower number = higher precedence)
        registration.setOrder(1); // Matches the @Order(1) you had on the filter class

        // Optional: Set a name for the filter registration
        // registration.setName("requestLoggingFilter");

        return registration;
    }

    // If you had other filters to register and map differently,
    // you would define additional @Bean methods returning FilterRegistrationBean here.
}