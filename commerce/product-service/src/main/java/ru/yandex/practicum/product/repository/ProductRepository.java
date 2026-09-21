package ru.yandex.practicum.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.product.entity.Product;
import ru.yandex.practicum.product.exception.NotFoundException;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    default Product findBy(long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("product", id));
    }

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.active = true")
    List<Product> findAllProducts();

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE LOWER(p.name) LIKE %:query% AND p.active = true")
    List<Product> findByNameContaining(@Param("query") String query);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.category.id = :categoryId AND p.active = true")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
}
