package com.mealstream.notification.kafka.consumer;

import com.mealstream.common.enums.NotificationType;
import com.mealstream.common.events.OrderAcceptedEvent;
import com.mealstream.notification.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAcceptedNotificationConsumer {

  private final NotificationDispatchService dispatchService;

  @KafkaListener(
      topics = "order-accepted",
      groupId = "notification-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload OrderAcceptedEvent event) {
    log.info(
        "[OrderAcceptedNotificationConsumer] Received eventId={} orderId={}",
        event.eventId(),
        event.orderId());

    String content =
        String.format(
            "Great news! Your order #%s has been accepted. Estimated preparation time: %d minutes.",
            event.orderId(), event.estimatedPrepMinutes());

    // Note: we don't have customerId in this event; use restaurantId as placeholder for recipient
    // routing
    dispatchService.dispatch(
        event.eventId(),
        "order-accepted",
        event.restaurantId(),
        event.orderId(),
        NotificationType.ORDER_ACCEPTED,
        content);
  }
}
