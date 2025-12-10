package io.github.sushmithashiva04ops.centraleventpublisher.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import io.github.sushmithashiva04ops.centraleventpublisher.config.OutboxProperties.OutboxConfigItem;
import jakarta.annotation.PostConstruct;

public class OutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    private final GenericOutboxRepository repository;
    private final OutboxMessagePublisher publisher;
    private final OutboxConfigItem item;
    private final ThreadPoolTaskScheduler scheduler;

    public OutboxProcessor(
            GenericOutboxRepository repository,
            OutboxMessagePublisher publisher,
            OutboxConfigItem item,
            ThreadPoolTaskScheduler scheduler
    ) {
        this.repository = repository;
        this.publisher = publisher;
        this.item = item;
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void init() {
        start();   // <-- IMPORTANT: auto-start after bean creation
    }

    public void start() {
        // Skip starting if tableName is null/empty
        if (item.getTableName() == null || item.getTableName().isBlank()) {
            log.warn("Skipping OutboxProcessor start → tableName is NULL/EMPTY");
            return;
        }

        scheduler.scheduleWithFixedDelay(
                this::processOnce,
                item.getPollingIntervalMs()
        );

        log.info("[{}] Outbox processor started with polling={}ms",
                item.getTableName(), item.getPollingIntervalMs());
    }


    public void processOnce() {
        if (item.getTableName() == null || item.getTableName().isBlank()) {
            return; // Skip processing
        }

        try {
            OutboxRecord rec = repository.fetchNextPending(item.getPendingStatus());
            if (rec == null) return;

            log.info("[{}] Got outbox id={} payload={}",
                    item.getTableName(), rec.getId(), rec.getPayload());

            // --- Handle empty queue ---
            String queue = item.getQueueNamePublish();
            if (queue == null || queue.isBlank()) {
                log.warn("[{}] queueNamePublish is NULL/EMPTY → marking id={} as SENT (skipped)",
                        item.getTableName(), rec.getId());
                repository.updateStatus(rec.getId(), item.getSentStatus());
                return;
            }

            // Publish
            publisher.publish(rec.getPayload(), queue);
            repository.updateStatus(rec.getId(), item.getSentStatus());

            log.info("[{}] Marked outbox id={} as {}",
                    item.getTableName(), rec.getId(), item.getSentStatus());

        } catch (Exception e) {
            log.error("[{}] Outbox processing error", item.getTableName(), e);
        }
    }

}
