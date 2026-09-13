package ru.practicum.aggregator.configuration.producer;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class ProducerConfiguration {
    private final ProducerProperties producerProperties;

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaProducer() {
        return new KafkaProducer<>(producerProperties.getProperties());
    }
}
