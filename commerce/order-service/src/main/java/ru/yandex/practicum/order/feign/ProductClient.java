package ru.yandex.practicum.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.order.feign.dto.ProductDto;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("${controllers.products.base-path}" + "/{id}")
    ProductDto getProductById(@PathVariable("id") long id);
}
