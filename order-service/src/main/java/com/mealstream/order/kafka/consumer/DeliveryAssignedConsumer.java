package com.mealstream.order.kafka.consumer;

import com.mealstream.common.enums.OrderStatus;
import com.mealstream.common.events.DeliveryAssignedEvent;
import com.mealstream.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryAssignedConsumer {

  private final OrderService orderService;

  @KafkaListener(
      topics = "delivery-assigned",
      groupId = "order-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload DeliveryAssignedEvent event) {
    log.info(
        "[DeliveryAssignedConsumer] Received event orderId={} driverName={}",
        event.orderId(),
        event.driverName());

    orderService.updateOrderStatus(event.orderId(), OrderStatus.DELIVERY_ASSIGNED);

    log.info(
        "[DeliveryAssignedConsumer] Order status updated to DELIVERY_ASSIGNED orderId={}",
        event.orderId());
  }
}
