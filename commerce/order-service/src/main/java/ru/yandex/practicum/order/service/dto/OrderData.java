package ru.yandex.practicum.order.service.dto;

import java.util.List;

public record OrderData(
        String customerName,
        String customerEmail,
        List<ItemData> items
) {
}
