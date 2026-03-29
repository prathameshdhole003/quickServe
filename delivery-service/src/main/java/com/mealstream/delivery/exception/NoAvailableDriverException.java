package com.mealstream.delivery.exception;

/**
 * Thrown when no available drivers can be found. This exception IS retryable — the Kafka error
 * handler will retry with backoff.
 */
public class NoAvailableDriverException extends RuntimeException {
  public NoAvailableDriverException(String orderId) {
    super("No available driver for orderId=" + orderId);
  }
}
