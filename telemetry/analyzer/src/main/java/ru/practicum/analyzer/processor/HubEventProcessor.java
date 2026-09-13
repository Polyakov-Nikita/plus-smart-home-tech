package ru.practicum.analyzer.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.configuration.consumer.ConsumerProperties;
import ru.practicum.analyzer.configuration.consumer.hub.HubConsumerProperties;
import ru.practicum.analyzer.service.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private final ConsumerProperties consumerProperties;
    private final HubConsumerProperties hubConsumerProperties;
    private final KafkaConsumer<String, SpecificRecordBase> hubConsumer;
    private final Map<Class<?>, HubEventHandler> hubEventHandlerMap;

    @Autowired
    public HubEventProcessor(
            ConsumerProperties consumerProperties,
            HubConsumerProperties hubConsumerProperties,
            KafkaConsumer<String, SpecificRecordBase> hubConsumer,
            Set<HubEventHandler> hubEventHandlers
    ) {
        this.consumerProperties = consumerProperties;
        this.hubConsumerProperties = hubConsumerProperties;
        this.hubConsumer = hubConsumer;
        this.hubEventHandlerMap = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getPayloadClass,
                        Function.identity()
                ));
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));
        try {
            hubConsumer.subscribe(hubConsumerProperties.getTopics());
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = hubConsumer.poll(consumerProperties.getAttemptTimeout());
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    handle(record);
                }
                hubConsumer.commitSync();
            }
        } catch (Exception e) {
            log.error("HubProcessor error: ", e);
        } finally {
            hubConsumer.close();
        }
    }

    private void handle(ConsumerRecord<String, SpecificRecordBase> record) {
        HubEventAvro event = (HubEventAvro) record.value();
        Object payload = event.getPayload();
        if (payload == null) {
            throw new IllegalArgumentException("payload is null. event: " + event);
        }
        Class<?> payloadClass = payload.getClass();
        if (hubEventHandlerMap.containsKey(payloadClass)) {
            hubEventHandlerMap.get(payloadClass).handle(event);
        } else {
            throw new IllegalArgumentException("handler not found. payloadClass: " + payloadClass);
        }
    }
}
