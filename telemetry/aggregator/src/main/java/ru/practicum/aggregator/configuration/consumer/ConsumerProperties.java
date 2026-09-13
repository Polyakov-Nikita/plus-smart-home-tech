package ru.practicum.aggregator.configuration.consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@RequiredArgsConstructor
@ConfigurationProperties("kafka.consumer")
@Getter
public class ConsumerProperties {
    private final List<String> topics;
    private final Properties properties;
    private final long attemptTimeoutMillis;

    public Duration getAttemptTimeout() {
        return Duration.ofMillis(attemptTimeoutMillis);
    }
}
