package ru.practicum.analyzer.configuration.consumer.hub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Properties;

@RequiredArgsConstructor
@ConfigurationProperties("kafka.consumer.hub")
@Getter
public class HubConsumerProperties {
    private final List<String> topics;
    private final Properties properties;
}
