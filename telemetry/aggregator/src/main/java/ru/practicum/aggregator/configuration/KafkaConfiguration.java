package ru.practicum.aggregator.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@AllArgsConstructor
@ConfigurationProperties("kafka")
@SuppressWarnings("unused")
public class KafkaConfiguration {
    @Getter
    private List<String> topics;
    @Getter
    private String producerTopic;
    private Properties consumerProperties;
    private Properties producerProperties;
    private long consumeAttemptTimeoutMillis;

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> kafkaConsumer() {
        return new KafkaConsumer<>(consumerProperties);
    }

    @Bean
    public KafkaProducer<String, SpecificRecordBase> kafkaProducer() {
        return new KafkaProducer<>(producerProperties);
    }

    public Duration getConsumeAttemptTimeout() {
        return Duration.ofMillis(consumeAttemptTimeoutMillis);
    }
}
