package com.example.main.core;


import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.main.config.OutboxProperties;
@Component
public class OutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

<<<<<<< HEAD
    private final GenericOutboxRepository repository;
    private final OutboxMessagePublisher publisher;
    private final OutboxProperties props;

    public OutboxProcessor(GenericOutboxRepository repository,
                           OutboxMessagePublisher publisher,
                           OutboxProperties props) {
        this.repository = repository;
=======
    private final GenericOutboxRepository jdbcRepository;
    private final MongoOutboxRepository mongoRepository;
    private final OutboxMessagePublisher publisher;
    private final OutboxProperties props;

    public OutboxProcessor(GenericOutboxRepository jdbcRepository,
                           MongoOutboxRepository mongoRepository,
                           OutboxMessagePublisher publisher,
                           OutboxProperties props) {
        this.jdbcRepository = jdbcRepository;
        this.mongoRepository = mongoRepository;
>>>>>>> 3c4d80f (DB changes)
        this.publisher = publisher;
        this.props = props;
    }

    @Scheduled(fixedDelayString = "${outbox.polling-interval-ms:3000}")
    public void processOnce() {
        try {
<<<<<<< HEAD
            OutboxRecord rec = repository.fetchNextPending(props.getPendingStatus());
=======
            OutboxRecord rec = null;
            
            // Use the appropriate repository based on database type
            if (props.getDatabaseType() == OutboxProperties.DatabaseType.MONGODB && mongoRepository != null) {
                rec = mongoRepository.fetchNextPending(props.getPendingStatus());
            } else if (jdbcRepository != null) {
                rec = jdbcRepository.fetchNextPending(props.getPendingStatus());
            }
            
>>>>>>> 3c4d80f (DB changes)
            if (rec == null) return;

            log.info("Got outbox id={} payload={}", rec.getId(), rec.getPayload());
            publisher.publish(rec.getPayload());
<<<<<<< HEAD
            repository.updateStatus(rec.getId(), props.getSentStatus());
=======
            
            // Update status using the appropriate repository
            if (props.getDatabaseType() == OutboxProperties.DatabaseType.MONGODB && mongoRepository != null) {
                mongoRepository.updateStatus(rec.getId(), props.getSentStatus());
            } else if (jdbcRepository != null) {
                jdbcRepository.updateStatus(rec.getId(), props.getSentStatus());
            }
            
>>>>>>> 3c4d80f (DB changes)
            log.info("Marked outbox id={} as {}", rec.getId(), props.getSentStatus());
        } catch (Exception e) {
            log.error("Outbox processing error", e);
            // keep as PENDING; we'll retry later
        }
    }
}
