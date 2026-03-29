package com.mealstream.restaurant.kafka.producer;

import com.mealstream.common.events.OrderAcceptedEvent;
import com.mealstream.common.events.OrderRejectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantEventProducer {

  private static final String TOPIC_ORDER_ACCEPTED = "order-accepted";
  private static final String TOPIC_ORDER_REJECTED = "order-rejected";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void publishOrderAccepted(OrderAcceptedEvent event) {
    kafkaTemplate
        .send(TOPIC_ORDER_ACCEPTED, event.orderId().toString(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error(
                    "[RestaurantEventProducer] Failed to publish OrderAcceptedEvent orderId={}",
                    event.orderId(),
                    ex);
              } else {
                log.info(
                    "[RestaurantEventProducer] Published OrderAcceptedEvent orderId={} partition={}"
                        + " offset={}",
                    event.orderId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
              }
            });
  }

  public void publishOrderRejected(OrderRejectedEvent event) {
    kafkaTemplate
        .send(TOPIC_ORDER_REJECTED, event.orderId().toString(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error(
                    "[RestaurantEventProducer] Failed to publish OrderRejectedEvent orderId={}",
                    event.orderId(),
                    ex);
              } else {
                log.info(
                    "[RestaurantEventProducer] Published OrderRejectedEvent orderId={} reason={}",
                    event.orderId(),
                    event.rejectionReason());
              }
            });
  }
}
