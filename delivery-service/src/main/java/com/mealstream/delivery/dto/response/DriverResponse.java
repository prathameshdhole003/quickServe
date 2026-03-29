package com.mealstream.delivery.dto.response;

import java.util.UUID;

public record DriverResponse(UUID id, String name, String phone, boolean isAvailable) {}
