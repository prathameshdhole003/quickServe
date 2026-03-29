package com.mealstream.restaurant.kafka.consumer;

import com.mealstream.common.events.OrderCreatedEvent;
import com.mealstream.restaurant.service.OrderAcceptanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

  private final OrderAcceptanceService orderAcceptanceService;

  @KafkaListener(
      topics = "order-created",
      groupId = "restaurant-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload OrderCreatedEvent event) {
    log.info(
        "[OrderCreatedConsumer] Received event eventId={} orderId={} restaurantId={}",
        event.eventId(),
        event.orderId(),
        event.restaurantId());
    orderAcceptanceService.processOrderCreated(event);
  }
}
