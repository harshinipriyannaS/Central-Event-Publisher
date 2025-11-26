package com.company.eventpublisher.core;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.company.eventpublisher.config.EventPublisherProperties;
import com.company.eventpublisher.exception.PublishException;
import com.company.eventpublisher.model.OutboxEvent;
import com.company.eventpublisher.repository.OutboxRepository;
import com.company.eventpublisher.sender.JmsSender;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;

public class EventPublisherService {

    private static final Logger log = LoggerFactory.getLogger(EventPublisherService.class);

    private final JmsSender jmsSender;
    private final OutboxRepository repository;
    private final EventPublisherProperties properties;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public EventPublisherService(
            JmsSender jmsSender,
            OutboxRepository repository,
            EventPublisherProperties properties,
            Retry retry,
            CircuitBreaker circuitBreaker) {
        this.jmsSender = jmsSender;
        this.repository = repository;
        this.properties = properties;
        this.retry = retry;
        this.circuitBreaker = circuitBreaker;
    }

    public void processEvent(OutboxEvent event) {
        try {
            
            Supplier<Void> supplier = () -> {
                publishEvent(event);
                return null;
            };

            
            Supplier<Void> decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker,
                    Retry.decorateSupplier(retry, supplier));

            decoratedSupplier.get();

        } catch (Exception e) {
            log.error("Failed to publish event {} after retries", event.getId(), e);
            
           
            repository.incrementRetryCount(event.getId());
            
            
            if (event.getRetryCount() + 1 >= properties.getMaxRetries()) {
                log.error("Event {} exceeded max retries ({}). Marking as FAILED.", 
                    event.getId(), properties.getMaxRetries());
                repository.markAsFailed(event.getId(), e.getMessage());
            }
        }
    }

    private void publishEvent(OutboxEvent event) {
        String queueName = properties.getQueueMapping().get(event.getEventType());

        if (queueName == null || queueName.isEmpty()) {
            log.error("No queue mapping found for event type: {}", event.getEventType());
            throw new PublishException("No queue mapping for event type: " + event.getEventType());
        }

        log.info("Publishing event {} of type {} to queue {}", event.getId(), event.getEventType(), queueName);

        boolean success = jmsSender.send(queueName, event.getPayload());

        if (success) {
            repository.markAsPublished(event.getId());
            log.info("Successfully published event {} - Status updated to PUBLISHED", event.getId());
        } else {
            throw new PublishException("Failed to send message to MQ for event: " + event.getId());
        }
    }
}
