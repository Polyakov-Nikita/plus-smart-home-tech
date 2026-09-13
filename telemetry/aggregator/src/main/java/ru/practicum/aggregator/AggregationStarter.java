package ru.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.configuration.consumer.ConsumerProperties;
import ru.practicum.aggregator.configuration.producer.ProducerProperties;
import ru.practicum.aggregator.service.AggregationService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final ConsumerProperties consumerProperties;
    private final ProducerProperties producerProperties;
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final AggregationService aggregationService;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(consumerProperties.getTopics());
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(consumerProperties.getAttemptTimeout());

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro eventAvro = (SensorEventAvro) record.value();
                    Optional<SensorsSnapshotAvro> snapshot = aggregationService.updateState(eventAvro);

                    if (snapshot.isPresent()) {
                        SensorsSnapshotAvro snap = snapshot.get();
                        ProducerRecord<String, SpecificRecordBase> producerRecord =
                                new ProducerRecord<>(producerProperties.getTopic(), null,
                                        snap.getTimestamp().toEpochMilli(), snap.getHubId(), snap);
                        producer.send(producerRecord);
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            log.info("wakeup exception");
        } catch (Exception e) {
            log.error("aggregation error", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                producer.close();
                consumer.close();
            }
        }
    }
}
