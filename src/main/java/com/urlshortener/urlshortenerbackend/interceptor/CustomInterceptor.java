package com.urlshortener.urlshortenerbackend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class CustomInterceptor implements HandlerInterceptor {
    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private static final long ONE_MINUTE_IN_MILLIS = 60_000;
    private static final Map<String, Queue<Long>> requestTimestamps = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIP = getClientIP(request);
        long currentTime = Instant.now().toEpochMilli();

        Queue<Long> timestamps = requestTimestamps.computeIfAbsent(clientIP, k -> new ConcurrentLinkedQueue<>());

        // Remove timestamps older than 1 minute
        while (!timestamps.isEmpty() && currentTime - timestamps.peek() > ONE_MINUTE_IN_MILLIS) {
            timestamps.poll();
        }

        if (timestamps.size() >= MAX_REQUESTS_PER_MINUTE) {
            log.warn("Rate limit exceeded for IP: {}", clientIP);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            return false;
        }

        timestamps.offer(currentTime);
        return true;
    }

    //This logic can be improvised, if the client is behind Proxy or Load Balancer then it will not work as expected.
    private String getClientIP(HttpServletRequest request) {
        String clientIP = request.getRemoteAddr();
        return clientIP;
    }
}
