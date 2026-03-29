package com.mealstream.order.mapper;

import com.mealstream.common.enums.OrderStatus;
import com.mealstream.order.domain.Order;
import com.mealstream.order.domain.OrderItem;
import com.mealstream.order.dto.request.CreateOrderRequest;
import com.mealstream.order.dto.response.OrderResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-29T08:37:06+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.6 (SAP SE)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Order toEntity(CreateOrderRequest request) {
        if ( request == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.customerId( request.customerId() );
        order.restaurantId( request.restaurantId() );
        order.deliveryAddress( request.deliveryAddress() );

        return order.build();
    }

    @Override
    public OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        UUID id = null;
        UUID customerId = null;
        UUID restaurantId = null;
        OrderStatus status = null;
        BigDecimal totalAmount = null;
        String deliveryAddress = null;
        List<OrderResponse.OrderItemResponse> items = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        id = order.getId();
        customerId = order.getCustomerId();
        restaurantId = order.getRestaurantId();
        status = order.getStatus();
        totalAmount = order.getTotalAmount();
        deliveryAddress = order.getDeliveryAddress();
        items = orderItemListToOrderItemResponseList( order.getItems() );
        createdAt = order.getCreatedAt();
        updatedAt = order.getUpdatedAt();

        OrderResponse orderResponse = new OrderResponse( id, customerId, restaurantId, status, totalAmount, deliveryAddress, items, createdAt, updatedAt );

        return orderResponse;
    }

    @Override
    public OrderItem toItemEntity(CreateOrderRequest.OrderItemRequest request) {
        if ( request == null ) {
            return null;
        }

        OrderItem.OrderItemBuilder orderItem = OrderItem.builder();

        orderItem.menuItemId( request.menuItemId() );
        orderItem.name( request.name() );
        orderItem.quantity( request.quantity() );
        orderItem.unitPrice( request.unitPrice() );

        return orderItem.build();
    }

    @Override
    public OrderResponse.OrderItemResponse toItemResponse(OrderItem item) {
        if ( item == null ) {
            return null;
        }

        UUID id = null;
        UUID menuItemId = null;
        String name = null;
        int quantity = 0;
        BigDecimal unitPrice = null;

        id = item.getId();
        menuItemId = item.getMenuItemId();
        name = item.getName();
        quantity = item.getQuantity();
        unitPrice = item.getUnitPrice();

        OrderResponse.OrderItemResponse orderItemResponse = new OrderResponse.OrderItemResponse( id, menuItemId, name, quantity, unitPrice );

        return orderItemResponse;
    }

    protected List<OrderResponse.OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderResponse.OrderItemResponse> list1 = new ArrayList<OrderResponse.OrderItemResponse>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toItemResponse( orderItem ) );
        }

        return list1;
    }
}
