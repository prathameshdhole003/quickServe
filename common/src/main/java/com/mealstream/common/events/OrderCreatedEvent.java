package com.mealstream.common.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Published by Order Service when a new order is created. Consumed by: Restaurant Service,
 * Notification Service.
 */
public record OrderCreatedEvent(
    UUID eventId,
    UUID orderId,
    UUID restaurantId,
    UUID customerId,
    List<OrderItemPayload> items,
    BigDecimal totalAmount,
    String deliveryAddress,
    @JsonFormat(shape = JsonFormat.Shape.STRING) Instant occurredAt) {
  public record OrderItemPayload(
      UUID menuItemId, String name, int quantity, BigDecimal unitPrice) {}
}
