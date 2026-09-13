package ru.practicum.aggregator.configuration.producer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

@RequiredArgsConstructor
@ConfigurationProperties("kafka.producer")
@Getter
public class ProducerProperties {
    private final String topic;
    private final Properties properties;
}
