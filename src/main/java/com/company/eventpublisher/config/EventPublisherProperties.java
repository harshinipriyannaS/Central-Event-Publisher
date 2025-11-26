package com.company.eventpublisher.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "event.publisher")
public class EventPublisherProperties {

   
    private String outboxTable = "outbox_events";
    private String statusColumn = "status";
    private Map<String, String> queueMapping = new HashMap<>();
    private DatabaseType databaseType = DatabaseType.JDBC; 

   
    private int pollingIntervalMs = 1000;
    private int batchSize = 1; 
    private int maxRetries = 3; 
    private boolean adaptivePolling = true; 
    private RetryConfig retry = new RetryConfig();
    private CircuitBreakerConfig circuitBreaker = new CircuitBreakerConfig();

    public enum DatabaseType {
        JDBC,
        MONGODB
    }

    public String getOutboxTable() {
        return outboxTable;
    }

    public void setOutboxTable(String outboxTable) {
        this.outboxTable = outboxTable;
    }

    public String getStatusColumn() {
        return statusColumn;
    }

    public void setStatusColumn(String statusColumn) {
        this.statusColumn = statusColumn;
    }

    public Map<String, String> getQueueMapping() {
        return queueMapping;
    }

    public void setQueueMapping(Map<String, String> queueMapping) {
        this.queueMapping = queueMapping;
    }

    public int getPollingIntervalMs() {
        return pollingIntervalMs;
    }

    public void setPollingIntervalMs(int pollingIntervalMs) {
        this.pollingIntervalMs = pollingIntervalMs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public RetryConfig getRetry() {
        return retry;
    }

    public void setRetry(RetryConfig retry) {
        this.retry = retry;
    }

    public CircuitBreakerConfig getCircuitBreaker() {
        return circuitBreaker;
    }

    public void setCircuitBreaker(CircuitBreakerConfig circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean isAdaptivePolling() {
        return adaptivePolling;
    }

    public void setAdaptivePolling(boolean adaptivePolling) {
        this.adaptivePolling = adaptivePolling;
    }

    public static class RetryConfig {
        private int maxAttempts = 5;
        private long backoffMs = 1000;

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public long getBackoffMs() {
            return backoffMs;
        }

        public void setBackoffMs(long backoffMs) {
            this.backoffMs = backoffMs;
        }
    }

    public static class CircuitBreakerConfig {
        private float failureThreshold = 50.0f;
        private int waitDurationSeconds = 30;

        public float getFailureThreshold() {
            return failureThreshold;
        }

        public void setFailureThreshold(float failureThreshold) {
            this.failureThreshold = failureThreshold;
        }

        public int getWaitDurationSeconds() {
            return waitDurationSeconds;
        }

        public void setWaitDurationSeconds(int waitDurationSeconds) {
            this.waitDurationSeconds = waitDurationSeconds;
        }
    }
}
