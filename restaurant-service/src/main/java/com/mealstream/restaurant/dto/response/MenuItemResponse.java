package com.mealstream.restaurant.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemResponse(
    UUID id,
    UUID restaurantId,
    String name,
    String description,
    BigDecimal price,
    boolean available) {}
