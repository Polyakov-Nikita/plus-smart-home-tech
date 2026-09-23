package ru.yandex.practicum.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;
import ru.yandex.practicum.product.entity.Category;
import ru.yandex.practicum.product.entity.Product;
import ru.yandex.practicum.product.exception.NotFoundException;
import ru.yandex.practicum.product.mapping.ProductMapper;
import ru.yandex.practicum.product.repository.CategoryRepository;
import ru.yandex.practicum.product.repository.ProductRepository;

import java.util.List;
import java.util.function.Consumer;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductsService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        Category category = getCategory(request);
        Product product = createProductEntity(request, category);
        Product result = productRepository.save(product);
        return productMapper.toProductDto(result);
    }

    private Category getCategory(CreateProductRequest request) {
        Long categoryId = request.categoryId();
        if (categoryId != null) {
            return categoryRepository.findBy(categoryId);
        }
        return null;
    }

    private Product createProductEntity(CreateProductRequest request, Category category) {
        Product productEntity = productMapper.toProduct(request);
        productEntity.setCategory(category);
        productEntity.setActive(true);
        return productEntity;
    }

    @Transactional
    public ProductDto updateProduct(long id, UpdateProductRequest request) {
        Product product = productRepository.findBy(id);
        updateProductFields(product, request);
        return productMapper.toProductDto(product);
    }

    private void updateProductFields(Product product, UpdateProductRequest request) {
        updateValue(request.name(), product::setName);
        updateValue(request.description(), product::setDescription);
        updateValue(request.price(), product::setPrice);
        updateValue(request.categoryId(), (categoryIdUpdate) -> {
            Category productCategory = product.getCategory();
            if (productCategory == null || !productCategory.getId().equals(categoryIdUpdate)) {
                Category categoryUpdate = categoryRepository.findBy(categoryIdUpdate);
                product.setCategory(categoryUpdate);
            }
        });
        updateValue(request.imageUrl(), product::setImageUrl);
        updateValue(request.active(), product::setActive);
    }

    private <V> void updateValue(V value, Consumer<V> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    public ProductDto getProduct(long id) {
        Product result = productRepository.findBy(id);
        return productMapper.toProductDto(result);
    }

    public List<ProductDto> getProducts() {
        return productRepository.findAllProducts().stream()
                .map(productMapper::toProductDto)
                .toList();
    }

    public List<ProductDto> searchProducts(String query) {
        if (query.isBlank()) {
            return List.of();
        }
        return productRepository.findByNameContaining(query.toLowerCase()).stream()
                .map(productMapper::toProductDto)
                .toList();
    }

    public List<ProductDto> getProductsByCategory(long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("category", categoryId);
        }
        return productRepository.findByCategoryId(categoryId).stream()
                .map(productMapper::toProductDto)
                .toList();
    }
}
