package com.mealstream.order.repository;

import com.mealstream.order.domain.IdempotencyRecord;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, String> {

  @Modifying
  @Transactional
  @Query("DELETE FROM IdempotencyRecord r WHERE r.expiresAt < :now")
  int deleteExpiredRecords(Instant now);
}
