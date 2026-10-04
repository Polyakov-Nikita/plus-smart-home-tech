package ru.yandex.practicum.order.exception;

import lombok.Getter;

@Getter
public class ProductServiceUnavailableException extends RuntimeException {
    private final long productId;
    private final Throwable cause;

    public ProductServiceUnavailableException(long productId, Throwable cause) {
        super(String.format("product service is unavailable. productId: '%d'", productId));
        this.productId = productId;
        this.cause = cause;
    }
}
