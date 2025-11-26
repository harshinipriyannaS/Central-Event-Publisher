package com.company.eventpublisher.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.company.eventpublisher.repository.MongoOutboxRepository;
import com.company.eventpublisher.repository.OutboxRepository;

@Configuration
@ConditionalOnClass(MongoTemplate.class)
@ConditionalOnProperty(name = "event.publisher.database-type", havingValue = "MONGODB")
public class MongoRepositoryConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoRepositoryConfig.class);

    @Bean
    public OutboxRepository mongoOutboxRepository(MongoTemplate mongoTemplate, EventPublisherProperties properties) {
        log.info("Using MongoDB Outbox Repository");
        return new MongoOutboxRepository(mongoTemplate, properties);
    }
}
