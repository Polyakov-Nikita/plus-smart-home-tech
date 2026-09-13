package ru.practicum.analyzer.configuration.consumer.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class HubConsumerConfiguration {
    private final HubConsumerProperties hubConsumerProperties;

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> hubConsumer() {
        return new KafkaConsumer<>(hubConsumerProperties.getProperties());
    }
}
