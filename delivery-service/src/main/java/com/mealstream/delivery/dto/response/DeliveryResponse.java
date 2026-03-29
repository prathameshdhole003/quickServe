package com.mealstream.delivery.dto.response;

import com.mealstream.common.enums.DeliveryStatus;
import java.time.Instant;
import java.util.UUID;

public record DeliveryResponse(
    UUID id,
    UUID orderId,
    UUID driverId,
    DeliveryStatus status,
    Instant createdAt,
    Instant updatedAt) {}
