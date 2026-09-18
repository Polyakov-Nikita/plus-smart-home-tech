package ru.practicum.analyzer.configuration.consumer.snapshot;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SnapshotConsumerConfiguration {
    private final SnapshotConsumerProperties snapshotConsumerProperties;

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> snapshotConsumer() {
        return new KafkaConsumer<>(snapshotConsumerProperties.getProperties());
    }
}
