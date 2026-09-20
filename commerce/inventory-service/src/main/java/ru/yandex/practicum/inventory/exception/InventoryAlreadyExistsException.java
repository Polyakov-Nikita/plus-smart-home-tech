package ru.yandex.practicum.inventory.exception;

import lombok.Getter;

@Getter
public class InventoryAlreadyExistsException extends RuntimeException{
    private final long productId;

    public InventoryAlreadyExistsException(long productId) {
        super(String.format("inventory already exists. productId: '%d'", productId));
        this.productId = productId;
    }
}
