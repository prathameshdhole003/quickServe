package com.mealstream.restaurant.controller;

import com.mealstream.restaurant.dto.request.AddMenuItemRequest;
import com.mealstream.restaurant.dto.request.RegisterRestaurantRequest;
import com.mealstream.restaurant.dto.response.MenuItemResponse;
import com.mealstream.restaurant.dto.response.RestaurantResponse;
import com.mealstream.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

  private final RestaurantService restaurantService;

  @PostMapping
  public ResponseEntity<RestaurantResponse> register(
      @Valid @RequestBody RegisterRestaurantRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(restaurantService.registerRestaurant(request));
  }

  @GetMapping("/{restaurantId}")
  public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable UUID restaurantId) {
    return ResponseEntity.ok(restaurantService.getRestaurant(restaurantId));
  }

  @GetMapping
  public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
    return ResponseEntity.ok(restaurantService.getAllRestaurants());
  }

  @PatchMapping("/{restaurantId}/toggle-open")
  public ResponseEntity<RestaurantResponse> toggleOpen(@PathVariable UUID restaurantId) {
    return ResponseEntity.ok(restaurantService.toggleOpen(restaurantId));
  }

  @PostMapping("/{restaurantId}/menu-items")
  public ResponseEntity<MenuItemResponse> addMenuItem(
      @PathVariable UUID restaurantId, @Valid @RequestBody AddMenuItemRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(restaurantService.addMenuItem(restaurantId, request));
  }

  @GetMapping("/{restaurantId}/menu-items")
  public ResponseEntity<List<MenuItemResponse>> getMenuItems(@PathVariable UUID restaurantId) {
    return ResponseEntity.ok(restaurantService.getMenuItems(restaurantId));
  }
}
