package com.mealstream.notification.kafka.consumer;

import com.mealstream.common.enums.NotificationType;
import com.mealstream.common.events.DeliveryAssignedEvent;
import com.mealstream.notification.service.NotificationDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryAssignedNotificationConsumer {

  private final NotificationDispatchService dispatchService;

  @KafkaListener(
      topics = "delivery-assigned",
      groupId = "notification-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload DeliveryAssignedEvent event) {
    log.info(
        "[DeliveryAssignedNotificationConsumer] Received eventId={} orderId={} driverName={}",
        event.eventId(),
        event.orderId(),
        event.driverName());

    String content =
        String.format(
            "Your order #%s is on its way! Driver: %s (%s) has been assigned.",
            event.orderId(), event.driverName(), event.driverPhone());

    dispatchService.dispatch(
        event.eventId(),
        "delivery-assigned",
        event.driverId(),
        event.orderId(),
        NotificationType.DELIVERY_ASSIGNED,
        content);
  }
}
