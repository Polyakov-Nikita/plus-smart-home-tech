package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.order.dto.CreateOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.OrderStatus;
import ru.yandex.practicum.order.entity.Order;
import ru.yandex.practicum.order.entity.OrderItem;
import ru.yandex.practicum.order.mapping.OrderMapper;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        Order order = orderMapper.toOrder(request);
        initialize(order);
        Order result = orderRepository.save(order);
        return orderMapper.toOrderDto(result);
    }

    private void initialize(Order order) {
        order.setStatus(OrderStatus.CREATED);
        order.setTotalPrice(calculateTotalPriceOf(order.getItems()));
        order.setStatusDetails("string");
        order.setCreatedAt(LocalDateTime.now());
        linkEvents(order);
    }

    private BigDecimal calculateTotalPriceOf(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void linkEvents(Order order) {
        order.getItems().forEach((item) -> item.setOrder(order));
    }

    public OrderDto getOrderById(long id) {
        Order result = orderRepository.findBy(id);
        return orderMapper.toOrderDto(result);
    }

    public List<OrderDto> getOrders() {
        List<Order> result = orderRepository.findAllOrders();
        return result.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    public List<OrderDto> getOrdersByEmail(String email) {
        List<Order> result = orderRepository.findAllByEmail(email);
        return result.stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }
}
