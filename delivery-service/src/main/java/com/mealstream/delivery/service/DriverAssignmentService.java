package com.mealstream.delivery.service;

import com.mealstream.common.enums.DeliveryStatus;
import com.mealstream.common.events.DeliveryAssignedEvent;
import com.mealstream.common.events.DeliveryFailedEvent;
import com.mealstream.common.events.OrderAcceptedEvent;
import com.mealstream.delivery.domain.Delivery;
import com.mealstream.delivery.domain.Driver;
import com.mealstream.delivery.domain.ProcessedEvent;
import com.mealstream.delivery.exception.NoAvailableDriverException;
import com.mealstream.delivery.kafka.producer.DeliveryEventProducer;
import com.mealstream.delivery.repository.DeliveryRepository;
import com.mealstream.delivery.repository.DriverRepository;
import com.mealstream.delivery.repository.ProcessedEventRepository;
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
public class DriverAssignmentService {

  private final DeliveryRepository deliveryRepository;
  private final DriverRepository driverRepository;
  private final ProcessedEventRepository processedEventRepository;
  private final DeliveryEventProducer deliveryEventProducer;

  /**
   * Assigns a driver to an order.
   *
   * <p>If no driver is available, throws NoAvailableDriverException. Spring Kafka will retry this
   * up to 3 times with exponential backoff. After all retries are exhausted,
   * DeadLetterPublishingRecoverer sends a DeliveryFailedEvent from the DLT consumer (see
   * DltConsumer).
   *
   * <p>Note: idempotency check + delivery create + driver mark-busy are all in one transaction.
   */
  @Transactional
  public void assignDriver(OrderAcceptedEvent event) {
    // Idempotency: skip if already processed
    if (!markAsProcessed(event.eventId(), "order-accepted")) {
      log.info("[DriverAssignmentService] Duplicate event skipped eventId={}", event.eventId());
      return;
    }

    // Check if delivery record already exists (defensive)
    if (deliveryRepository.findByOrderId(event.orderId()).isPresent()) {
      log.warn("[DriverAssignmentService] Delivery already exists for orderId={}", event.orderId());
      return;
    }

    Driver driver =
        driverRepository
            .findFirstAvailableDriver()
            .orElseThrow(
                () -> {
                  log.warn(
                      "[DriverAssignmentService] No available drivers for orderId={}",
                      event.orderId());
                  return new NoAvailableDriverException(event.orderId().toString());
                });

    // Mark driver busy
    driver.setAvailable(false);
    driverRepository.save(driver);

    // Create delivery record
    Delivery delivery =
        Delivery.builder()
            .orderId(event.orderId())
            .driverId(driver.getId())
            .status(DeliveryStatus.ASSIGNED)
            .build();
    Delivery saved = deliveryRepository.save(delivery);

    log.info(
        "[DriverAssignmentService] Driver assigned orderId={} driverId={} deliveryId={}",
        event.orderId(),
        driver.getId(),
        saved.getId());

    // Publish success event
    DeliveryAssignedEvent assignedEvent =
        new DeliveryAssignedEvent(
            UUID.randomUUID(),
            event.orderId(),
            saved.getId(),
            driver.getId(),
            driver.getName(),
            driver.getPhone(),
            Instant.now());
    deliveryEventProducer.publishDeliveryAssigned(assignedEvent);
  }

  /**
   * Called when all retries are exhausted (from DLT consumer). Creates a failed delivery record and
   * publishes DeliveryFailedEvent.
   */
  @Transactional
  public void handleDeliveryFailure(UUID orderId, String reason) {
    // Create a failed delivery record
    Delivery delivery = Delivery.builder().orderId(orderId).status(DeliveryStatus.FAILED).build();
    Delivery saved = deliveryRepository.save(delivery);

    log.error(
        "[DriverAssignmentService] Delivery failed permanently orderId={} reason={}",
        orderId,
        reason);

    DeliveryFailedEvent failedEvent =
        new DeliveryFailedEvent(UUID.randomUUID(), orderId, saved.getId(), reason, Instant.now());
    deliveryEventProducer.publishDeliveryFailed(failedEvent);
  }

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
