# MealStream

Event-driven food delivery backend using Spring Boot microservices and Apache Kafka.

---

## Architecture Overview

```
[Client]
   │
   │  POST /api/v1/orders  (Idempotency-Key header)
   ▼
┌──────────────────┐         order-created        ┌─────────────────────┐
│  Order Service   │ ──────────────────────────►  │ Restaurant Service  │
│  :8081           │                              │  :8082              │
│                  │ ◄── order-accepted ───────── │                     │
│                  │ ◄── order-rejected ───────── │                     │
└──────────────────┘                              └─────────────────────┘
         │                                                  │
         │  order-accepted                      order-created / order-accepted
         │  order-rejected                      order-rejected / delivery-*
         ▼                                                  ▼
┌──────────────────┐         delivery-assigned   ┌─────────────────────┐
│ Delivery Service │ ──────────────────────────► │Notification Service │
│  :8083           │         delivery-failed     │  :8084              │
│                  │ ──────────────────────────► │                     │
└──────────────────┘                             └─────────────────────┘
```

### Kafka Topics

| Topic               | Producer           | Consumers                                        |
|---------------------|--------------------|--------------------------------------------------|
| `order-created`     | Order Service      | Restaurant Service, Notification Service         |
| `order-accepted`    | Restaurant Service | Order Service, Delivery Service, Notification    |
| `order-rejected`    | Restaurant Service | Order Service, Notification Service              |
| `delivery-assigned` | Delivery Service   | Order Service, Notification Service              |
| `delivery-failed`   | Delivery Service   | Order Service, Notification Service              |

**DLT topics** (Dead Letter Topics): `{topic}.DLT` for each of the above.

### Consumer Groups

| Group ID                      | Service              |
|-------------------------------|----------------------|
| `order-service-group`         | Order Service        |
| `restaurant-service-group`    | Restaurant Service   |
| `delivery-service-group`      | Delivery Service     |
| `notification-service-group`  | Notification Service |

### Idempotency

- **REST layer** (Order Service): `Idempotency-Key` header checked against `idempotency_records` table.
- **Kafka consumers** (all services): `eventId` inserted into `processed_events` table before processing. Duplicate = skip.

### Retry & DLQ

- Spring Kafka `DefaultErrorHandler` with `ExponentialBackOff`: 1s → 2s → 4s (3 retries).
- After 3 failures, `DeadLetterPublishingRecoverer` routes to `{topic}.DLT`.
- Delivery Service `OrderAcceptedDltConsumer` reads `order-accepted.DLT` and publishes `DeliveryFailedEvent`.

---

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose

---

## Quick Start

### 1. Start infrastructure

```bash
cd mealstream
docker compose up -d
```

Wait for all containers to be healthy (~15 seconds):

```bash
docker compose ps
```

Kafka UI is available at: http://localhost:9090

### 2. Build the project

```bash
mvn clean install -DskipTests
```

### 3. Start the services (4 separate terminals)

```bash
# Terminal 1 - Order Service
cd order-service && mvn spring-boot:run

# Terminal 2 - Restaurant Service
cd restaurant-service && mvn spring-boot:run

# Terminal 3 - Delivery Service
cd delivery-service && mvn spring-boot:run

# Terminal 4 - Notification Service
cd notification-service && mvn spring-boot:run
```

---

## Testing the Full Order Flow

### Step 1 — Register a Restaurant

```bash
curl -s -X POST http://localhost:8082/api/v1/restaurants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "The Golden Fork",
    "ownerId": "00000000-0000-0000-0000-000000000001",
    "address": "123 Main St, Springfield",
    "avgPrepMinutes": 25
  }' | jq .
```

Save the `id` from the response as `RESTAURANT_ID`.

### Step 2 — Open the Restaurant

```bash
curl -s -X PATCH http://localhost:8082/api/v1/restaurants/{RESTAURANT_ID}/toggle-open | jq .
```

Confirm `"isOpen": true` in the response.

### Step 3 — Place an Order

```bash
curl -s -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: $(uuidgen)" \
  -d '{
    "customerId": "00000000-0000-0000-0000-000000000099",
    "restaurantId": "RESTAURANT_ID",
    "deliveryAddress": "456 Elm Street, Springfield",
    "items": [
      {
        "menuItemId": "00000000-0000-0000-0000-000000000010",
        "name": "Margherita Pizza",
        "quantity": 2,
        "unitPrice": 12.99
      },
      {
        "menuItemId": "00000000-0000-0000-0000-000000000011",
        "name": "Caesar Salad",
        "quantity": 1,
        "unitPrice": 8.50
      }
    ]
  }' | jq .
```

Save the `id` from the response as `ORDER_ID`. The order starts with status `PENDING`.

