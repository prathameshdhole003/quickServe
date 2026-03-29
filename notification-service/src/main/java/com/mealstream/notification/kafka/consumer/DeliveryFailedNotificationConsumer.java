package com.mealstream.notification.kafka.consumer;

import com.mealstream.common.enums.NotificationType;
import com.mealstream.common.events.DeliveryFailedEvent;
import com.mealstream.notification.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryFailedNotificationConsumer {

  private final NotificationDispatchService dispatchService;

  @KafkaListener(
      topics = "delivery-failed",
      groupId = "notification-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload DeliveryFailedEvent event) {
    log.warn(
        "[DeliveryFailedNotificationConsumer] Received eventId={} orderId={} reason={}",
        event.eventId(),
        event.orderId(),
        event.failureReason());

    String content =
        String.format(
            "We're sorry, delivery for order #%s could not be arranged. Reason: %s. A refund will"
                + " be processed.",
            event.orderId(), event.failureReason());

    dispatchService.dispatch(
        event.eventId(),
        "delivery-failed",
        event.deliveryId(),
        event.orderId(),
        NotificationType.DELIVERY_FAILED,
        content);
  }
}
