package com.mealstream.common.exception;

public class MealStreamException extends RuntimeException {

  private final ErrorCode errorCode;

  public MealStreamException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public MealStreamException(ErrorCode errorCode, String detail) {
    super(errorCode.getMessage() + ": " + detail);
    this.errorCode = errorCode;
  }

  public MealStreamException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
}
