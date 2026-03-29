package com.mealstream.order.dto.response;

import com.mealstream.common.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    UUID customerId,
    UUID restaurantId,
    OrderStatus status,
    BigDecimal totalAmount,
    String deliveryAddress,
    List<OrderItemResponse> items,
    Instant createdAt,
    Instant updatedAt) {
  public record OrderItemResponse(
      UUID id, UUID menuItemId, String name, int quantity, BigDecimal unitPrice) {}
}
