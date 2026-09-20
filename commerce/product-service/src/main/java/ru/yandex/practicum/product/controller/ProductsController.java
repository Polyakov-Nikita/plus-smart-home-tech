package ru.yandex.practicum.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;
import ru.yandex.practicum.product.service.ProductsService;

import java.util.List;

@RestController
@RequestMapping(path = "${controllers.products.base-path}")
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ProductsController {
    private final ProductsService productsService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productsService.createProduct(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable long id,
            @RequestBody @Valid UpdateProductRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productsService.updateProduct(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productsService.getProduct(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productsService.getProducts());
    }

    @GetMapping("${controllers.products.search}")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String query) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productsService.searchProducts(query));
    }

    @GetMapping("${controllers.products.categories}")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@PathVariable long categoryId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productsService.getProductsByCategory(categoryId));
    }
}
