package ru.yandex.practicum.inventory.mapping;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;
import ru.yandex.practicum.inventory.entity.Inventory;

@Component
public class InventoryMapper {
    public Inventory toInventory(UpdateInventoryRequest request) {
        return Inventory.builder()
                .productId(request.productId())
                .quantity(request.quantity())
                .build();
    }

    public InventoryDto toInventoryDto(Inventory inventory) {
        return new InventoryDto(
                inventory.getId(),
                inventory.getProductId(),
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                inventory.getAvailableQuantity()
        );
    }
}
