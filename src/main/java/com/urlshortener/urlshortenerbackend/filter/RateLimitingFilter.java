package com.urlshortener.urlshortenerbackend.filter;

import com.urlshortener.urlshortenerbackend.exception.GlobalExceptionHandler;
import com.urlshortener.urlshortenerbackend.exception.RateLimitExceededException;
import com.urlshortener.urlshortenerbackend.ratelimiter.DistributedRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final DistributedRateLimiter distributedRateLimiter;
    private final HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String clientIp = request.getRemoteAddr();
            distributedRateLimiter.validateRateLimit(clientIp);

            filterChain.doFilter(request, response);

        } catch (RateLimitExceededException ex) {

            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    ex
            );
        }
    }

    private String extractClientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
