package com.mealstream.delivery.repository;

import com.mealstream.delivery.domain.Driver;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DriverRepository extends JpaRepository<Driver, UUID> {

  @Query(
      value =
          "SELECT * FROM delivery_svc.drivers WHERE is_available = true ORDER BY created_at ASC"
              + " LIMIT 1",
      nativeQuery = true)
  Optional<Driver> findFirstAvailableDriver();
}
