package com.urlshortener.urlshortenerbackend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Rate limiting interceptor to protect APIs from abuse.
 * Uses a sliding window algorithm per client IP.
 */
@Component
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor {

    /**
     * Maximum number of requests allowed per IP per minute
     */
    private static final int MAX_REQUESTS_PER_MINUTE = 5;

    /**
     * Sliding window size (1 minute)
     */
    private static final long WINDOW_DURATION_MILLIS = 60_000;

    /**
     * Stores request timestamps per client IP.
     * Key   -> client IP
     * Value -> timestamps of requests within sliding window
     */
    private final ConcurrentHashMap<String, Queue<Long>> requestLog = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String clientIp = resolveClientIp(request);
        long currentTime = Instant.now().toEpochMilli();

        Queue<Long> timestamps =
                requestLog.computeIfAbsent(clientIp, ip -> new ConcurrentLinkedQueue<>());

        // Remove requests outside the sliding window
        cleanupOldRequests(timestamps, currentTime);

        if (timestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            sendRateLimitResponse(response);
            return false;
        }

        timestamps.offer(currentTime);
        return true;
    }

    /**
     * Removes timestamps that are older than the sliding window duration.
     */
    private void cleanupOldRequests(Queue<Long> timestamps, long currentTime) {
        while (!timestamps.isEmpty()
                && currentTime - timestamps.peek() > WINDOW_DURATION_MILLIS) {
            timestamps.poll();
        }
    }

    /**
     * Sends HTTP 429 response when rate limit is exceeded.
     */
    private void sendRateLimitResponse(HttpServletResponse response) throws Exception {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"error\":\"Too many requests. Please try again later.\"}"
        );
    }

    /**
     * Resolves client IP address.
     * Handles scenarios where the application is behind a proxy or load balancer.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
