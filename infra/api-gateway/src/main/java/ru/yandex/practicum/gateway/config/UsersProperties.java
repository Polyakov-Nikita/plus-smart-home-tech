package ru.yandex.practicum.gateway.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "security")
@Getter
public class UsersProperties {
    private final List<UserProperty> users;

    public record UserProperty(String username, String password, List<String> roles) {
    }
}
