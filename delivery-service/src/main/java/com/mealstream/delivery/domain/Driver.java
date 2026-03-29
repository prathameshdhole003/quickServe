package com.mealstream.delivery.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(schema = "delivery_svc", name = "drivers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Driver {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, length = 30)
  private String phone;

  @Column(name = "is_available", nullable = false)
  private boolean isAvailable;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;
}