### Step 4 — Watch the Event Flow in Logs

In the service logs you should see this sequence:

```
[Order Service]        Published OrderCreatedEvent orderId=...
[Restaurant Service]   Received event eventId=... orderId=...
[Restaurant Service]   Order accepted orderId=...
[Restaurant Service]   Published OrderAcceptedEvent orderId=...
[Notification Service] type=ORDER_CREATED orderId=... | Your order #... has been received...
[Order Service]        Updating order status: PENDING -> ACCEPTED
[Delivery Service]     Received event eventId=... orderId=...
[Delivery Service]     Driver assigned orderId=... driverId=...
[Delivery Service]     Published DeliveryAssignedEvent orderId=...
[Notification Service] type=ORDER_ACCEPTED orderId=... | Great news! Your order...
[Order Service]        Updating order status: ACCEPTED -> DELIVERY_ASSIGNED
[Notification Service] type=DELIVERY_ASSIGNED orderId=... | Your order is on its way!...
```

### Step 5 — Check Order Status

```bash
curl -s http://localhost:8081/api/v1/orders/{ORDER_ID} | jq .
# Expected: "status": "DELIVERY_ASSIGNED"
```

### Step 6 — Check Delivery

```bash
curl -s http://localhost:8083/api/v1/deliveries/order/{ORDER_ID} | jq .
# Expected: "status": "ASSIGNED" with driverId populated
```

### Step 7 — Check Available Drivers

```bash
curl -s http://localhost:8083/api/v1/drivers/available | jq .
# One driver should now be unavailable (assigned to the order)
```

---

## Testing Idempotency

Send the same order twice with the same `Idempotency-Key`:

```bash
KEY=$(uuidgen)

# First request — creates the order
curl -s -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: $KEY" \
  -d '{ ... same body ... }' | jq .id

# Second request — returns the SAME order ID, no duplicate created
curl -s -X POST http://localhost:8081/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: $KEY" \
  -d '{ ... same body ... }' | jq .id
```

Both responses should return the same `id`.

---

## Testing the Rejection Flow

Close the restaurant first:

```bash
curl -s -X PATCH http://localhost:8082/api/v1/restaurants/{RESTAURANT_ID}/toggle-open | jq .
# "isOpen": false
```

Then place a new order. You should see in the logs:

```
[Restaurant Service]   Order rejected orderId=... reason=closed
[Order Service]        Updating order status: PENDING -> REJECTED
[Notification Service] type=ORDER_REJECTED | Unfortunately, your order...
```

---

## Testing the DLQ / Retry Flow

To simulate a delivery failure (no drivers available):

1. Register 0 drivers (the 3 seeded drivers are marked available by default, so first mark them all busy by placing 3 orders successfully).
2. Then place a 4th order — delivery service will retry 3 times then route to DLT.
3. `OrderAcceptedDltConsumer` picks it up and publishes `DeliveryFailedEvent`.

Alternatively, temporarily stop the delivery-service, consume all drivers, then restart — the DLT consumer will process the backlog.

---

## Testing Idempotency on Kafka Consumer Side

Restart any service (e.g., `restaurant-service`) mid-flow. When it restarts, it replays from the last committed offset. The `processed_events` table ensures events are not double-processed.

---

## Service Health Checks

```bash
curl http://localhost:8081/actuator/health  # Order Service
curl http://localhost:8082/actuator/health  # Restaurant Service
curl http://localhost:8083/actuator/health  # Delivery Service
curl http://localhost:8084/actuator/health  # Notification Service
```

---

## Register Additional Drivers

```bash
curl -s -X POST http://localhost:8083/api/v1/drivers \
  -H "Content-Type: application/json" \
  -d '{"name": "Dave Brown", "phone": "+1-555-0201"}' | jq .
```

---

## Project Structure

```
mealstream/
├── docker-compose.yml
├── pom.xml                          ← parent POM with all dependency management
├── common/                          ← shared event records, enums, exceptions
│   └── src/main/java/com/mealstream/common/
│       ├── events/                  ← OrderCreatedEvent, OrderAcceptedEvent, ...
│       ├── enums/                   ← OrderStatus, DeliveryStatus, NotificationType
│       └── exception/               ← MealStreamException, ErrorCode
├── order-service/       :8081       ← REST entry point, idempotency, publishes order-created
├── restaurant-service/  :8082       ← consumes order-created, publishes accept/reject
├── delivery-service/    :8083       ← consumes order-accepted, retry+DLQ, assigns drivers
└── notification-service/:8084       ← consumes all events, logs structured notifications
```

Each service follows: `Controller → Service → Repository` with DTOs, MapStruct mappers, Flyway migrations, and a `processed_events` table for consumer idempotency.
