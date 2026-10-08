package ru.yandex.practicum.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "controllers")
public class PathProperties {
    private final Categories categories;
    private final Products products;
    private final Inventory inventory;
    private final Order order;

    public String getCategoriesBasePath() {
        return categories.basePath;
    }

    public String getProductsBasePath() {
        return products.basePath;
    }

    public String getInventoryBasePath() {
        return inventory.basePath;
    }

    public String getOrderBasePath() {
        return order.basePath;
    }

    public String getOrderByEmail() {
        return order.byEmail;
    }

    public String getOrderById() {
        return order.byId;
    }

    public record Categories(String basePath) {
    }

    public record Products(String basePath) {
    }

    public record Inventory(String basePath) {
    }

    public record Order(String basePath, String byEmail, String byId) {
    }
}
