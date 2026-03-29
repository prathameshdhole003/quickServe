package com.mealstream.common.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.UUID;

/**
 * Published by Delivery Service when delivery assignment fails after all retries. Consumed by:
 * Order Service, Notification Service.
 */
public record DeliveryFailedEvent(
    UUID eventId,
    UUID orderId,
    UUID deliveryId,
    String failureReason,
    @JsonFormat(shape = JsonFormat.Shape.STRING) Instant occurredAt) {}
