package ru.yandex.practicum.product.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String entityType;
    private final long id;

    public NotFoundException(String entityType, long id) {
        super(String.format("%s not found, id: %d", entityType, id));
        this.entityType = entityType;
        this.id = id;
    }
}