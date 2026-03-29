package com.mealstream.order.kafka.consumer;

import com.mealstream.common.enums.OrderStatus;
import com.mealstream.common.events.OrderAcceptedEvent;
import com.mealstream.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAcceptedConsumer {

  private final OrderService orderService;

  @KafkaListener(
      topics = "order-accepted",
      groupId = "order-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload OrderAcceptedEvent event) {
    log.info(
        "[OrderAcceptedConsumer] Received event orderId={} restaurantId={} estimatedPrep={}min",
        event.orderId(),
        event.restaurantId(),
        event.estimatedPrepMinutes());

    orderService.updateOrderStatus(event.orderId(), OrderStatus.ACCEPTED);

    log.info(
        "[OrderAcceptedConsumer] Order status updated to ACCEPTED orderId={}", event.orderId());
  }
}
