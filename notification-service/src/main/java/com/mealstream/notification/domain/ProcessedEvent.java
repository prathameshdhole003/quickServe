package com.mealstream.notification.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Entity
@Table(schema = "notification_svc", name = "processed_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedEvent {

  @Id
  @Column(name = "event_id")
  private UUID eventId;

  @Column(nullable = false, length = 100)
  private String topic;

  @Column(name = "processed_at", nullable = false)
  private Instant processedAt;
}
