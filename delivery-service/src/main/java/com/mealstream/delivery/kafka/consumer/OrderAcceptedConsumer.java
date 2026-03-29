package com.mealstream.delivery.kafka.consumer;

import com.mealstream.common.events.OrderAcceptedEvent;
import com.mealstream.delivery.service.DriverAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAcceptedConsumer {

  private final DriverAssignmentService driverAssignmentService;

  /**
   * Consumes order-accepted events and attempts driver assignment.
   *
   * <p>If NoAvailableDriverException is thrown, Spring Kafka's DefaultErrorHandler will retry up to
   * 3 times with exponential backoff (1s, 2s, 4s). After retries exhausted,
   * DeadLetterPublishingRecoverer routes to order-accepted.DLT. The DLT is consumed by
   * OrderAcceptedDltConsumer which triggers the failure flow.
   */
  @KafkaListener(
      topics = "order-accepted",
      groupId = "delivery-service-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload OrderAcceptedEvent event) {
    log.info(
        "[OrderAcceptedConsumer] Received event eventId={} orderId={}",
        event.eventId(),
        event.orderId());
    driverAssignmentService.assignDriver(event);
  }
}
