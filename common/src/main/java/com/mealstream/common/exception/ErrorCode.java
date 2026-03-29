package com.mealstream.common.exception;

public enum ErrorCode {
  ORDER_NOT_FOUND("Order not found"),
  RESTAURANT_NOT_FOUND("Restaurant not found"),
  DELIVERY_NOT_FOUND("Delivery not found"),
  NO_AVAILABLE_DRIVER("No available driver at this time"),
  DUPLICATE_REQUEST("Duplicate request - idempotency key already used"),
  INVALID_ORDER_STATE("Order is in an invalid state for this operation"),
  EVENT_PROCESSING_FAILED("Failed to process event");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
