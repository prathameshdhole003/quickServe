package com.mealstream.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealstream.order.domain.IdempotencyRecord;
import com.mealstream.order.repository.IdempotencyRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

  private final IdempotencyRepository idempotencyRepository;
  private final ObjectMapper objectMapper;

  /** Returns the cached response if this idempotency key was already processed. */
  @Transactional(readOnly = true)
  public Optional<StoredResponse> findExistingResponse(String idempotencyKey) {
    return idempotencyRepository
        .findById(idempotencyKey)
        .filter(r -> r.getExpiresAt().isAfter(Instant.now()))
        .map(r -> new StoredResponse(r.getResponseBody(), r.getStatusCode()));
  }

  /**
   * Stores the response body for future idempotent lookups. Uses ON CONFLICT DO NOTHING semantics
   * via catching DataIntegrityViolationException.
   */
  @Transactional
  public void saveResponse(String idempotencyKey, Object responseBody, int statusCode) {
    try {
      String json = objectMapper.writeValueAsString(responseBody);
      IdempotencyRecord record =
          IdempotencyRecord.builder()
              .idempotencyKey(idempotencyKey)
              .responseBody(json)
              .statusCode(statusCode)
              .createdAt(Instant.now())
              .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
              .build();
      idempotencyRepository.save(record);
      log.debug("[Idempotency] Stored response for key={}", idempotencyKey);
    } catch (DataIntegrityViolationException e) {
      // Race condition: another thread stored it first — that's fine
      log.debug("[Idempotency] Concurrent insert for key={}, ignoring", idempotencyKey);
    } catch (Exception e) {
      log.error("[Idempotency] Failed to store response for key={}", idempotencyKey, e);
    }
  }

  @Scheduled(cron = "0 0 * * * *") // every hour
  @Transactional
  public void purgeExpiredRecords() {
    int deleted = idempotencyRepository.deleteExpiredRecords(Instant.now());
    log.info("[Idempotency] Purged {} expired records", deleted);
  }

  public record StoredResponse(String body, int statusCode) {}
}
