package ru.yandex.practicum.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.order.feign.dto.ReserveRequest;
import ru.yandex.practicum.order.feign.dto.ReserveResponse;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @PostMapping("${controllers.inventory.base-path}" + "${controllers.inventory.reserve}")
    ReserveResponse reserveStock(@RequestBody ReserveRequest request);

    @PostMapping("${controllers.inventory.base-path}" + "${controllers.inventory.release}")
    ReserveResponse releaseStock(@RequestBody ReserveRequest request);
}
