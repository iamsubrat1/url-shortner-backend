package com.urlshortener.urlshortenerbackend.ratelimiter;

import com.urlshortener.urlshortenerbackend.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class DistributedRateLimiter {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> rateLimiterScript;
    private final RateLimitProperties properties;

    public void validateRateLimit(String clientKey) {

        String redisKey = "rate_limit:" + clientKey;

        Long result = redisTemplate.execute(
                rateLimiterScript,
                Collections.singletonList(redisKey),
                String.valueOf(properties.getCapacity()),
                String.valueOf(properties.getWindowSeconds()),
                String.valueOf(Instant.now().getEpochSecond())
        );

        if (result == null || result == 0L) {
            throw new RateLimitExceededException(
                    "Too many requests. Please try again later."
            );
        }
    }
}