package ru.yandex.practicum.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.ReserveResponse;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;
import ru.yandex.practicum.inventory.entity.Inventory;
import ru.yandex.practicum.inventory.exception.*;
import ru.yandex.practicum.inventory.mapping.InventoryMapper;
import ru.yandex.practicum.inventory.repository.InventoryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Transactional
    public InventoryDto createInventory(UpdateInventoryRequest request) {
        checkInventoryExistence(request.productId());
        Inventory inventory = inventoryMapper.toInventory(request);
        initializeInventory(inventory);
        Inventory result = inventoryRepository.save(inventory);
        return inventoryMapper.toInventoryDto(result);
    }

    private void checkInventoryExistence(Long productId) {
        if (inventoryRepository.existsByProductId(productId)) {
            throw new InventoryAlreadyExistsException(productId);
        }
    }

    private void initializeInventory(Inventory inventory) {
        inventory.setReservedQuantity(0);
        inventory.setAvailableQuantity(inventory.getQuantity());
    }

    @Transactional
    public ReserveResponse reserveInventory(ReserveRequest request) {
        Inventory inventory = getInventoryBy(request.productId());
        Integer requestQuantity = request.quantity();
        checkAvailableQuantity(inventory.getAvailableQuantity(), requestQuantity);
        reserveQuantity(inventory, requestQuantity);
        return createResponse(inventory.getAvailableQuantity(), "product successfully reserved");
    }

    private Inventory getInventoryBy(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
    }

    private void checkAvailableQuantity(Integer availableQuantity, Integer needToReserve) {
        if (availableQuantity < needToReserve) {
            throw new NotEnoughAvailableQuantityException(availableQuantity, needToReserve);
        }
    }

    private void reserveQuantity(Inventory inventory, Integer quantity) {
        Integer availableQuantity = inventory.getAvailableQuantity() - quantity;
        inventory.setAvailableQuantity(availableQuantity);
        Integer reservedQuantity = inventory.getReservedQuantity() + quantity;
        inventory.setReservedQuantity(reservedQuantity);
    }

    private ReserveResponse createResponse(Integer availableQuantity, String message) {
        return new ReserveResponse(
                true,
                availableQuantity,
                message
        );
    }

    @Transactional
    public ReserveResponse releaseInventory(ReserveRequest request) {
        Inventory inventory = getInventoryBy(request.productId());
        Integer requestQuantity = request.quantity();
        checkReservedQuantity(inventory.getReservedQuantity(), requestQuantity);
        releaseQuantity(inventory, requestQuantity);
        return createResponse(inventory.getAvailableQuantity(), "product successfully released");
    }

    private void checkReservedQuantity(Integer reservedQuantity, Integer needToRelease) {
        if (reservedQuantity < needToRelease) {
            throw new NotEnoughReservedQuantityException(reservedQuantity, needToRelease);
        }
    }

    private void releaseQuantity(Inventory inventory, Integer quantity) {
        Integer availableQuantity = inventory.getAvailableQuantity() + quantity;
        inventory.setAvailableQuantity(availableQuantity);
        Integer reservedQuantity = inventory.getReservedQuantity() - quantity;
        inventory.setReservedQuantity(reservedQuantity);
    }

    @Transactional
    public InventoryDto updateInventory(UpdateInventoryRequest request) {
        Inventory inventory = getInventoryBy(request.productId());
        Integer quantityUpdate = request.quantity();
        Integer reservedQuantity = inventory.getReservedQuantity();
        checkQuantityUpdate(quantityUpdate, reservedQuantity);
        updateInventoryQuantity(inventory, quantityUpdate, reservedQuantity);
        return inventoryMapper.toInventoryDto(inventory);
    }

    private void updateInventoryQuantity(Inventory inventory, Integer quantityUpdate, Integer reservedQuantity) {
        inventory.setQuantity(quantityUpdate);
        Integer availableQuantity = quantityUpdate - reservedQuantity;
        inventory.setAvailableQuantity(availableQuantity);
    }

    private void checkQuantityUpdate(Integer quantityUpdate, Integer reservedQuantity) {
        if (quantityUpdate < reservedQuantity) {
            throw new NotEnoughTotalQuantityException(quantityUpdate, reservedQuantity);
        }
    }

    public List<InventoryDto> getInventories() {
        List<Inventory> result = inventoryRepository.findAll();
        return result.stream()
                .map(inventoryMapper::toInventoryDto)
                .toList();
    }

    public InventoryDto getInventoryByProductId(long productId) {
        Inventory result = getInventoryBy(productId);
        return inventoryMapper.toInventoryDto(result);
    }
}
