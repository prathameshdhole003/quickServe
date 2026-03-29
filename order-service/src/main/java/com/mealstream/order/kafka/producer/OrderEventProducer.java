package com.mealstream.order.kafka.producer;

import com.mealstream.common.events.OrderCreatedEvent;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

  private static final String TOPIC_ORDER_CREATED = "order-created";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void publishOrderCreated(OrderCreatedEvent event) {
    // Use orderId as key to guarantee ordering per order across partitions
    CompletableFuture<SendResult<String, Object>> future =
        kafkaTemplate.send(TOPIC_ORDER_CREATED, event.orderId().toString(), event);

    future.whenComplete(
        (result, ex) -> {
          if (ex != null) {
            log.error(
                "[OrderEventProducer] Failed to publish OrderCreatedEvent orderId={} error={}",
                event.orderId(),
                ex.getMessage(),
                ex);
          } else {
            log.info(
                "[OrderEventProducer] Published OrderCreatedEvent orderId={} topic={} partition={}"
                    + " offset={}",
                event.orderId(),
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
          }
        });
  }
}
