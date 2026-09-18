package ru.practicum.analyzer.configuration.consumer.snapshot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Properties;

@RequiredArgsConstructor
@ConfigurationProperties("kafka.consumer.snapshot")
@Getter
public class SnapshotConsumerProperties {
    private final List<String> topics;
    private final Properties properties;
}
