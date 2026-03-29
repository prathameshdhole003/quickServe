package com.mealstream.common.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.UUID;

/**
 * Published by Restaurant Service when an order is accepted. Consumed by: Order Service, Delivery
 * Service, Notification Service.
 */
public record OrderAcceptedEvent(
    UUID eventId,
    UUID orderId,
    UUID restaurantId,
    int estimatedPrepMinutes,
    @JsonFormat(shape = JsonFormat.Shape.STRING) Instant occurredAt) {}
