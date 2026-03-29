package com.mealstream.delivery.exception;

import com.mealstream.common.exception.MealStreamException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MealStreamException.class)
  public ProblemDetail handleMealStreamException(MealStreamException ex) {
    HttpStatus status =
        switch (ex.getErrorCode()) {
          case DELIVERY_NOT_FOUND -> HttpStatus.NOT_FOUND;
          case NO_AVAILABLE_DRIVER -> HttpStatus.SERVICE_UNAVAILABLE;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    log.error("[DeliveryService] Business error code={}", ex.getErrorCode());
    ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    detail.setType(URI.create("/errors/" + ex.getErrorCode().name().toLowerCase()));
    return detail;
  }
}
