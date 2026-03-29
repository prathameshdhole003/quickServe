package com.mealstream.notification.kafka.consumer;

import com.mealstream.common.enums.NotificationType;
import com.mealstream.common.events.OrderRejectedEvent;
import com.mealstream.notification.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRejectedNotificationConsumer {

  private final NotificationDispatchService dispatchService;

  @KafkaListener(
      topics = "order-rejected",
      groupId = "notification-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload OrderRejectedEvent event) {
    log.info(
        "[OrderRejectedNotificationConsumer] Received eventId={} orderId={}",
        event.eventId(),
        event.orderId());

    String content =
        String.format(
            "Unfortunately, your order #%s was rejected. Reason: %s. Please try again.",
            event.orderId(), event.rejectionReason());

    dispatchService.dispatch(
        event.eventId(),
        "order-rejected",
        event.restaurantId(),
        event.orderId(),
        NotificationType.ORDER_REJECTED,
        content);
  }
}
