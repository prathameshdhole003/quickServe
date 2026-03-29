package com.mealstream.notification.repository;

import com.mealstream.notification.domain.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  List<Notification> findByOrderIdOrderByCreatedAtDesc(UUID orderId);

  List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);
}
