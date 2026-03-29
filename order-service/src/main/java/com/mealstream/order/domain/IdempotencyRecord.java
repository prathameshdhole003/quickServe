package com.mealstream.order.domain;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Entity
@Table(schema = "order_svc", name = "idempotency_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord {

  @Id
  @Column(name = "idempotency_key", length = 255)
  private String idempotencyKey;

  @Column(name = "response_body", nullable = false, columnDefinition = "TEXT")
  private String responseBody;

  @Column(name = "status_code", nullable = false)
  private int statusCode;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;
}
