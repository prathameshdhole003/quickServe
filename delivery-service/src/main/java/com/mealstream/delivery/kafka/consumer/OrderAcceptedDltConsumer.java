package com.mealstream.delivery.kafka.consumer;

import com.mealstream.delivery.service.DriverAssignmentService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes the Dead Letter Topic for order-accepted events.
 *
 * <p>When driver assignment fails after all retries (NoAvailableDriverException), the original
 * message lands here. We extract the orderId and trigger the permanent failure flow — publishing a
 * DeliveryFailedEvent.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAcceptedDltConsumer {

  private final DriverAssignmentService driverAssignmentService;

  @KafkaListener(
      topics = "order-accepted.DLT",
      groupId = "delivery-service-dlt-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void consumeDlt(ConsumerRecord<String, Object> record) {
    log.error(
        "[OrderAcceptedDltConsumer] Processing DLT message key={} topic={} offset={}",
        record.key(),
        record.topic(),
        record.offset());

    try {
      // The message key is the orderId (set by producers)
      UUID orderId = UUID.fromString(record.key());
      driverAssignmentService.handleDeliveryFailure(
          orderId, "No available driver after maximum retry attempts");
    } catch (Exception ex) {
      log.error("[OrderAcceptedDltConsumer] Failed to process DLT record key={}", record.key(), ex);
      // Do not re-throw: we don't want infinite DLT loops
    }
  }
}
