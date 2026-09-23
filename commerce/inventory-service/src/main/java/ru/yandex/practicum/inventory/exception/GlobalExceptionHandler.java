package ru.yandex.practicum.inventory.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {
    @ExceptionHandler(InventoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleInventoryNotFound(InventoryNotFoundException e) {
        log.warn("Ресурс не найден: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInsufficientStock(InsufficientStockException e) {
        log.warn("Недостаточно товара: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    /**
     * Конфликт оптимистичной блокировки: два запроса одновременно изменили одну запись.
     * Клиент должен повторить запрос.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOptimisticLock(ObjectOptimisticLockingFailureException e) {
        log.warn("Конфликт конкурентного доступа: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(),
                "Конфликт конкурентного доступа. Данные были изменены другим запросом. Повторите операцию.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Некорректный запрос: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        log.warn("Ошибка валидации: {}", errors);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Ошибка валидации", errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception e) {
        log.error("Внутренняя ошибка сервера", e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Внутренняя ошибка сервера");
    }

    @ExceptionHandler(InventoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInventoryAlreadyExists(InventoryAlreadyExistsException e) {
        log.error("Продукт уже добавлен на склад: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    @ExceptionHandler(NotEnoughAvailableQuantityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotEnoughAvailableQuantity(NotEnoughAvailableQuantityException e) {
        log.error("Для резервирования недостаточно товара: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    @ExceptionHandler(NotEnoughReservedQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotEnoughReservedQuantity(NotEnoughReservedQuantityException e) {
        log.error("Не достаточно зарезервированных товаров для снятия резерва: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(NotEnoughTotalQuantityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotEnoughTotalQuantity(NotEnoughTotalQuantityException e) {
        log.error("Новое количество товара меньше уже зарезервированного: {}", e.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
    }
}