package com.mealstream.notification.kafka.consumer;

import com.mealstream.common.enums.NotificationType;
import com.mealstream.common.events.OrderCreatedEvent;
import com.mealstream.notification.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedNotificationConsumer {

  private final NotificationDispatchService dispatchService;

  @KafkaListener(
      topics = "order-created",
      groupId = "notification-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload OrderCreatedEvent event) {
    log.info(
        "[OrderCreatedNotificationConsumer] Received eventId={} orderId={}",
        event.eventId(),
        event.orderId());

    String content =
        String.format(
            "Your order #%s has been received and is being processed. Total: $%.2f",
            event.orderId(), event.totalAmount());

    dispatchService.dispatch(
        event.eventId(),
        "order-created",
        event.customerId(),
        event.orderId(),
        NotificationType.ORDER_CREATED,
        content);
  }
}
