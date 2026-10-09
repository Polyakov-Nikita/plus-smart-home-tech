package ru.yandex.practicum.order.dto;

public record OrderStatusInfo(
        OrderStatus status,
        String statusDetails
) {
}
