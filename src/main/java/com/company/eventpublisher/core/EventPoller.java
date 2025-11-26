package com.company.eventpublisher.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.company.eventpublisher.config.EventPublisherProperties;
import com.company.eventpublisher.model.OutboxEvent;
import com.company.eventpublisher.repository.OutboxRepository;

public class EventPoller {

    private static final Logger log = LoggerFactory.getLogger(EventPoller.class);

    private final OutboxRepository repository;
    private final EventPublisherService publisherService;
    private final EventPublisherProperties properties;
    
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    private final AtomicInteger emptyPollCount = new AtomicInteger(0);

    public EventPoller(
            OutboxRepository repository,
            EventPublisherService publisherService,
            EventPublisherProperties properties) {
        this.repository = repository;
        this.publisherService = publisherService;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${event.publisher.polling-interval-ms:1000}")
    public void pollAndPublish() {
        if (isShuttingDown.get()) {
            log.info("Skipping poll - shutdown in progress");
            return;
        }


        if (properties.isAdaptivePolling() && emptyPollCount.get() > 5) {
            try {
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        isProcessing.set(true);
        try {
            List<OutboxEvent> events = repository.fetchNewEvents(properties.getBatchSize());

            if (!events.isEmpty()) {
                log.info("Polled {} new event(s) from outbox", events.size());
                emptyPollCount.set(0);

                
                for (OutboxEvent event : events) {
                    if (isShuttingDown.get()) {
                        log.warn("Shutdown");
                        break;
                    }
                    publisherService.processEvent(event);
                }
            } else {
                emptyPollCount.incrementAndGet();
            }

        } catch (Exception e) {
            log.error("Error in polling cycle", e);
        } finally {
            isProcessing.set(false);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Graceful shutdown");
        isShuttingDown.set(true);
        
    
        int waitCount = 0;
        while (isProcessing.get() && waitCount < 30) {
            try {
                Thread.sleep(1000);
                waitCount++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
    }
}
