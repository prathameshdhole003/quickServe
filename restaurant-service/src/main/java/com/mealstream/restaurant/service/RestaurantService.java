package com.mealstream.restaurant.service;

import com.mealstream.common.exception.ErrorCode;
import com.mealstream.common.exception.MealStreamException;
import com.mealstream.restaurant.domain.MenuItem;
import com.mealstream.restaurant.domain.Restaurant;
import com.mealstream.restaurant.dto.request.AddMenuItemRequest;
import com.mealstream.restaurant.dto.request.RegisterRestaurantRequest;
import com.mealstream.restaurant.dto.response.MenuItemResponse;
import com.mealstream.restaurant.dto.response.RestaurantResponse;
import com.mealstream.restaurant.mapper.RestaurantMapper;
import com.mealstream.restaurant.repository.MenuItemRepository;
import com.mealstream.restaurant.repository.RestaurantRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

  private final RestaurantRepository restaurantRepository;
  private final MenuItemRepository menuItemRepository;
  private final RestaurantMapper restaurantMapper;

  @Transactional
  public RestaurantResponse registerRestaurant(RegisterRestaurantRequest request) {
    Restaurant restaurant = restaurantMapper.toEntity(request);
    Restaurant saved = restaurantRepository.save(restaurant);
    log.info(
        "[RestaurantService] Registered restaurant id={} name={}", saved.getId(), saved.getName());
    return restaurantMapper.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public RestaurantResponse getRestaurant(UUID restaurantId) {
    return restaurantRepository
        .findById(restaurantId)
        .map(restaurantMapper::toResponse)
        .orElseThrow(
            () -> new MealStreamException(ErrorCode.RESTAURANT_NOT_FOUND, restaurantId.toString()));
  }

  @Transactional(readOnly = true)
  public List<RestaurantResponse> getAllRestaurants() {
    return restaurantRepository.findAll().stream().map(restaurantMapper::toResponse).toList();
  }

  @Transactional
  public RestaurantResponse toggleOpen(UUID restaurantId) {
    Restaurant restaurant =
        restaurantRepository
            .findById(restaurantId)
            .orElseThrow(
                () ->
                    new MealStreamException(
                        ErrorCode.RESTAURANT_NOT_FOUND, restaurantId.toString()));
    restaurant.setOpen(!restaurant.isOpen());
    Restaurant saved = restaurantRepository.save(restaurant);
    log.info("[RestaurantService] Restaurant id={} isOpen={}", saved.getId(), saved.isOpen());
    return restaurantMapper.toResponse(saved);
  }

  @Transactional
  public MenuItemResponse addMenuItem(UUID restaurantId, AddMenuItemRequest request) {
    if (!restaurantRepository.existsById(restaurantId)) {
      throw new MealStreamException(ErrorCode.RESTAURANT_NOT_FOUND, restaurantId.toString());
    }
    MenuItem item =
        MenuItem.builder()
            .restaurantId(restaurantId)
            .name(request.name())
            .description(request.description())
            .price(request.price())
            .isAvailable(true)
            .build();
    MenuItem saved = menuItemRepository.save(item);
    log.info(
        "[RestaurantService] Added menu item id={} to restaurant id={}",
        saved.getId(),
        restaurantId);
    return new MenuItemResponse(
        saved.getId(),
        saved.getRestaurantId(),
        saved.getName(),
        saved.getDescription(),
        saved.getPrice(),
        saved.isAvailable());
  }

  @Transactional(readOnly = true)
  public List<MenuItemResponse> getMenuItems(UUID restaurantId) {
    return menuItemRepository.findByRestaurantId(restaurantId).stream()
        .map(
            i ->
                new MenuItemResponse(
                    i.getId(),
                    i.getRestaurantId(),
                    i.getName(),
                    i.getDescription(),
                    i.getPrice(),
                    i.isAvailable()))
        .toList();
  }
}
