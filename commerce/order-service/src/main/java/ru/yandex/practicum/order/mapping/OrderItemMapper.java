package ru.yandex.practicum.order.mapping;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.dto.OrderItemDto;
import ru.yandex.practicum.order.entity.OrderItem;
import ru.yandex.practicum.order.service.dto.ItemData;

@Component
public class OrderItemMapper {
    public OrderItem toOrderItem(ItemData itemData) {
        return OrderItem.builder()
                .productId(itemData.productId())
                .productName(itemData.productName())
                .quantity(itemData.quantity())
                .price(itemData.price())
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
