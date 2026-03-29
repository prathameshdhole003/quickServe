package com.mealstream.restaurant.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record AddMenuItemRequest(
    @NotBlank String name, String description, @NotNull @DecimalMin("0.01") BigDecimal price) {}
