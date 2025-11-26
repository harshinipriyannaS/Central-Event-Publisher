package com.company.eventpublisher.config;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;

@Configuration
public class ResilienceConfig {

    @Bean
    public Retry eventPublisherRetry(EventPublisherProperties properties) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(properties.getRetry().getMaxAttempts())
                .waitDuration(Duration.ofMillis(properties.getRetry().getBackoffMs()))
                .build();

        return Retry.of("eventPublisher", config);
    }

    @Bean
    public CircuitBreaker eventPublisherCircuitBreaker(EventPublisherProperties properties) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(properties.getCircuitBreaker().getFailureThreshold())
                .waitDurationInOpenState(Duration.ofSeconds(properties.getCircuitBreaker().getWaitDurationSeconds()))
                .slidingWindowSize(10)
                .build();

        return CircuitBreaker.of("eventPublisher", config);
    }
}
