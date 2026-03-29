package com.mealstream.restaurant.exception;

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
          case RESTAURANT_NOT_FOUND -> HttpStatus.NOT_FOUND;
          default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    log.error("[RestaurantService] Business error code={}", ex.getErrorCode());
    ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    detail.setType(URI.create("/errors/" + ex.getErrorCode().name().toLowerCase()));
    return detail;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
    ProblemDetail detail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
    detail.setProperty("fieldErrors", errors);
    return detail;
  }
}
