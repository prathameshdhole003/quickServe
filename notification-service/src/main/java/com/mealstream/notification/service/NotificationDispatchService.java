package com.mealstream.notification.service;

import com.mealstream.common.enums.NotificationType;
import com.mealstream.notification.domain.Notification;
import com.mealstream.notification.domain.ProcessedEvent;
import com.mealstream.notification.repository.NotificationRepository;
import com.mealstream.notification.repository.ProcessedEventRepository;
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
public class NotificationDispatchService {

  private final NotificationRepository notificationRepository;
  private final ProcessedEventRepository processedEventRepository;

  /**
   * Dispatches a notification for a given event, with idempotency. Idempotency check and
   * notification persist happen in the same transaction.
   */
  @Transactional
  public void dispatch(
      UUID eventId,
      String topic,
      UUID recipientId,
      UUID orderId,
      NotificationType type,
      String content) {

    if (!markAsProcessed(eventId, topic)) {
      log.info(
          "[NotificationDispatchService] Duplicate event skipped eventId={} type={}",
          eventId,
          type);
      return;
    }

    Notification notification =
        Notification.builder()
            .recipientId(recipientId)
            .orderId(orderId)
            .type(type)
            .content(content)
            .status("SENT")
            .sentAt(Instant.now())
            .build();

    notificationRepository.save(notification);

    // Simulate notification delivery (log-based — no real channel needed)
    log.info(
        "[NOTIFICATION] type={} orderId={} recipientId={} | {}",
        type,
        orderId,
        recipientId,
        content);
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
