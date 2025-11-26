package com.company.eventpublisher.repository;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.company.eventpublisher.config.EventPublisherProperties;
import com.company.eventpublisher.model.EventStatus;
import com.company.eventpublisher.model.OutboxEvent;

public class MongoOutboxRepository implements OutboxRepository {

    private static final Logger log = LoggerFactory.getLogger(MongoOutboxRepository.class);

    private final MongoTemplate mongoTemplate;
    private final EventPublisherProperties properties;

    public MongoOutboxRepository(MongoTemplate mongoTemplate, EventPublisherProperties properties) {
        this.mongoTemplate = mongoTemplate;
        this.properties = properties;
    }

    @Override
    public List<OutboxEvent> fetchNewEvents(int batchSize) {
        Query query = new Query()
                .addCriteria(Criteria.where(properties.getStatusColumn()).is(EventStatus.NEW.name()))
                .addCriteria(Criteria.where("retryCount").lt(properties.getMaxRetries()))
                .with(Sort.by(Sort.Direction.ASC, "createdAt"))
                .limit(batchSize);

        return mongoTemplate.find(query, OutboxEvent.class, properties.getOutboxTable());
    }

    @Override
    public void markAsPublished(Object eventId) {
    
        Query query = new Query(Criteria.where("_id").is(eventId));
        Update update = new Update()
                .set(properties.getStatusColumn(), EventStatus.PUBLISHED.name())
                .set("publishedAt", Instant.now());

        mongoTemplate.updateFirst(query, update, properties.getOutboxTable());
        log.debug("Marked event {} as PUBLISHED in MongoDB", eventId);
    }

    @Override
    public void incrementRetryCount(Object eventId) {
        Query query = new Query(Criteria.where("_id").is(eventId));
        Update update = new Update()
                .inc("retryCount", 1)
                .set("lastRetryAt", Instant.now());

        mongoTemplate.updateFirst(query, update, properties.getOutboxTable());
        log.debug("Incremented retry count for event {}", eventId);
    }

    @Override
    public void markAsFailed(Object eventId, String errorMessage) {
        Query query = new Query(Criteria.where("_id").is(eventId));
        Update update = new Update()
                .set(properties.getStatusColumn(), EventStatus.FAILED.name())
                .set("errorMessage", errorMessage)
                .set("failedAt", Instant.now());

        mongoTemplate.updateFirst(query, update, properties.getOutboxTable());
        log.warn("Marked event {} as FAILED in MongoDB", eventId);
    }
}
