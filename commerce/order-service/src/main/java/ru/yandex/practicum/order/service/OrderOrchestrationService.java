package ru.yandex.practicum.order.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.order.dto.CreateOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.OrderItemRequest;
import ru.yandex.practicum.order.exception.OrderProcessingException;
import ru.yandex.practicum.order.feign.InventoryClient;
import ru.yandex.practicum.order.feign.ProductClient;
import ru.yandex.practicum.order.feign.dto.ProductDto;
import ru.yandex.practicum.order.feign.dto.ReserveRequest;
import ru.yandex.practicum.order.service.dto.ItemData;
import ru.yandex.practicum.order.service.dto.OrderData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderOrchestrationService {
    private final OrderService orderService;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public OrderDto createOrder(CreateOrderRequest request) {
        List<ItemData> itemsData = createItemsData(request.items());
        itemsData = addProductInfo(itemsData);
        reserveItems(itemsData);
        OrderData orderData = createOrderData(request, itemsData);
        return orderService.createOrder(orderData);
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

    private List<ItemData> addProductInfo(List<ItemData> itemsData) {
        List<ItemData> itemsDataUpdate = new ArrayList<>();
        itemsData.forEach(itemData -> {
            ItemData itemDataUpdate = createItemDataUpdate(itemData);
            itemsDataUpdate.add(itemDataUpdate);
        });
        return itemsDataUpdate;
    }

    private ItemData createItemDataUpdate(ItemData itemData) {
        try {
            Long productId = itemData.productId();
            ProductDto productDto = productClient.getProductById(productId);
            checkProductActivity(productDto);
            return new ItemData(
                    productId,
                    productDto.name(),
                    itemData.quantity(),
                    productDto.price()
            );
        } catch (FeignException e) {
            throw mapProductException(e.status(), itemData.productId());
        }
    }

    private void checkProductActivity(ProductDto productDto) {
        if (!productDto.active()) {
            throw new OrderProcessingException("product deactivated");
        }
    }

    private OrderProcessingException mapProductException(Integer status, Long productId) {
        if (status == 404) {
            return new OrderProcessingException(String.format("product not found. id: '%d'", productId));
        }
        return new OrderProcessingException("failed to get product data");
    }

    private void reserveItems(List<ItemData> itemsData) {
        List<ReserveRequest> requestHistory = new ArrayList<>();
        itemsData.forEach(itemData -> reserveItem(itemData, requestHistory));
    }

    private void reserveItem(ItemData itemData, List<ReserveRequest> requestHistory) {
        try {
            ReserveRequest reserveRequest = new ReserveRequest(itemData.productId(), itemData.quantity());
            inventoryClient.reserveStock(reserveRequest);
            requestHistory.add(reserveRequest);
        } catch (FeignException e) {
            undoReserves(requestHistory);
            throw mapInventoryException(e.status(), itemData.productId());
        }
    }

    private void undoReserves(List<ReserveRequest> requestHistory) {
        try {
            requestHistory.forEach(request -> {
                ReserveRequest releaseRequest = new ReserveRequest(request.productId(), request.quantity());
                inventoryClient.releaseStock(releaseRequest);
            });
        } catch (FeignException e) {
            throw new OrderProcessingException("failed to undo reserves");
        }
    }

    private OrderProcessingException mapInventoryException(Integer status, Long productId) {
        if (status == 404) {
            return new OrderProcessingException(String.format("inventory not found. productId: '%d'", productId));
        }
        if (status == 409) {
            return new OrderProcessingException(String.format("not enough product. productId: '%d'", productId));
        }
        return new OrderProcessingException("failed to reserve product");
    }

    private OrderData createOrderData(CreateOrderRequest request, List<ItemData> items) {
        return new OrderData(
                request.customerName(),
                request.customerEmail(),
                items
        );
    }
}
