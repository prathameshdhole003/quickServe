package com.mealstream.restaurant.mapper;

import com.mealstream.restaurant.domain.Restaurant;
import com.mealstream.restaurant.dto.request.RegisterRestaurantRequest;
import com.mealstream.restaurant.dto.response.RestaurantResponse;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-29T08:47:55+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.6 (SAP SE)"
)
@Component
public class RestaurantMapperImpl implements RestaurantMapper {

    @Override
    public Restaurant toEntity(RegisterRestaurantRequest request) {
        if ( request == null ) {
            return null;
        }

        Restaurant.RestaurantBuilder restaurant = Restaurant.builder();

        restaurant.name( request.name() );
        restaurant.ownerId( request.ownerId() );
        restaurant.address( request.address() );
        restaurant.avgPrepMinutes( request.avgPrepMinutes() );

        restaurant.isOpen( false );

        return restaurant.build();
    }

    @Override
    public RestaurantResponse toResponse(Restaurant restaurant) {
        if ( restaurant == null ) {
            return null;
        }

        boolean open = false;
        UUID id = null;
        String name = null;
        UUID ownerId = null;
        String address = null;
        int avgPrepMinutes = 0;
        Instant createdAt = null;

        open = restaurant.isOpen();
        id = restaurant.getId();
        name = restaurant.getName();
        ownerId = restaurant.getOwnerId();
        address = restaurant.getAddress();
        avgPrepMinutes = restaurant.getAvgPrepMinutes();
        createdAt = restaurant.getCreatedAt();

        RestaurantResponse restaurantResponse = new RestaurantResponse( id, name, ownerId, address, open, avgPrepMinutes, createdAt );

        return restaurantResponse;
    }
}
