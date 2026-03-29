package com.mealstream.restaurant.mapper;

import com.mealstream.restaurant.domain.Restaurant;
import com.mealstream.restaurant.dto.request.RegisterRestaurantRequest;
import com.mealstream.restaurant.dto.response.RestaurantResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isOpen", constant = "false")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Restaurant toEntity(RegisterRestaurantRequest request);

  @Mapping(target = "open", source = "open")
  RestaurantResponse toResponse(Restaurant restaurant);
}
