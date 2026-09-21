package ru.yandex.practicum.inventory.exception;

import lombok.Getter;

@Getter
public class InventoryNotFoundException extends RuntimeException {
    private final long productId;

    public InventoryNotFoundException(long productId) {
        super(String.format("inventory not found. productId: '%d'", productId));
        this.productId = productId;
    }
}