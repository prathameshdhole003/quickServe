package com.mealstream.delivery.kafka.producer;

import com.mealstream.common.events.DeliveryAssignedEvent;
import com.mealstream.common.events.DeliveryFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventProducer {

  private static final String TOPIC_DELIVERY_ASSIGNED = "delivery-assigned";
  private static final String TOPIC_DELIVERY_FAILED = "delivery-failed";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void publishDeliveryAssigned(DeliveryAssignedEvent event) {
    kafkaTemplate
        .send(TOPIC_DELIVERY_ASSIGNED, event.orderId().toString(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error(
                    "[DeliveryEventProducer] Failed to publish DeliveryAssignedEvent orderId={}",
                    event.orderId(),
                    ex);
              } else {
                log.info(
                    "[DeliveryEventProducer] Published DeliveryAssignedEvent orderId={}"
                        + " driverName={}",
                    event.orderId(),
                    event.driverName());
              }
            });
  }

  public void publishDeliveryFailed(DeliveryFailedEvent event) {
    kafkaTemplate
        .send(TOPIC_DELIVERY_FAILED, event.orderId().toString(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error(
                    "[DeliveryEventProducer] Failed to publish DeliveryFailedEvent orderId={}",
                    event.orderId(),
                    ex);
              } else {
                log.warn(
                    "[DeliveryEventProducer] Published DeliveryFailedEvent orderId={} reason={}",
                    event.orderId(),
                    event.failureReason());
              }
            });
  }
}
