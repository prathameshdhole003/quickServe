package com.mealstream.restaurant.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RegisterRestaurantRequest(
    @NotBlank String name,
    @NotNull UUID ownerId,
    @NotBlank String address,
    @Min(1) int avgPrepMinutes) {}
