package com.mealstream.order.kafka.consumer;

import com.mealstream.common.enums.OrderStatus;
import com.mealstream.common.events.OrderRejectedEvent;
import com.mealstream.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRejectedConsumer {

  private final OrderService orderService;

  @KafkaListener(
      topics = "order-rejected",
      groupId = "order-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload OrderRejectedEvent event) {
    log.info(
        "[OrderRejectedConsumer] Received event orderId={} reason={}",
        event.orderId(),
        event.rejectionReason());

    orderService.updateOrderStatus(event.orderId(), OrderStatus.REJECTED);

    log.info(
        "[OrderRejectedConsumer] Order status updated to REJECTED orderId={}", event.orderId());
  }
}
