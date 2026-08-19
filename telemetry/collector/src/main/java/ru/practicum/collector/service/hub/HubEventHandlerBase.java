package ru.practicum.collector.service.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.practicum.collector.dto.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@RequiredArgsConstructor
public abstract class HubEventHandlerBase<P extends SpecificRecordBase> implements HubEventHandler {
    private static final String TOPIC = "telemetry.hubs.v1";

    protected final Producer<String, SpecificRecordBase> producer;

    protected abstract P getPayload(HubEvent event);

    @Override
    public void handle(HubEvent event) {
        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(getPayload(event))
                .build();
        producer.send(new ProducerRecord<>(TOPIC, eventAvro));
    }
}
