package com.mealstream.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealstream.order.dto.request.CreateOrderRequest;
import com.mealstream.order.dto.response.OrderResponse;
import com.mealstream.order.service.IdempotencyService;
import com.mealstream.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;
  private final IdempotencyService idempotencyService;
  private final ObjectMapper objectMapper;

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
      @Valid @RequestBody CreateOrderRequest request) {

    // Layer 1 idempotency: check for duplicate request
    if (idempotencyKey != null) {
      var existing = idempotencyService.findExistingResponse(idempotencyKey);
      if (existing.isPresent()) {
        log.info(
            "[OrderController] Returning cached response for idempotency key={}", idempotencyKey);
        try {
          OrderResponse cached = objectMapper.readValue(existing.get().body(), OrderResponse.class);
          return ResponseEntity.status(existing.get().statusCode()).body(cached);
        } catch (Exception e) {
          log.warn("[OrderController] Failed to deserialize cached response, reprocessing", e);
        }
      }
    }

    OrderResponse response = orderService.createOrder(request);

    if (idempotencyKey != null) {
      idempotencyService.saveResponse(idempotencyKey, response, HttpStatus.ACCEPTED.value());
    }

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
    return ResponseEntity.ok(orderService.getOrder(orderId));
  }

  @GetMapping("/customer/{customerId}")
  public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(@PathVariable UUID customerId) {
    return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
  }
}
