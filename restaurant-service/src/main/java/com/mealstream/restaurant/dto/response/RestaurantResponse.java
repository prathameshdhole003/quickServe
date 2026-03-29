package com.mealstream.restaurant.dto.response;

import java.time.Instant;
import java.util.UUID;

public record RestaurantResponse(
    UUID id,
    String name,
    UUID ownerId,
    String address,
    boolean open,
    int avgPrepMinutes,
    Instant createdAt) {}
