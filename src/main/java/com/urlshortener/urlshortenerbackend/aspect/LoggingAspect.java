package com.urlshortener.urlshortenerbackend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    private long startTime;

    @Before(value = "execution(* com.urlshortener.urlshortenerbackend.controller..*(..)) || " +
            "execution(* com.urlshortener.urlshortenerbackend.service..*(..))")
    public void logMethodStart(final JoinPoint joinPoint) {
        startTime = System.currentTimeMillis();
        log.debug("Starting method: {}", joinPoint.getSignature().getName());
    }

    @After(value = "execution(* com.urlshortener.urlshortenerbackend.controller.*.*(..)) || " +
            "execution(* com.urlshortener.urlshortenerbackend.service.*.*(..))")
    public void logMethodEnd(final JoinPoint joinPoint) {
        long endTime = System.currentTimeMillis();
        log.debug("Finished method: {}. Execution time: {} ms", joinPoint.getSignature().getName(), (endTime - startTime));
    }
}
