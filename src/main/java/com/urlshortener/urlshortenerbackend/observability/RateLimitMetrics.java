package com.urlshortener.urlshortenerbackend.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class RateLimitMetrics {
    private final Counter rateLimitChecks;
    private final Counter rateLimitExceeded;
    private final Timer redisExecutionTimer;

    public RateLimitMetrics(MeterRegistry meterRegistry) {

        this.rateLimitChecks = Counter.builder("ratelimit.checks.total")
                .description("Total rate limit validations")
                .register(meterRegistry);

        this.rateLimitExceeded = Counter.builder("ratelimit.exceeded.total")
                .description("Total rate limit violations")
                .register(meterRegistry);

        this.redisExecutionTimer = Timer.builder("ratelimit.redis.execution.time")
                .description("Time taken for Redis Lua execution")
                .register(meterRegistry);
    }

    public void incrementChecks() {
        rateLimitChecks.increment();
    }

    public void incrementExceeded() {
        rateLimitExceeded.increment();
    }

    public Timer getRedisExecutionTimer() {
        return redisExecutionTimer;
    }
}
