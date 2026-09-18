package ru.practicum.aggregator.configuration.consumer;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ConsumerConfiguration {
    private final ConsumerProperties consumerProperties;

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> kafkaConsumer() {
        return new KafkaConsumer<>(consumerProperties.getProperties());
    }
}
