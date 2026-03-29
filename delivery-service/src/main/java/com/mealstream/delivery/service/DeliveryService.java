package com.mealstream.delivery.service;

import com.mealstream.common.exception.ErrorCode;
import com.mealstream.common.exception.MealStreamException;
import com.mealstream.delivery.domain.Delivery;
import com.mealstream.delivery.domain.Driver;
import com.mealstream.delivery.dto.response.DeliveryResponse;
import com.mealstream.delivery.dto.response.DriverResponse;
import com.mealstream.delivery.mapper.DeliveryMapper;
import com.mealstream.delivery.repository.DeliveryRepository;
import com.mealstream.delivery.repository.DriverRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

  private final DeliveryRepository deliveryRepository;
  private final DriverRepository driverRepository;
  private final DeliveryMapper deliveryMapper;

  @Transactional(readOnly = true)
  public DeliveryResponse getDelivery(UUID deliveryId) {
    Delivery delivery =
        deliveryRepository
            .findById(deliveryId)
            .orElseThrow(
                () -> new MealStreamException(ErrorCode.DELIVERY_NOT_FOUND, deliveryId.toString()));
    return deliveryMapper.toResponse(delivery);
  }

  @Transactional(readOnly = true)
  public DeliveryResponse getDeliveryByOrder(UUID orderId) {
    Delivery delivery =
        deliveryRepository
            .findByOrderId(orderId)
            .orElseThrow(
                () -> new MealStreamException(ErrorCode.DELIVERY_NOT_FOUND, "orderId=" + orderId));
    return deliveryMapper.toResponse(delivery);
  }

  @Transactional
  public DriverResponse registerDriver(String name, String phone) {
    Driver driver = Driver.builder().name(name).phone(phone).isAvailable(true).build();
    Driver saved = driverRepository.save(driver);
    log.info("[DeliveryService] Registered driver id={} name={}", saved.getId(), saved.getName());
    return new DriverResponse(
        saved.getId(), saved.getName(), saved.getPhone(), saved.isAvailable());
  }

  @Transactional(readOnly = true)
  public List<DriverResponse> getAvailableDrivers() {
    return driverRepository.findAll().stream()
        .filter(Driver::isAvailable)
        .map(d -> new DriverResponse(d.getId(), d.getName(), d.getPhone(), d.isAvailable()))
        .toList();
  }
}
