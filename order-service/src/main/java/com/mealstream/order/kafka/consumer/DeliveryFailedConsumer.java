package com.mealstream.order.kafka.consumer;

import com.mealstream.common.enums.OrderStatus;
import com.mealstream.common.events.DeliveryFailedEvent;
import com.mealstream.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryFailedConsumer {

  private final OrderService orderService;

  @KafkaListener(
      topics = "delivery-failed",
      groupId = "order-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload DeliveryFailedEvent event) {
    log.warn(
        "[DeliveryFailedConsumer] Received event orderId={} reason={}",
        event.orderId(),
        event.failureReason());

    orderService.updateOrderStatus(event.orderId(), OrderStatus.CANCELLED);

    log.info(
        "[DeliveryFailedConsumer] Order status updated to CANCELLED orderId={}", event.orderId());
  }
}
