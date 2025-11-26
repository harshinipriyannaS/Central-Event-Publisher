package com.example.main.core;


import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.main.config.OutboxProperties;
@Component
public class OutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    private final GenericOutboxRepository repository;
    private final OutboxMessagePublisher publisher;
    private final OutboxProperties props;

    public OutboxProcessor(GenericOutboxRepository repository,
                           OutboxMessagePublisher publisher,
                           OutboxProperties props) {
        this.repository = repository;
        this.publisher = publisher;
        this.props = props;
    }

    @Scheduled(fixedDelayString = "${outbox.polling-interval-ms:3000}")
    public void processOnce() {
        try {
            OutboxRecord rec = repository.fetchNextPending(props.getPendingStatus());
            if (rec == null) return;

            log.info("Got outbox id={} payload={}", rec.getId(), rec.getPayload());
            publisher.publish(rec.getPayload());
            repository.updateStatus(rec.getId(), props.getSentStatus());
            log.info("Marked outbox id={} as {}", rec.getId(), props.getSentStatus());
        } catch (Exception e) {
            log.error("Outbox processing error", e);
            // keep as PENDING; we'll retry later
        }
    }
}
