package ru.yandex.practicum.order.service.dto;

import java.math.BigDecimal;

public record ItemData(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price
) {
}
