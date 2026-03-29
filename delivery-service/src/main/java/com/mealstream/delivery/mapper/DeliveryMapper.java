package com.mealstream.delivery.mapper;

import com.mealstream.delivery.domain.Delivery;
import com.mealstream.delivery.dto.response.DeliveryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
  DeliveryResponse toResponse(Delivery delivery);
}
