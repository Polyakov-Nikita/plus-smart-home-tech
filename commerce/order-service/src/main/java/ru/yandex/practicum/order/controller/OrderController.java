package ru.yandex.practicum.order.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.dto.CreateOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.service.OrderOrchestrationService;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;

@RestController
@Validated
@RequestMapping(path = "${controllers.order.base-path}")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class OrderController {
    private final OrderService orderService;
    private final OrderOrchestrationService orderOrchestrationService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderOrchestrationService.createOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrders());
    }

    @GetMapping("${controllers.order.by-email}")
    public ResponseEntity<List<OrderDto>> getOrdersByEmail(@RequestParam(value = "email") @Email String email) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrdersByEmail(email));
    }
}
