package com.urlshortener.urlshortenerbackend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Around(
            "execution(* com.urlshortener.urlshortenerbackend.controller..*(..)) || " +
                    "execution(* com.urlshortener.urlshortenerbackend.service..*(..))"
    )
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception ex) {
            log.error(
                    "Exception in {}.{}() with cause = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    ex.getMessage(),
                    ex
            );
            throw ex;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            log.debug(
                    "Executed {}.{}() in {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime
            );
        }
    }
}
