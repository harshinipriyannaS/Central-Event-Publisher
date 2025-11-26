package com.company.eventpublisher.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.company.eventpublisher.repository.OutboxRepository;
import com.company.eventpublisher.repository.OutboxRepositoryImpl;

@Configuration
@ConditionalOnClass(JdbcTemplate.class)
@ConditionalOnProperty(name = "event.publisher.database-type", havingValue = "JDBC", matchIfMissing = true)
public class JdbcRepositoryConfig {

    private static final Logger log = LoggerFactory.getLogger(JdbcRepositoryConfig.class);

    @Bean
    public OutboxRepository jdbcOutboxRepository(JdbcTemplate jdbcTemplate, EventPublisherProperties properties) {
        log.info("Using JDBC Outbox Repository for PostgreSQL/MySQL");
        return new OutboxRepositoryImpl(jdbcTemplate, properties);
    }
}
