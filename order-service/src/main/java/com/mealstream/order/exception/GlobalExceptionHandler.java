package com.mealstream.order.exception;

import com.mealstream.common.exception.MealStreamException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MealStreamException.class)
  public ProblemDetail handleMealStreamException(MealStreamException ex) {
    HttpStatus status =
        switch (ex.getErrorCode()) {
          case ORDER_NOT_FOUND -> HttpStatus.NOT_FOUND;
          case DUPLICATE_REQUEST -> HttpStatus.CONFLICT;
          case INVALID_ORDER_STATE -> HttpStatus.UNPROCESSABLE_ENTITY;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

    log.error(
        "[OrderService] Business error code={} message={}", ex.getErrorCode(), ex.getMessage());

    ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    detail.setType(URI.create("/errors/" + ex.getErrorCode().name().toLowerCase()));
    detail.setProperty("errorCode", ex.getErrorCode().name());
    return detail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField, FieldError::getDefaultMessage, (first, second) -> first));

    ProblemDetail detail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request validation failed");
    detail.setType(URI.create("/errors/validation"));
    detail.setProperty("fieldErrors", fieldErrors);
    return detail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex) {
    log.error("[OrderService] Unhandled exception", ex);
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
  }
}
