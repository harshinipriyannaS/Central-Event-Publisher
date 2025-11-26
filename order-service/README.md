# Order Service (PostgreSQL)

## Purpose
Consumes inventory events, creates orders, and publishes order events.

## Database
- **Type**: PostgreSQL
- **Database**: order_db
- **Table**: outbox_events

## Setup

### 1. Setup PostgreSQL
```sql
CREATE DATABASE order_db;
\c order_db

CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL
);

CREATE INDEX idx_outbox_status ON outbox_events(status, created_at);
```

### 2. Build
```bash
mvn clean package
```

### 3. Run
```bash
mvn spring-boot:run
```

## Configuration
- **Port**: 8082
- **Database**: PostgreSQL (localhost:5432)
- **Consumes from**: `inventory-to-order-queue`
- **Publishes to**: `order-to-notification-queue`

## Event Flow
1. **InventoryEventListener** consumes from `inventory-to-order-queue`
2. Business logic creates order
3. Event written to PostgreSQL outbox (status=NEW)
4. **Central Event Publisher JAR** polls outbox
5. Publisher publishes to ActiveMQ queue
6. Event marked as PUBLISHED

## Components
- **Listener**: Consumes inventory events from MQ
- **Service**: Business logic + writes to outbox
- **Central Publisher**: Polls outbox and publishes to MQ

## Dependencies
- Central Event Publisher JAR (handles outbox polling)
- PostgreSQL
- ActiveMQ
