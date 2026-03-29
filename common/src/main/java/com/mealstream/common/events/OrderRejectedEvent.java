package com.mealstream.common.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.UUID;

/**
 * Published by Restaurant Service when an order is rejected. Consumed by: Order Service,
 * Notification Service.
 */
public record OrderRejectedEvent(
    UUID eventId,
    UUID orderId,
    UUID restaurantId,
    String rejectionReason,
    @JsonFormat(shape = JsonFormat.Shape.STRING) Instant occurredAt) {}
