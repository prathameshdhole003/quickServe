package com.mealstream.order.repository;

import com.mealstream.common.enums.OrderStatus;
import com.mealstream.order.domain.Order;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
  List<Order> findByCustomerId(UUID customerId);

  List<Order> findByStatus(OrderStatus status);
}
