# Inventory Service (MongoDB)

## Purpose
Manages inventory and publishes events when inventory is updated.

## Database
- **Type**: MongoDB
- **Database**: inventory_db
- **Collection**: outbox_events

## Setup

### 1. Setup MongoDB
```bash
mongo
use inventory_db
db.createCollection("outbox_events")
db.outbox_events.createIndex({ "status": 1, "createdAt": 1 })
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
- **Port**: 8081
- **Database**: MongoDB (localhost:27017)
- **Publishes to**: `inventory-to-order-queue`

## API

### Update Inventory
```bash
POST http://localhost:8081/api/inventory/update?productId=PROD-123&quantity=50
```

## Event Flow
1. REST API receives inventory update
2. Business logic updates inventory
3. Event written to MongoDB outbox (status=NEW)
4. **Central Event Publisher JAR** polls outbox
5. Publisher publishes to ActiveMQ queue
6. Event marked as PUBLISHED

## Dependencies
- Central Event Publisher JAR (handles outbox polling)
- MongoDB
- ActiveMQ
