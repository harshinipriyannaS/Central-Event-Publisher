# Central Event Publisher JAR

Reusable library for Outbox Pattern event publishing to ActiveMQ.

## Build JAR
```bash
mvn clean install
```

## Usage in Microservice

### 1. Add dependency
```xml
<dependency>
    <groupId>com.company</groupId>
    <artifactId>central-event-publisher</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- For PostgreSQL/MySQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- OR for MongoDB -->
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
</dependency>
```

### 2. Configure application.properties

**For PostgreSQL:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/order_service
spring.datasource.username=postgres
spring.datasource.password=secret

spring.activemq.broker-url=tcp://localhost:61616

event.publisher.database-type=JDBC
event.publisher.outbox-table=outbox_events
event.publisher.status-column=status
event.publisher.queue-mapping.OrderCreated=order-created-queue
```

**For MongoDB:**
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/inventory_service

spring.activemq.broker-url=tcp://localhost:61616

event.publisher.database-type=MONGODB
event.publisher.outbox-table=outbox_events
event.publisher.status-column=status
event.publisher.queue-mapping.InventoryUpdated=inventory-updated-queue
```

### 3. Create outbox table/collection

**PostgreSQL:**
```sql
CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL
);
```

**MongoDB:**
```javascript
db.createCollection("outbox_events");
// Documents will have: _id, event_type, payload, status, createdAt, publishedAt
```

That's it! Publisher starts automatically.
