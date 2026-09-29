package ru.yandex.practicum.order.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.entity.Order;
import ru.yandex.practicum.order.service.dto.OrderData;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderItemMapper orderItemMapper;

    public Order toOrder(OrderData orderData) {
        return Order.builder()
                .customerName(orderData.customerName())
                .customerEmail(orderData.customerEmail())
                .items(
                        orderData.items().stream()
                                .map(orderItemMapper::toOrderItem)
                                .toList()
                )
                .build();
    }

    public OrderDto toOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getStatus().name(),
                order.getTotalPrice(),
                order.getStatusDetails(),
                order.getCreatedAt(),
                order.getItems().stream()
                        .map(orderItemMapper::toOrderItemDto)
                        .toList()
        );
    }
}
