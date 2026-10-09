package ru.yandex.practicum.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.yandex.practicum.gateway.config.PathProperties;
import ru.yandex.practicum.gateway.config.UsersProperties;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
@SuppressWarnings("unused")
public class SecurityConfigurationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private PathProperties pathProperties;
    @Autowired
    private UsersProperties usersProperties;

    private UsersProperties.UserProperty ivan;
    private UsersProperties.UserProperty anna;

    @BeforeEach
    public void setUp() {
        ivan = getUserByName("ivan");
        anna = getUserByName("anna");
    }

    @Test
    public void getProducts_withoutAuth_Ok() {
        exchange(webTestClient.get(), pathProperties.getProductsBasePath())
                .expectStatus().isOk();
    }

    @Test
    public void postOrders_withoutAuth_Unauthorized() {
        exchange(webTestClient.post(), pathProperties.getOrderBasePath())
                .expectStatus().isUnauthorized();
    }

    @Test
    public void postOrders_withUserAuth_Ok() {
        exchange(webTestClient.post(), pathProperties.getOrderBasePath(), ivan)
                .expectStatus().isOk();
    }

    @Test
    public void patchProducts_withUserAuth_Forbidden() {
        exchange(webTestClient.patch(), pathProperties.getProductsBasePath(), ivan)
                .expectStatus().isForbidden();
    }

    @Test
    public void patchProducts_withAdminAuth_Ok() {
        exchange(webTestClient.patch(), pathProperties.getProductsBasePath(), anna)
                .expectStatus().isOk();
    }

    @Test
    public void getOrders_withUserAuth_Forbidden() {
        exchange(webTestClient.get(), pathProperties.getOrderBasePath(), ivan)
                .expectStatus().isForbidden();
    }

    @Test
    public void getOrders_withAdminAuth_Ok() {
        exchange(webTestClient.get(), pathProperties.getOrderBasePath(), anna)
                .expectStatus().isOk();
    }

    @Test
    public void unknownRoute_withAdminAuth_Forbidden() {
        exchange(webTestClient.get(), "/api/unknown", anna)
                .expectStatus().isForbidden();
    }

    @Test
    public void preflight_Ok() {
        exchangeOptions()
                .expectStatus().isOk();
    }

    private UsersProperties.UserProperty getUserByName(String username) {
        return usersProperties.getUsers().stream()
                .filter(userProperty -> userProperty.username().equals(username))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("User '" + username + "' not found in test config"));
    }

    private WebTestClient.ResponseSpec exchange(
            WebTestClient.RequestHeadersUriSpec<?> method,
            String uri
    ) {
        return method
                .uri(uri)
                .exchange();
    }

    private WebTestClient.ResponseSpec exchange(
            WebTestClient.RequestHeadersUriSpec<?> method,
            String uri,
            UsersProperties.UserProperty user
    ) {
        return method
                .uri(uri)
                .headers(headers -> headers.setBasicAuth(user.username(), user.password()))
                .exchange();
    }

    private WebTestClient.ResponseSpec exchangeOptions() {
        return webTestClient.options()
                .uri("http://localhost:8443/somewhere")
                .header("Origin", "http://localhost:8443")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "authorization, content-type")
                .exchange();
    }

    @TestConfiguration
    public static class TestBackendConfiguration {
        @Bean
        public RouterFunction<ServerResponse> testBackendRoutes() {
            return RouterFunctions.route(
                    RequestPredicates.path("/**"),
                    request -> ServerResponse.ok().build()
            );
        }
    }
}
