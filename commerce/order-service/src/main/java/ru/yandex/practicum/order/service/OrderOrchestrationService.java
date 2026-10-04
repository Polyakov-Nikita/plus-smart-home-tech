package ru.yandex.practicum.order.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.order.dto.*;
import ru.yandex.practicum.order.exception.InventoryServiceUnavailableException;
import ru.yandex.practicum.order.exception.OrderProcessingException;
import ru.yandex.practicum.order.exception.ProductServiceUnavailableException;
import ru.yandex.practicum.order.feign.InventoryClient;
import ru.yandex.practicum.order.feign.ProductClient;
import ru.yandex.practicum.order.feign.dto.ProductDto;
import ru.yandex.practicum.order.feign.dto.ReserveRequest;
import ru.yandex.practicum.order.feign.dto.ReserveResponse;
import ru.yandex.practicum.order.feign.fallback.ServiceCallResult;
import ru.yandex.practicum.order.service.dto.ItemData;
import ru.yandex.practicum.order.service.dto.OrderData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class OrderOrchestrationService {
    private final OrderService orderService;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public OrderDto createOrder(CreateOrderRequest request) {
        AtomicBoolean isProductDegraded = new AtomicBoolean(false);
        AtomicBoolean isInventoryDegraded = new AtomicBoolean(false);
        List<ItemData> itemsData = createItemsData(request.items());
        itemsData = addProductInfo(itemsData, isProductDegraded);
        if (isProductDegraded.get()) {
            OrderData orderData = createOrderData(request, itemsData);
            return orderService.createOrder(orderData, createStatusInfo(isProductDegraded));
        }
        reserveItems(itemsData, isInventoryDegraded);
        OrderData orderData = createOrderData(request, itemsData);
        return orderService.createOrder(orderData, createStatusInfo(isInventoryDegraded));
    }

    private List<ItemData> createItemsData(List<OrderItemRequest> orderItems) {
        Map<Long, ItemData> idItems = new HashMap<>();
        orderItems.forEach(
                orderItem ->
                        idItems.merge(
                                orderItem.productId(),
                                new ItemData(orderItem.productId(), null, orderItem.quantity(), null),
                                (existing, value) ->
                                        new ItemData(
                                                existing.productId(),
                                                null,
                                                existing.quantity() + value.quantity(),
                                                null
                                        )
                        )
        );
        return new ArrayList<>(idItems.values());
    }

    private List<ItemData> addProductInfo(List<ItemData> itemsData, AtomicBoolean isProductDegraded) {
        List<ItemData> itemsDataUpdate = new ArrayList<>();
        itemsData.forEach(itemData -> {
            ItemData itemDataUpdate = createItemDataUpdate(itemData, isProductDegraded);
            itemsDataUpdate.add(itemDataUpdate);
        });
        return itemsDataUpdate;
    }

    private ItemData createItemDataUpdate(ItemData itemData, AtomicBoolean isProductDegraded) {
        Long productId = itemData.productId();
        ProductDto productDto = getProduct(productId, isProductDegraded);
        checkProductActivity(productDto);
        return new ItemData(
                productId,
                productDto.name(),
                itemData.quantity(),
                productDto.price()
        );
    }

    private ProductDto getProduct(Long productId, AtomicBoolean isProductDegraded) {
        ServiceCallResult<ProductDto> callResult = createGetProductCallResult(productId);
        return switch (callResult) {
            case ServiceCallResult.Success<ProductDto> success -> success.value();
            case ServiceCallResult.Failure<ProductDto> failure ->
                    throw new OrderProcessingException(String.format("product not found. id: '%d'", productId));
            case ServiceCallResult.Degraded<ProductDto> degraded -> {
                isProductDegraded.set(true);
                yield createFallbackProduct(productId);
            }
        };
    }

    private ProductDto createFallbackProduct(Long productId) {
        return new ProductDto(
                productId,
                "name placeholder",
                "description placeholder",
                BigDecimal.ONE,
                true
        );
    }

    private ServiceCallResult<ProductDto> createGetProductCallResult(Long productId) {
        try {
            return new ServiceCallResult.Success<>(
                    productClient.getProductById(productId)
            );
        } catch (ProductServiceUnavailableException e) {
            return new ServiceCallResult.Degraded<>(
                    "catalog is unavailable"
            );
        }
    }

    private void checkProductActivity(ProductDto productDto) {
        if (!productDto.active()) {
            throw new OrderProcessingException("product deactivated");
        }
    }

    private void reserveItems(List<ItemData> itemsData, AtomicBoolean isInventoryDegraded) {
        List<ReserveRequest> requestHistory = new ArrayList<>();
        for (ItemData itemData : itemsData) {
            if (isInventoryDegraded.get()) {
                break;
            }
            reserveItem(itemData, requestHistory, isInventoryDegraded);
        }
    }

    private void reserveItem(ItemData itemData, List<ReserveRequest> requestHistory, AtomicBoolean isInventoryDegraded) {
        ReserveRequest reserveRequest = new ReserveRequest(itemData.productId(), itemData.quantity());
        reserveStock(reserveRequest, requestHistory, isInventoryDegraded);
        requestHistory.add(reserveRequest);
    }

    private void reserveStock(ReserveRequest reserveRequest, List<ReserveRequest> requestHistory, AtomicBoolean isInventoryDegraded) {
        ServiceCallResult<ReserveResponse> callResult = createReserveStockCallResult(reserveRequest);
        switch (callResult) {
            case ServiceCallResult.Success<ReserveResponse> success -> {
            }
            case ServiceCallResult.Failure<ReserveResponse> failure -> {
                undoReserves(requestHistory);
                throw new OrderProcessingException(String.format("reserving error: %s", failure.message()));
            }
            case ServiceCallResult.Degraded<ReserveResponse> degraded -> {
                isInventoryDegraded.set(true);
            }
        }
    }

    private ServiceCallResult<ReserveResponse> createReserveStockCallResult(ReserveRequest reserveRequest) {
        try {
            return new ServiceCallResult.Success<>(inventoryClient.reserveStock(reserveRequest));
        } catch (InventoryServiceUnavailableException e) {
            return new ServiceCallResult.Degraded<>(
                    "inventory is unavailable"
            );
        }
    }

    private void undoReserves(List<ReserveRequest> requestHistory) {
        try {
            requestHistory.forEach(request -> {
                ReserveRequest releaseRequest = new ReserveRequest(request.productId(), request.quantity());
                releaseStock(releaseRequest);
            });
        } catch (FeignException e) {
            throw new OrderProcessingException("failed to undo reserves");
        }
    }

    private void releaseStock(ReserveRequest reserveRequest) {
        ServiceCallResult<ReserveResponse> callResult = createReleaseStockCallResult(reserveRequest);
        switch (callResult) {
            case ServiceCallResult.Success<ReserveResponse> success -> {
            }
            case ServiceCallResult.Failure<ReserveResponse> failure ->
                    throw new OrderProcessingException(String.format("releasing failure: %s", failure.message()));
            case ServiceCallResult.Degraded<ReserveResponse> degraded ->
                    throw new OrderProcessingException("unexpected releasing error");
        }
    }

    private ServiceCallResult<ReserveResponse> createReleaseStockCallResult(ReserveRequest reserveRequest) {
        try {
            return new ServiceCallResult.Success<>(inventoryClient.releaseStock(reserveRequest));
        } catch (InventoryServiceUnavailableException e) {
            return new ServiceCallResult.Degraded<>(
                    "inventory is unavailable"
            );
        }
    }

    private OrderData createOrderData(CreateOrderRequest request, List<ItemData> items) {
        return new OrderData(
                request.customerName(),
                request.customerEmail(),
                items
        );
    }

    private OrderStatusInfo createStatusInfo(AtomicBoolean isDegraded) {
        if (isDegraded.get()) {
            return new OrderStatusInfo(OrderStatus.PENDING_CONFIRMATION, "order is awaiting confirmation");
        }
        return new OrderStatusInfo(OrderStatus.CONFIRMED, "successfully created order");
    }
}
