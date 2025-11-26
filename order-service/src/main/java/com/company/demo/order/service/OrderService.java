package com.company.demo.order.service;

import java.sql.Timestamp;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final JdbcTemplate jdbcTemplate;

    public OrderService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void processInventoryUpdate(String inventoryData) {
        log.info("Processing inventory update and creating order");

        // Business logic here (create order based on inventory)
        
        // Write to outbox
        String sql = "INSERT INTO outbox_events (event_type, payload, status, created_at) VALUES (?, ?, ?, ?)";
        String payload = String.format("{\"source\":\"inventory\",\"data\":%s,\"orderId\":\"ORD-%d\"}", 
            inventoryData, System.currentTimeMillis());
        
        jdbcTemplate.update(sql, "OrderCreated", payload, "NEW", Timestamp.from(Instant.now()));
        log.info("Order created and event written to PostgreSQL outbox");
    }
}
