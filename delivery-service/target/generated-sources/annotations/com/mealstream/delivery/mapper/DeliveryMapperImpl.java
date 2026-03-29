package com.mealstream.delivery.mapper;

import com.mealstream.common.enums.DeliveryStatus;
import com.mealstream.delivery.domain.Delivery;
import com.mealstream.delivery.dto.response.DeliveryResponse;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-29T11:41:09+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.6 (SAP SE)"
)
@Component
public class DeliveryMapperImpl implements DeliveryMapper {

    @Override
    public DeliveryResponse toResponse(Delivery delivery) {
        if ( delivery == null ) {
            return null;
        }

        UUID id = null;
        UUID orderId = null;
        UUID driverId = null;
        DeliveryStatus status = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        id = delivery.getId();
        orderId = delivery.getOrderId();
        driverId = delivery.getDriverId();
        status = delivery.getStatus();
        createdAt = delivery.getCreatedAt();
        updatedAt = delivery.getUpdatedAt();

        DeliveryResponse deliveryResponse = new DeliveryResponse( id, orderId, driverId, status, createdAt, updatedAt );

        return deliveryResponse;
    }
}
