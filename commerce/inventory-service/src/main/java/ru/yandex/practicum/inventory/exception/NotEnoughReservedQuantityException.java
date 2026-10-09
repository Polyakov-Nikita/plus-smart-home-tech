package ru.yandex.practicum.inventory.exception;

import lombok.Getter;

@Getter
public class NotEnoughReservedQuantityException extends RuntimeException {
    private final Integer reservedQuantity;
    private final Integer needToRelease;

    public NotEnoughReservedQuantityException(Integer reservedQuantity, Integer needToRelease) {
        super(
                String.format(
                        "not enough quantity to release. reservedQuantity: '%d', needToRelease: '%d'",
                        reservedQuantity,
                        needToRelease
                )
        );
        this.reservedQuantity = reservedQuantity;
        this.needToRelease = needToRelease;
    }
}
