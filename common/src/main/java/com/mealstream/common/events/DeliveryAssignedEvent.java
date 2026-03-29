package com.mealstream.common.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.UUID;

/**
 * Published by Delivery Service when a driver is assigned. Consumed by: Order Service, Notification
 * Service.
 */
public record DeliveryAssignedEvent(
    UUID eventId,
    UUID orderId,
    UUID deliveryId,
    UUID driverId,
    String driverName,
    String driverPhone,
    @JsonFormat(shape = JsonFormat.Shape.STRING) Instant occurredAt) {}
