package com.mealstream.delivery.controller;

import com.mealstream.delivery.dto.request.RegisterDriverRequest;
import com.mealstream.delivery.dto.response.DeliveryResponse;
import com.mealstream.delivery.dto.response.DriverResponse;
import com.mealstream.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DeliveryController {

  private final DeliveryService deliveryService;

  @GetMapping("/deliveries/{deliveryId}")
  public ResponseEntity<DeliveryResponse> getDelivery(@PathVariable UUID deliveryId) {
    return ResponseEntity.ok(deliveryService.getDelivery(deliveryId));
  }

  @GetMapping("/deliveries/order/{orderId}")
  public ResponseEntity<DeliveryResponse> getDeliveryByOrder(@PathVariable UUID orderId) {
    return ResponseEntity.ok(deliveryService.getDeliveryByOrder(orderId));
  }

  @PostMapping("/drivers")
  public ResponseEntity<DriverResponse> registerDriver(
      @Valid @RequestBody RegisterDriverRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(deliveryService.registerDriver(request.name(), request.phone()));
  }

  @GetMapping("/drivers/available")
  public ResponseEntity<List<DriverResponse>> getAvailableDrivers() {
    return ResponseEntity.ok(deliveryService.getAvailableDrivers());
  }
}
