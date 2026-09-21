package ru.yandex.practicum.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "inventories")
public class Inventory {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
