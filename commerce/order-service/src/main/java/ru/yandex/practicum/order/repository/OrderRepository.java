package ru.yandex.practicum.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.order.entity.Order;
import ru.yandex.practicum.order.exception.NotFoundException;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    default Order findBy(long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("order", id));
    }

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items")
    List<Order> findAllOrders();

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.customerEmail = :email")
    List<Order> findAllByEmail(@Param("email") String email);
}
