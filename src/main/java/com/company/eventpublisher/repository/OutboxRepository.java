package com.company.eventpublisher.repository;

import java.util.List;

import com.company.eventpublisher.model.OutboxEvent;

public interface OutboxRepository {
    
    List<OutboxEvent> fetchNewEvents(int batchSize);
    
    void markAsPublished(Object eventId);
    
    void incrementRetryCount(Object eventId);
    
    void markAsFailed(Object eventId, String errorMessage);
}
