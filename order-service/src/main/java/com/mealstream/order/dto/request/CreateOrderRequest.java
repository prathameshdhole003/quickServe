package com.mealstream.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    @NotNull(message = "customerId is required") UUID customerId,
    @NotNull(message = "restaurantId is required") UUID restaurantId,
    @NotBlank(message = "deliveryAddress is required") String deliveryAddress,
    @NotEmpty(message = "Order must have at least one item") @Valid List<OrderItemRequest> items) {
  public record OrderItemRequest(
      @NotNull UUID menuItemId,
      @NotBlank String name,
      @Min(1) int quantity,
      @NotNull @DecimalMin("0.01") BigDecimal unitPrice) {}
}
