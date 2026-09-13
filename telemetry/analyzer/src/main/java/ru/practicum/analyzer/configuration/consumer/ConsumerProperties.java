package ru.practicum.analyzer.configuration.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@RequiredArgsConstructor
@ConfigurationProperties("kafka.consumer")
public class ConsumerProperties {
    private final long attemptTimeoutMillis;

    public Duration getAttemptTimeout() {
        return Duration.ofMillis(attemptTimeoutMillis);
    }
}
