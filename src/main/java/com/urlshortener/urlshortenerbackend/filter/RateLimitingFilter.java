package com.urlshortener.urlshortenerbackend.filter;

import com.urlshortener.urlshortenerbackend.exception.RateLimitExceededException;
import com.urlshortener.urlshortenerbackend.ratelimiter.DistributedRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

            String userKey = resolveUserKey();
            distributedRateLimiter.validateRateLimit(userKey);

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

    private String resolveUserKey() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authentication required for rate limiting");
        }

        String username = authentication.getName();

        return "user:" + username;
    }


}
