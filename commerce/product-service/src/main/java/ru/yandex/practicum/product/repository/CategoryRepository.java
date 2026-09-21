package ru.yandex.practicum.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.product.entity.Category;
import ru.yandex.practicum.product.exception.NotFoundException;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    default Category findBy(long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("category", id));
    }
}
