package com.mealstream.order.mapper;

import com.mealstream.order.domain.Order;
import com.mealstream.order.domain.OrderItem;
import com.mealstream.order.dto.request.CreateOrderRequest;
import com.mealstream.order.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "items", ignore = true)
  Order toEntity(CreateOrderRequest request);

  OrderResponse toResponse(Order order);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  OrderItem toItemEntity(CreateOrderRequest.OrderItemRequest request);

  OrderResponse.OrderItemResponse toItemResponse(OrderItem item);
}
