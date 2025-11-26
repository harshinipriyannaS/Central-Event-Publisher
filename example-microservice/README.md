# Example Microservice Configurations

This folder contains example `application.properties` files for different database types.

## For MongoDB Microservice

Copy `application-mongodb.properties` to your microservice's `src/main/resources/application.properties`

**Required:**
- MongoDB connection URI
- Queue mappings for your event types

## For PostgreSQL Microservice

Copy `application-postgres.properties` to your microservice's `src/main/resources/application.properties`

**Required:**
- PostgreSQL connection details
- Queue mappings for your event types

## Configuration Guide

### Minimal Required Config

```properties
# Database type
event.publisher.database-type=MONGODB  # or JDBC

# Table/Collection name
event.publisher.outbox-table=outbox_events

# Queue mappings
event.publisher.queue-mapping.YourEventType=your-queue-name
```

### All Available Options

```properties
# Polling
event.publisher.polling-interval-ms=1000
event.publisher.batch-size=1
event.publisher.adaptive-polling=true

# Retry
event.publisher.max-retries=5
event.publisher.retry.max-attempts=5
event.publisher.retry.backoff-ms=1000

# Circuit Breaker
event.publisher.circuit-breaker.failure-threshold=50
event.publisher.circuit-breaker.wait-duration-seconds=30
```

## Actual Working Examples

See the real implementations:
- `inventory-service/src/main/resources/application.properties` (MongoDB)
- `order-service/src/main/resources/application.properties` (PostgreSQL)
- `notification-service/src/main/resources/application.properties` (Consumer only)
