package ru.practicum.collector.service.hub;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.collector.kafka.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class HubEventHandlerBase<P extends SpecificRecordBase> implements HubEventHandler {
    private static final String TOPIC = "telemetry.hubs.v1";

    private final KafkaEventProducer producer;

    protected abstract P getPayload(HubEventProto event);

    @Override
    public void handle(HubEventProto event) {
        String hubId = event.getHubId();
        Timestamp timestamp = event.getTimestamp();
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(instant)
                .setPayload(getPayload(event))
                .build();
        producer.send(TOPIC, instant, hubId, eventAvro);
    }
}
