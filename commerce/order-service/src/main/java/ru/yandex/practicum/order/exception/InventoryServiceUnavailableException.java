package ru.yandex.practicum.order.exception;

import lombok.Getter;

@Getter
public class InventoryServiceUnavailableException extends RuntimeException {
    private final long productId;
    private final Throwable cause;

    public InventoryServiceUnavailableException(long productId, Throwable cause) {
        super(String.format("inventory service is unavailable. productId: '%d'", productId));
        this.productId = productId;
        this.cause = cause;
    }
}
