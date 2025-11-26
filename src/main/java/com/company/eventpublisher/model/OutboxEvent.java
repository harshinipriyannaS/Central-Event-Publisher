package com.company.eventpublisher.model;

import java.time.Instant;

public class OutboxEvent {
    private Object id;  
    private String eventType;
    private String payload;
    private EventStatus status;
    private Instant createdAt;
    private Instant publishedAt;
    private int retryCount;
    private Instant lastRetryAt;
    private String errorMessage;

    public OutboxEvent() {
    }

    public OutboxEvent(Object id, String eventType, String payload, EventStatus status, Instant createdAt) {
        this.id = id;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.createdAt = createdAt;
        this.retryCount = 0;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getLastRetryAt() {
        return lastRetryAt;
    }

    public void setLastRetryAt(Instant lastRetryAt) {
        this.lastRetryAt = lastRetryAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
