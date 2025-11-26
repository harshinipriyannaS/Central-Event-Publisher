# Notification Service

## Purpose
Final consumer - receives order events and sends notifications.

## Database
None (pure consumer, no outbox needed)

## Setup

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

## Configuration
- **Port**: 8083
- **Consumes from**: `order-to-notification-queue`

## Event Flow
1. **OrderEventListener** consumes from `order-to-notification-queue`
2. Logs the order details
3. Simulates sending notification (email/SMS)

## Components
- **Listener**: Consumes order events from MQ
- No outbox (this is the final destination)

## Dependencies
- ActiveMQ only (no database, no Central Publisher JAR)
