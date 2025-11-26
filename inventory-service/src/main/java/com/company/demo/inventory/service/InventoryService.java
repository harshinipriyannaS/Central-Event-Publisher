package com.company.demo.inventory.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.company.demo.inventory.model.OutboxEvent;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final MongoTemplate mongoTemplate;

    public InventoryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void updateInventory(String productId, int quantity) {
        log.info("Updating inventory for product: {}, quantity: {}", productId, quantity);

        // Business logic here (update inventory)
        
        // Write to outbox
        OutboxEvent event = new OutboxEvent();
        event.setEventType("InventoryUpdated");
        event.setPayload(String.format("{\"productId\":\"%s\",\"quantity\":%d,\"timestamp\":\"%s\"}", 
            productId, quantity, Instant.now()));
        event.setStatus("NEW");
        event.setCreatedAt(Instant.now());
        event.setRetryCount(0);  // Initialize retry count

        mongoTemplate.save(event, "outbox_events");
        log.info("Event written to MongoDB outbox");
    }
}
