package com.company.eventpublisher.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.company.eventpublisher.config.EventPublisherProperties;
import com.company.eventpublisher.model.EventStatus;
import com.company.eventpublisher.model.OutboxEvent;

public class OutboxRepositoryImpl implements OutboxRepository {

    private static final Logger log = LoggerFactory.getLogger(OutboxRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final EventPublisherProperties properties;

    public OutboxRepositoryImpl(JdbcTemplate jdbcTemplate, EventPublisherProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.properties = properties;
    }

    @Override
    @Transactional
    public List<OutboxEvent> fetchNewEvents(int batchSize) {
        String sql = String.format(
            "SELECT id, event_type, payload, %s as status, created_at, retry_count FROM %s WHERE %s = ? AND retry_count < ? ORDER BY created_at LIMIT ? FOR UPDATE SKIP LOCKED",
            properties.getStatusColumn(),
            properties.getOutboxTable(),
            properties.getStatusColumn()
        );

        return jdbcTemplate.query(sql, 
            new Object[]{EventStatus.NEW.name(), properties.getMaxRetries(), batchSize}, 
            new OutboxEventRowMapper());
    }

    @Override
    @Transactional
    public void markAsPublished(Object eventId) {
        String sql = String.format(
            "UPDATE %s SET %s = ?, published_at = ? WHERE id = ?",
            properties.getOutboxTable(),
            properties.getStatusColumn()
        );

        int updated = jdbcTemplate.update(sql, EventStatus.PUBLISHED.name(), Timestamp.from(Instant.now()), eventId);
        
        if (updated > 0) {
            log.debug("Marked event {} as PUBLISHED", eventId);
        }
    }

    @Override
    @Transactional
    public void incrementRetryCount(Object eventId) {
        String sql = String.format(
            "UPDATE %s SET retry_count = retry_count + 1, last_retry_at = ? WHERE id = ?",
            properties.getOutboxTable()
        );
        
        jdbcTemplate.update(sql, Timestamp.from(Instant.now()), eventId);
        log.debug("Incremented retry count for event {}", eventId);
    }

    @Override
    @Transactional
    public void markAsFailed(Object eventId, String errorMessage) {
        String sql = String.format(
            "UPDATE %s SET %s = ?, error_message = ?, failed_at = ? WHERE id = ?",
            properties.getOutboxTable(),
            properties.getStatusColumn()
        );
        
        jdbcTemplate.update(sql, EventStatus.FAILED.name(), errorMessage, Timestamp.from(Instant.now()), eventId);
        log.warn("Marked event {} as FAILED", eventId);
    }

    private static class OutboxEventRowMapper implements RowMapper<OutboxEvent> {
        @Override
        public OutboxEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            OutboxEvent event = new OutboxEvent();
            event.setId(rs.getLong("id"));  
            event.setEventType(rs.getString("event_type"));
            event.setPayload(rs.getString("payload"));
            event.setStatus(EventStatus.valueOf(rs.getString("status")));
            event.setRetryCount(rs.getInt("retry_count"));
            
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                event.setCreatedAt(createdAt.toInstant());
            }
            
            return event;
        }
    }
}
