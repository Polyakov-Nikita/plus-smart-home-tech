package ru.yandex.practicum.inventory.exception;

import lombok.Getter;

@Getter
public class NotEnoughTotalQuantityException extends RuntimeException {
    private final Integer totalQuantity;
    private final Integer reserveQuantity;

    public NotEnoughTotalQuantityException(Integer totalQuantity, Integer reserveQuantity) {
        super(
                String.format(
                        "total quantity are less than reserve. totalQuantity: '%d', reserveQuantity: '%d'",
                        totalQuantity,
                        reserveQuantity
                )
        );
        this.totalQuantity = totalQuantity;
        this.reserveQuantity = reserveQuantity;
    }
}
