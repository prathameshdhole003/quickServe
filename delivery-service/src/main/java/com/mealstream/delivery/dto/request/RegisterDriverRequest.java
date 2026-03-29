package com.mealstream.delivery.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterDriverRequest(@NotBlank String name, @NotBlank String phone) {}
