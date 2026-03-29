package com.mealstream.order.service;

import com.mealstream.common.enums.OrderStatus;
import com.mealstream.common.events.OrderCreatedEvent;
import com.mealstream.common.exception.ErrorCode;
import com.mealstream.common.exception.MealStreamException;
import com.mealstream.order.domain.Order;
import com.mealstream.order.domain.OrderItem;
import com.mealstream.order.dto.request.CreateOrderRequest;
import com.mealstream.order.dto.response.OrderResponse;
import com.mealstream.order.kafka.producer.OrderEventProducer;
import com.mealstream.order.mapper.OrderMapper;
import com.mealstream.order.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final OrderEventProducer orderEventProducer;

  @Transactional
  public OrderResponse createOrder(CreateOrderRequest request) {
    // Build the aggregate
    Order order =
        Order.builder()
            .customerId(request.customerId())
            .restaurantId(request.restaurantId())
            .deliveryAddress(request.deliveryAddress())
            .status(OrderStatus.PENDING)
            .totalAmount(BigDecimal.ZERO)
            .build();

    // Map and attach items
    request
        .items()
        .forEach(
            itemReq -> {
              OrderItem item = orderMapper.toItemEntity(itemReq);
              order.addItem(item);
            });

    // Calculate total
    BigDecimal total =
        order.getItems().stream()
            .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    order.setTotalAmount(total);

    Order saved = orderRepository.save(order);
    log.info(
        "[OrderService] Created order id={} customerId={} restaurantId={}",
        saved.getId(),
        saved.getCustomerId(),
        saved.getRestaurantId());

    // Publish event AFTER DB commit (caller must be non-@Transactional or this
    // runs inside the transaction; event published on commit success)
    OrderCreatedEvent event = buildOrderCreatedEvent(saved);
    orderEventProducer.publishOrderCreated(event);

    return orderMapper.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public OrderResponse getOrder(UUID orderId) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new MealStreamException(ErrorCode.ORDER_NOT_FOUND, orderId.toString()));
    return orderMapper.toResponse(order);
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> getOrdersByCustomer(UUID customerId) {
    return orderRepository.findByCustomerId(customerId).stream()
        .map(orderMapper::toResponse)
        .toList();
  }

  @Transactional
  public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new MealStreamException(ErrorCode.ORDER_NOT_FOUND, orderId.toString()));
    log.info(
        "[OrderService] Updating order id={} status={} -> {}",
        orderId,
        order.getStatus(),
        newStatus);
    order.setStatus(newStatus);
    orderRepository.save(order);
  }

  private OrderCreatedEvent buildOrderCreatedEvent(Order order) {
    List<OrderCreatedEvent.OrderItemPayload> itemPayloads =
        order.getItems().stream()
            .map(
                i ->
                    new OrderCreatedEvent.OrderItemPayload(
                        i.getMenuItemId(), i.getName(), i.getQuantity(), i.getUnitPrice()))
            .toList();

    return new OrderCreatedEvent(
        UUID.randomUUID(),
        order.getId(),
        order.getRestaurantId(),
        order.getCustomerId(),
        itemPayloads,
        order.getTotalAmount(),
        order.getDeliveryAddress(),
        Instant.now());
  }
}
