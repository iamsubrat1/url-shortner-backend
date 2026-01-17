package com.urlshortener.urlshortenerbackend.config;

import com.urlshortener.urlshortenerbackend.interceptor.RateLimitingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration to register interceptors.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitingInterceptor rateLimitingInterceptor;

    public WebMvcConfig(RateLimitingInterceptor rateLimitingInterceptor) {
        this.rateLimitingInterceptor = rateLimitingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor)
                // Apply to all URL shortener APIs
                .addPathPatterns("/api/v1/urls/**")
                // Exclude non-business endpoints
                .excludePathPatterns("/actuator/**", "/health");
    }
}

