package ru.yandex.practicum.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.ReserveResponse;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;
import ru.yandex.practicum.inventory.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping(path = "${controllers.inventory.base-path}")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryDto> createInventory(@RequestBody @Valid UpdateInventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(request));
    }

    @PostMapping("${controllers.inventory.reserve}")
    public ResponseEntity<ReserveResponse> reserveInventory(@RequestBody @Valid ReserveRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.reserveInventory(request));
    }

    @PostMapping("${controllers.inventory.release}")
    public ResponseEntity<ReserveResponse> releaseInventory(@RequestBody @Valid ReserveRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.releaseInventory(request));
    }

    @PutMapping
    public ResponseEntity<InventoryDto> updateInventory(@RequestBody @Valid UpdateInventoryRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.updateInventory(request));
    }

    @GetMapping
    public ResponseEntity<List<InventoryDto>> getInventories() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.getInventories());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDto> getInventoryByProductId(@PathVariable long productId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(inventoryService.getInventoryByProductId(productId));
    }
}
