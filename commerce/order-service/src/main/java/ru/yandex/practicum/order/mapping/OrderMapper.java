package ru.yandex.practicum.order.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.dto.CreateOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.entity.Order;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderItemMapper orderItemMapper;

    public Order toOrder(CreateOrderRequest createOrderRequest) {
        return Order.builder()
                .customerName(createOrderRequest.customerName())
                .customerEmail(createOrderRequest.customerEmail())
                .items(
                        createOrderRequest.items().stream()
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
