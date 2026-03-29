package com.mealstream.restaurant.repository;

import com.mealstream.restaurant.domain.Restaurant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {}
