package ru.yandex.practicum.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SecurityConfiguration {
    private static final String ROLE_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String URL_ANY = "/**";

    private final PathProperties pathProperties;
    private final UsersProperties usersProperties;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .authorizeExchange(
                        exchange -> exchange
                                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                .pathMatchers(
                                        HttpMethod.GET,
                                        pathProperties.getProductsBasePath() + URL_ANY,
                                        pathProperties.getCategoriesBasePath() + URL_ANY,
                                        pathProperties.getInventoryBasePath() + URL_ANY
                                ).permitAll()
                                .pathMatchers(
                                        HttpMethod.POST,
                                        pathProperties.getOrderBasePath() + URL_ANY
                                ).hasRole(ROLE_USER)
                                .pathMatchers(
                                        HttpMethod.GET,
                                        pathProperties.getOrderBasePath() + pathProperties.getOrderByEmail(),
                                        pathProperties.getOrderBasePath() + pathProperties.getOrderById()
                                ).hasRole(ROLE_USER)
                                .pathMatchers(
                                        HttpMethod.GET,
                                        pathProperties.getOrderBasePath()
                                ).hasRole(ROLE_ADMIN)
                                .pathMatchers(
                                        pathProperties.getProductsBasePath() + URL_ANY,
                                        pathProperties.getCategoriesBasePath() + URL_ANY,
                                        pathProperties.getInventoryBasePath() + URL_ANY
                                ).hasRole(ROLE_ADMIN)
                                .anyExchange().denyAll()
                )
                .httpBasic(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    MapReactiveUserDetailsService mapReactiveUserDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails[] details = usersProperties.getUsers().stream()
                .map(
                        user -> User.builder()
                                .username(user.username())
                                .password(passwordEncoder.encode(user.password()))
                                .roles(user.roles().toArray(String[]::new))
                                .build()
                )
                .toArray(UserDetails[]::new);
        return new MapReactiveUserDetailsService(details);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
