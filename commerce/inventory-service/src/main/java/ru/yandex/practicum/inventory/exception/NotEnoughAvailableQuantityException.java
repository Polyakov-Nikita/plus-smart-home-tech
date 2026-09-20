package ru.yandex.practicum.inventory.exception;

import lombok.Getter;

@Getter
public class NotEnoughAvailableQuantityException extends RuntimeException {
    private final Integer availableQuantity;
    private final Integer needToReserve;

    public NotEnoughAvailableQuantityException(Integer availableQuantity, Integer needToReserve) {
        super(
                String.format(
                        "not enough quantity to reserve. availableQuantity: '%d', needToReserve: '%d'",
                        availableQuantity,
                        needToReserve
                )
        );
        this.availableQuantity = availableQuantity;
        this.needToReserve = needToReserve;
    }
}
