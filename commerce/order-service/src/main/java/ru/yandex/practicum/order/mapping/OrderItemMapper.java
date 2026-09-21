package ru.yandex.practicum.order.mapping;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.dto.OrderItemDto;
import ru.yandex.practicum.order.dto.OrderItemRequest;
import ru.yandex.practicum.order.entity.OrderItem;

@Component
public class OrderItemMapper {
    public OrderItem toOrderItem(OrderItemRequest orderItemRequest) {
        return OrderItem.builder()
                .productId(orderItemRequest.productId())
                .productName(orderItemRequest.productName())
                .quantity(orderItemRequest.quantity())
                .price(orderItemRequest.price())
                .build();
    }

    public OrderItemDto toOrderItemDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getProductId(),
                orderItem.getProductName(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }
}
