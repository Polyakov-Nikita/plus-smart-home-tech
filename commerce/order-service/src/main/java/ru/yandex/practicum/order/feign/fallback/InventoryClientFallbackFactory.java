package ru.yandex.practicum.order.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.exception.InventoryServiceUnavailableException;
import ru.yandex.practicum.order.feign.InventoryClient;
import ru.yandex.practicum.order.feign.dto.ReserveRequest;
import ru.yandex.practicum.order.feign.dto.ReserveResponse;

@Component
@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {
    @Override
    public InventoryClient create(Throwable cause) {
        return new InventoryClient() {
            @Override
            public ReserveResponse reserveStock(ReserveRequest request) {
                Long productId = request.productId();
                log.warn("inventory-service is unavailable while reserving product. productId: {}", productId, cause);
                throw new InventoryServiceUnavailableException(productId, cause);
            }

            @Override
            public ReserveResponse releaseStock(ReserveRequest request) {
                Long productId = request.productId();
                log.warn("inventory-service is unavailable while releasing product. productId: {}", productId, cause);
                throw new InventoryServiceUnavailableException(productId, cause);
            }
        };
    }
}
