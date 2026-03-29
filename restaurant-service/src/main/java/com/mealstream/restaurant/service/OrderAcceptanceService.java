package com.mealstream.restaurant.service;

import com.mealstream.common.events.OrderAcceptedEvent;
import com.mealstream.common.events.OrderCreatedEvent;
import com.mealstream.common.events.OrderRejectedEvent;
import com.mealstream.common.exception.ErrorCode;
import com.mealstream.common.exception.MealStreamException;
import com.mealstream.restaurant.domain.ProcessedEvent;
import com.mealstream.restaurant.domain.Restaurant;
import com.mealstream.restaurant.kafka.producer.RestaurantEventProducer;
import com.mealstream.restaurant.repository.ProcessedEventRepository;
import com.mealstream.restaurant.repository.RestaurantRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAcceptanceService {

  private final RestaurantRepository restaurantRepository;
  private final ProcessedEventRepository processedEventRepository;
  private final RestaurantEventProducer restaurantEventProducer;

  /**
   * Processes an OrderCreatedEvent with idempotency guarantee. The processed_events insert and
   * business logic are in the same transaction.
   */
  @Transactional
  public void processOrderCreated(OrderCreatedEvent event) {
    // Layer 2 idempotency: skip if already processed
    if (!markAsProcessed(event.eventId(), "order-created")) {
      log.info("[OrderAcceptanceService] Duplicate event skipped eventId={}", event.eventId());
      return;
    }

    Restaurant restaurant =
        restaurantRepository
            .findById(event.restaurantId())
            .orElseThrow(
                () ->
                    new MealStreamException(
                        ErrorCode.RESTAURANT_NOT_FOUND, event.restaurantId().toString()));

    log.info(
        "[OrderAcceptanceService] Processing order orderId={} restaurantId={} isOpen={}",
        event.orderId(),
        event.restaurantId(),
        restaurant.isOpen());

    if (restaurant.isOpen()) {
      OrderAcceptedEvent accepted =
          new OrderAcceptedEvent(
              UUID.randomUUID(),
              event.orderId(),
              event.restaurantId(),
              restaurant.getAvgPrepMinutes(),
              Instant.now());
      restaurantEventProducer.publishOrderAccepted(accepted);
      log.info("[OrderAcceptanceService] Order accepted orderId={}", event.orderId());
    } else {
      OrderRejectedEvent rejected =
          new OrderRejectedEvent(
              UUID.randomUUID(),
              event.orderId(),
              event.restaurantId(),
              "Restaurant is currently closed",
              Instant.now());
      restaurantEventProducer.publishOrderRejected(rejected);
      log.info("[OrderAcceptanceService] Order rejected orderId={} reason=closed", event.orderId());
    }
  }

  /**
   * Inserts event into processed_events. Returns true if this is the first time processing; false
   * if duplicate.
   */
  private boolean markAsProcessed(UUID eventId, String topic) {
    try {
      processedEventRepository.save(
          ProcessedEvent.builder()
              .eventId(eventId)
              .topic(topic)
              .processedAt(Instant.now())
              .build());
      return true;
    } catch (DataIntegrityViolationException e) {
      return false;
    }
  }
}
